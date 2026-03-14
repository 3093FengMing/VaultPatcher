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
    public interface BytecodeLookup {
        byte[] find(String internalName) throws IOException;
    }
    private static final String OBJECT = "java/lang/Object";
    private static final String CLONEABLE = "java/lang/Cloneable";
    private static final String SERIALIZABLE = "java/io/Serializable";
    private final ClassLoader classLoader;
    private final BytecodeLookup bytecodeLookup;
    private static final class ClassInfo{
        final String name;
        final String superName;
        final List<String> interfaces;
        final boolean isInterface;
        ClassInfo(String name, String superName, List<String> interfaces, boolean isInterface) {
            this.name = name;
            this.superName = superName;
            this.interfaces = interfaces;
            this.isInterface = isInterface;
        }
    }
    public static ClassLoader ClassLoaderGetter() {
        Object cl = null;
        // If in LaunchWrapper environment, get LaunchWrapper classLoader
        try {
            Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch");
            cl = launch.getField("classLoader").get(null);
        } catch (Throwable ignored) {}
        // If not in LaunchWrapper environment, try to get context loader first
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        // Then the class loader
        if (cl == null) {
            cl = SafeClassWriter.class.getClassLoader();
        }
        return (ClassLoader) cl;
    }
    public static BytecodeLookup launchWrapperLookup() {
        // First try to get classbytes through launchWrapper
        return new BytecodeLookup() {
            @Override
            public byte[] find(String internalName) throws IOException {
                try {
                    Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch");
                    Object cl = launch.getField("classLoader").get(null);
                    Method m = cl.getClass().getMethod("getClassBytes", String.class);
                    return (byte[]) m.invoke(cl, internalName.replace("/", "."));
                } catch (Throwable ignored) {
                    return null;
                }
            }
        };
    }
    private byte[] readClassBytes(String internalName) throws IOException {
        if (bytecodeLookup != null) {
            byte[] fromLookup = bytecodeLookup.find(internalName);
            if (fromLookup != null) return fromLookup;
        }
        // Then we try to get classbyte from context
        String resource = internalName + ".class";
        InputStream in = classLoader.getResourceAsStream(resource);
        if (in == null) return null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] buf = new byte[4096];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            return out.toByteArray();
        } finally {
            in.close();
        }
    }

    private ClassInfo resolve(String internalName) {
        try {
            // Try to get class info from classbytes first
            byte[] bytes = readClassBytes(internalName);
            if (bytes == null) {
                // If we can't, use reflection
                try {
                    Class<?> clazz = Class.forName(internalName.replace('/', '.'), false, classLoader);
                    Class<?> superClass = clazz.getSuperclass();
                    Class<?>[] interfaces = clazz.getInterfaces();
                    List<String> itfs = new ArrayList<String>(interfaces.length);
                    for (Class<?> itf : interfaces) {
                        itfs.add(itf.getName().replace('.', '/'));
                    }
                    ClassInfo info = new ClassInfo(internalName, superClass == null ? null : superClass.getName().replace('.', '/'), itfs, clazz.isInterface());
                    return info;
                } catch (Throwable ignored) {
                    return null;
                }
            }
            ClassReader cr = new ClassReader(bytes);
            int access = cr.getAccess();
            String superName = cr.getSuperName();
            String[] ifs = cr.getInterfaces();
            ClassInfo info = new ClassInfo(internalName, superName, ifs == null ? Collections.<String>emptyList() : Arrays.asList(ifs), (access & Opcodes.ACC_INTERFACE) != 0);
            return info;
        } catch (Throwable ignored) {
            return null;
        }
    }


    public SafeClassWriter(ClassLoader classLoader, BytecodeLookup bytecodeLookup) {
        super(ClassWriter.COMPUTE_FRAMES);
        this.classLoader = classLoader;
        this.bytecodeLookup = bytecodeLookup;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        // If two type are the same, return directly
        if (type1.equals(type2)) {
            return type1;
        }
        try {
            // We treat Array separately
            if (isArrayType(type1) || isArrayType(type2)) {
                return commonSuperArrayAware(type1, type2);
            }
            // If one type is assignable from another, return it
            if (isAssignableFrom(type1, type2)) return type1;
            if (isAssignableFrom(type2, type1)) return type2;
            // resolve class information and then do LCA
            ClassInfo i1 = resolve(type1);
            ClassInfo i2 = resolve(type2);
            if (i1 == null || i2 == null) {
                return OBJECT;
            }
            if (i1.isInterface || i2.isInterface) return OBJECT;
            // Walk ancestors of type1, then walk type2 upwards until first hit.
            Set<String> ancestors = new LinkedHashSet<String>();
            String cur = type1;
            while (cur != null) {
                ancestors.add(cur);
                ClassInfo c = resolve(cur);
                cur = c == null ? null : c.superName;
            }
            cur = type2;
            while (cur != null) {
                if (ancestors.contains(cur)) return cur;
                ClassInfo c = resolve(cur);
                cur = c == null ? null : c.superName;
            }
            return OBJECT;
        } catch (Throwable ignored) {
            // Never fail frame computation due to hierarchy lookup.
            return OBJECT;
        }
    }
    private String commonSuperArrayAware(String type1, String type2) {
        boolean a1 = isArrayType(type1);
        boolean a2 = isArrayType(type2);
        // array + non-array
        if (a1 && !a2) {
            if (OBJECT.equals(type2) || CLONEABLE.equals(type2) || SERIALIZABLE.equals(type2)) return type2;
            return OBJECT;
        }
        if (!a1 && a2) {
            if (OBJECT.equals(type1) || CLONEABLE.equals(type1) || SERIALIZABLE.equals(type1)) return type1;
            return OBJECT;
        }
        // both arrays
        Type t1 = Type.getType(type1);
        Type t2 = Type.getType(type2);
        int d1 = t1.getDimensions();
        int d2 = t2.getDimensions();
        Type e1 = t1.getElementType();
        Type e2 = t2.getElementType();
        // same primitive element + same dims => itself
        if (e1.getSort() != Type.OBJECT || e2.getSort() != Type.OBJECT) {
            if (d1 == d2 && e1.getSort() == e2.getSort()) return type1;
            return OBJECT;
        }
        // different dims for reference arrays: best safe precision is Object[minDim]
        if (d1 != d2) {
            return arrayOf(OBJECT, Math.min(d1, d2));
        }
        // same dims, reference elements
        String elem1 = e1.getInternalName();
        String elem2 = e2.getInternalName();
        String commonElem = getCommonSuperClass(elem1, elem2);
        return arrayOf(commonElem, d1);
    }
    private static boolean isArrayType(String type) {
        return type != null && type.length() > 0 && type.charAt(0) == '[';
    }
    private static String arrayOf(String elementInternalName, int dims) {
        StringBuilder sb = new StringBuilder(dims + elementInternalName.length() + 2);
        for (int i = 0; i < dims; i++) sb.append('[');
        sb.append('L').append(elementInternalName).append(';');
        return sb.toString();
    }
    private boolean isAssignableFrom(String maybeSuper, String maybeSub) {
        if (maybeSuper.equals(maybeSub)) return true;
        if (OBJECT.equals(maybeSuper)) return true;
        // Handle arrays conservatively and correctly.
        if (isArrayType(maybeSuper) || isArrayType(maybeSub)) {
            return isArrayAssignableFrom(maybeSuper, maybeSub);
        }
        ClassInfo sub = resolve(maybeSub);
        if (sub == null) return false;
        Deque<String> q = new ArrayDeque<String>();
        Set<String> seen = new HashSet<String>();
        q.add(maybeSub);
        while (!q.isEmpty()) {
            String cur = q.removeFirst();
            if (!seen.add(cur)) continue;
            if (maybeSuper.equals(cur)) return true;
            ClassInfo ci = resolve(cur);
            if (ci == null) continue;
            if (ci.superName != null) q.addLast(ci.superName);
            for (String itf : ci.interfaces) q.addLast(itf);
        }
        return false;
    }
    private boolean isArrayAssignableFrom(String maybeSuper, String maybeSub) {
        if (maybeSuper.equals(maybeSub)) return true;
        if (OBJECT.equals(maybeSuper)) return true;
        if (CLONEABLE.equals(maybeSuper) || SERIALIZABLE.equals(maybeSuper)) {
            return isArrayType(maybeSub);
        }
        if (!isArrayType(maybeSuper) || !isArrayType(maybeSub)) return false;
        Type s = Type.getType(maybeSuper);
        Type t = Type.getType(maybeSub);
        if (s.getDimensions() != t.getDimensions()) return false;
        Type se = s.getElementType();
        Type te = t.getElementType();
        if (se.getSort() != Type.OBJECT || te.getSort() != Type.OBJECT) {
            return se.getSort() == te.getSort();
        }
        return isAssignableFrom(se.getInternalName(), te.getInternalName());
    }
}
