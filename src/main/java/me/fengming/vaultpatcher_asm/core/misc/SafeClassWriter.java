package me.fengming.vaultpatcher_asm.core.misc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Ceroler
 */
public class SafeClassWriter extends ClassWriter {

    private static final String OBJECT = "java/lang/Object";
    private static final String CLONEABLE = "java/lang/Cloneable";
    private static final String SERIALIZABLE = "java/io/Serializable";
    private final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    private class ClassInfo {
        private final String className;
        private final String superName;
        private final List<String> interfaces;
        private final boolean isInterface;
        public ClassInfo(String name, String suName, List<String> itfs, boolean isItf) {
                this.className = name;
                this.superName = suName;
                this.interfaces = itfs;
                this.isInterface = isItf;            }

            protected boolean isAssignableFrom(ClassInfo anotherInfo) {
                if (this.className.equals(anotherInfo.className)) return true;
                if (isArray(anotherInfo.className)) {
                    return this.isArrayAssignableFrom(anotherInfo);
                }
                if (anotherInfo.superName != null) {
                    ClassInfo superInfo = resolve(anotherInfo.superName);
                    if (superInfo != null && this.isAssignableFrom(superInfo)) {
                        return true;
                    }
                }
                for (String itf : anotherInfo.interfaces) {
                    ClassInfo itfInfo = resolve(itf);
                    if (itfInfo != null && this.isAssignableFrom(itfInfo)) {
                        return true;
                    }
                }
                return false;
        }

        private boolean isArray(String name) {
            return name != null && !name.isEmpty() && name.charAt(0) == '[';
        }

        private boolean isArrayAssignableFrom(ClassInfo anotherInfo) {
            if (this.className.equals(OBJECT) || this.className.equals(CLONEABLE) || this.className.equals(SERIALIZABLE)) return true;
            if (!isArray(this.className)) return false;
            Type ae = Type.getType(this.className.substring(1));
            Type be = Type.getType(anotherInfo.className.substring(1));
            if ((ae.getSort() != Type.OBJECT && ae.getSort() != Type.ARRAY) || (be.getSort() != Type.OBJECT && be.getSort() != Type.ARRAY)) {
                return ae.equals(be);
            }
            ClassInfo aeInfo;
            ClassInfo beInfo;
            if (ae.getSort() == Type.ARRAY) {
                aeInfo = new ClassInfo(ae.getDescriptor(),OBJECT,Arrays.asList(CLONEABLE,SERIALIZABLE),false);
            } else {
                aeInfo = resolve(ae.getInternalName());
            }
            if (be.getSort() == Type.ARRAY) {
                beInfo = new ClassInfo(be.getDescriptor(),OBJECT,Arrays.asList(CLONEABLE,SERIALIZABLE),false);
            } else {
                beInfo = resolve(be.getInternalName());
            }
            if (aeInfo != null && beInfo != null) return aeInfo.isAssignableFrom(beInfo);
            return false;
        }

    }



    public SafeClassWriter() { super(ClassWriter.COMPUTE_FRAMES); }

    private byte[] readClassBytes(String internalName) throws IOException {
        String resource = internalName + ".class";
        InputStream in = this.classloader.getResourceAsStream(resource);
        if (in==null) return null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] buffer = new byte[4096];
            int r;
            while ((r=in.read(buffer)) != -1) {
                out.write(buffer, 0, r);
            }
            return out.toByteArray();
        } finally {
            in.close();
        }
    }

    private ClassInfo resolve(String internalName) {
        try {
            byte[] classBytes = readClassBytes(internalName);
            if (classBytes != null) {
                ClassReader cr = new ClassReader(classBytes);
                return new ClassInfo(cr.getClassName(), cr.getSuperName(), cr.getInterfaces() == null ? Collections.emptyList() : Arrays.asList(cr.getInterfaces()), ((cr.getAccess() & Opcodes.ACC_INTERFACE) != 0));
            }
        } catch (Throwable ignored) {}
        try {
            Class<?> classRef = Class.forName(internalName.replace('/', '.'), false, this.classloader);
            String clazzName = classRef.getName().replace('.', '/');
            String superName;
            if (classRef.isInterface()) {
                superName = OBJECT;
            } else {
                Class<?> superClazz = classRef.getSuperclass();
                superName = superClazz == null ? null : superClazz.getName().replace('.', '/');
            }
            Class<?>[] interfaces = classRef.getInterfaces();
            List<String> interfaces_list = new ArrayList<>(interfaces.length);
            for (Class<?> itf : interfaces) {
                interfaces_list.add(itf.getName().replace('.', '/'));
            }
            return new ClassInfo(clazzName, superName, interfaces_list, classRef.isInterface());
        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        ClassInfo ci1 = resolve(type1);
        ClassInfo ci2 = resolve(type2);
        if (ci1 != null && ci2 != null) {
            if (ci1.isAssignableFrom(ci2)) {
                return ci1.className;
            } else if (ci2.isAssignableFrom(ci1)) {
                return ci2.className;
            } else if (ci1.isInterface || ci2.isInterface) {
                return OBJECT;
            } else do {
                ci1=resolve(ci1.superName);
            } while (ci1 != null && !ci1.isAssignableFrom(ci2));
            if (ci1 != null) return ci1.className;
        }
        return OBJECT;
    }
}


