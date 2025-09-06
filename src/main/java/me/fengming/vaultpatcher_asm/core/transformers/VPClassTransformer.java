package me.fengming.vaultpatcher_asm.core.transformers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.cache.ClassCache;
import me.fengming.vaultpatcher_asm.core.misc.VPClassLoader;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.node.handlers.NodeHandler;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import me.fengming.vaultpatcher_asm.plugin.VaultPatcherPlugin;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VPClassTransformer implements Consumer<ClassNode> {
    private Set<TranslationInfo> translationInfos;
    private boolean disableLocal = false;

    private boolean transformed = false;

    public VPClassTransformer(Set<TranslationInfo> infos) {
        this.translationInfos = infos;
        if (infos != null) {
            // TransformChecker.setTransformed(infos);
            VaultPatcher.debugInfo("[VaultPatcher] Loading VPTransformer for translation infos: {}", infos);
        }
    }

    private void methodReplace(ClassNode input) {
        boolean mixedClinit = false;
        boolean hasClinit = false;
        boolean isInterface = (input.access & Opcodes.ACC_INTERFACE) != 0;
        for (TranslationInfo info : translationInfos) {
            String methodName = info.getTargetClassInfo().getMethod();

            boolean skipMethodsCheck = StringUtils.isBlank(methodName);

            for (MethodNode method : input.methods) {
                if (method.name.startsWith("__vp")) continue;

                if (skipMethodsCheck || methodName.equals(method.name)) {
                    handlerInstructions(input, method, info);
                }

                if (!mixedClinit && !disableLocal && method.name.equals("<clinit>")) {
                    InsnList list = createClinitAddition(input, isInterface);

                    method.instructions.insertBefore(method.instructions.getLast(), list);
                    hasClinit = true;
                    mixedClinit = true;
                }
            }
        }

        // patch it (add replace method)
        if (!disableLocal) {
            Set<Map.Entry<String, String>> set = translationInfos.stream()
                    .map(TranslationInfo::getPairs)
                    .map(Pairs::getMap)
                    .map(HashMap::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            patchClass(input, input.name, set, hasClinit, isInterface);
        }

    }

    private static InsnList createClinitAddition(ClassNode input, boolean isInterface) {
        /*
        static {
            ...(original code)...
            ...(original code)...

            HashMap __vp_map = new HashMap();
            __vp__init();
        }
        */
        String mapClassName = isInterface ? input.name + "$vp_map" : "java/util/HashMap";
        InsnList list = new InsnList();

        list.add(new TypeInsnNode(Opcodes.NEW, mapClassName));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, mapClassName, "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, input.name, "__vp_map", "Ljava/util/HashMap;"));

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, input.name, "__vp_init", "()V"));
        return list;
    }

    private void handlerInstructions(ClassNode input, MethodNode method, TranslationInfo info) {
        // Initial Local Variable Map
        final HashMap<Integer, String> localVariableMap = new HashMap<>();
        boolean disableLocalVariable = true;
        if (!disableLocal && method.localVariables != null) {
            disableLocalVariable = false;
            method.localVariables.stream()
                    .filter(node -> node.desc.equals("Ljava/lang/String;"))
                    .forEach(node -> localVariableMap.put(node.index, node.name));
        }

        final NodeHandlerParameters params = new NodeHandlerParameters(disableLocal, disableLocalVariable, input, method, localVariableMap, info);
        for (AbstractInsnNode instruction : method.instructions) {
            params.addOrdinal();
            NodeHandler<?> handler = NodeHandler.getHandlerByNode(instruction, params);
            if (handler == null) continue;
            instruction = handler._modifyNode();
        }
    }

    private static void patchClass(ClassVisitor cv, String className, Set<Map.Entry<String, String>> set, boolean hasClinit, boolean isInterface) {

        // field __vp_map
        {
            /*
            private static final HashMap<String, String> __vp__map;
            */
            FieldVisitor fv = cv.visitField((isInterface ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PRIVATE) | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "__vp_map", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            fv.visitEnd();
        }

        // fix
        if (isInterface) {
            String innerClassName = className + "$vp_map";
            // <clinit>
            if (!hasClinit) {
                /*
                static {
                    HashMap __vp_map = new ThisClass$vp_map();
                }
                */
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitTypeInsn(Opcodes.NEW, innerClassName);
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, innerClassName, "<init>", "()V", false);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, className, "__vp_map", "Ljava/util/HashMap;");

                Label label1 = new Label();
                mv.visitLabel(label1);
                mv.visitInsn(Opcodes.RETURN);

                Label label2 = new Label();
                mv.visitLabel(label2);
                mv.visitMaxs(0, 0);

                mv.visitEnd();
            }

            // init (inner class)
            {
                /*
                class ThisClass$vp_map extends HashMap<String, String> {
                    ThisClass$vp_map() {
                        super();
                        this.put(key1, value1);
                        this.put(key2, value2);
                        this.put(key3, value3);
                        ...
                    }
                }

                (actually is)
                HashMap __vp_map = new HashMap() {{
                        this.put(key1, value1);
                        this.put(key2, value2);
                        this.put(key3, value3);
                        ...
                }}
                */

                // inner class
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cw.visit(Opcodes.V1_8, Opcodes.ACC_FINAL | Opcodes.ACC_SUPER, innerClassName, "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", "java/util/HashMap", null);
                cw.visitSource("VaultPatcher_" + innerClassName, null);
                cw.visitOuterClass(className, null, null);
                cw.visitInnerClass(innerClassName, null, null, Opcodes.ACC_STATIC);

                // <init>
                MethodVisitor mv = cw.visitMethod(0, "<init>", "(I)V", null, null);

                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ILOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "(I)V", false);

                Label label1 = new Label();
                mv.visitLabel(label1);
                for (Map.Entry<String, String> entry : set) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitLdcInsn(entry.getKey());
                    mv.visitLdcInsn(entry.getValue());
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, innerClassName, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitInsn(Opcodes.POP);
                }

                Label label2 = new Label();
                mv.visitLabel(label2);
                mv.visitInsn(Opcodes.RETURN);

                Label label3 = new Label();
                mv.visitLabel(label3);
                mv.visitLocalVariable("this", "L" + innerClassName + ";", null, label0, label3, 0);
                mv.visitLocalVariable("x0", "I", null, label0, label3, 1);
                mv.visitMaxs(3, 2);

                mv.visitEnd();

                cw.visitEnd();

                byte[] bytes = cw.toByteArray();
                VPClassLoader.newClass(Thread.currentThread().getContextClassLoader(), innerClassName, bytes);
            }
        } else {
            // <clinit>
            if (!hasClinit) {
                /*
                static {
                    HashMap __vp_map = new HashMap();
                    __vp__init();
                }
                */
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, className, "__vp_map", "Ljava/util/HashMap;");

                Label label1 = new Label();
                mv.visitLabel(label1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "__vp_init", "()V", false);

                Label label2 = new Label();
                mv.visitLabel(label2);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);

                mv.visitEnd();
            }

            // __vp_init
            {
                /*
                private static __vp__init() {
                    __vp_map.put(key1, value1);
                    __vp_map.put(key2, value2);
                    __vp_map.put(key3, value3);
                    ...
                }
                */
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "__vp_init", "()V", null, null);
                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                for (Map.Entry<String, String> entry : set) {
                    mv.visitFieldInsn(Opcodes.GETSTATIC, className, "__vp_map", "Ljava/util/HashMap;");
                    mv.visitLdcInsn(entry.getKey());
                    mv.visitLdcInsn(entry.getValue());
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitInsn(Opcodes.POP);
                }

                Label label1 = new Label();
                mv.visitLabel(label1);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(3, 0);
                mv.visitEnd();
            }
        }

        // __vp_replace(String)
        {
            /*
            private static String __vp_replace(String source) {
                return (String) __vp__map.get(source, source);
            }
            */
            MethodVisitor mv = cv.visitMethod((isInterface ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PRIVATE) | Opcodes.ACC_STATIC, "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();

            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitFieldInsn(Opcodes.GETSTATIC, className, "__vp_map", "Ljava/util/HashMap;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            mv.visitInsn(Opcodes.ARETURN);

            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitLocalVariable("source", "Ljava/lang/String;", null, label0, label1, 0);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }

        // __vp_replace(Object)
        {
            /*
            private static String __vp_replace(Object source) {
                return source == null ? null : (String) __vp__map.get(source.toString(), source.toString());
            }
            */
            MethodVisitor mv = cv.visitMethod((isInterface ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PRIVATE) | Opcodes.ACC_STATIC, "__vp_replace", "(Ljava/lang/Object;)Ljava/lang/String;", null, null);

            mv.visitCode();

            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitVarInsn(Opcodes.ALOAD, 0);

            Label label1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNONNULL, label1);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitLabel(label1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(Opcodes.GETSTATIC, className, "__vp_map", "Ljava/util/HashMap;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            mv.visitInsn(Opcodes.ARETURN);

            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitLocalVariable("source", "Ljava/lang/Object;", null, label0, label2, 0);
            mv.visitMaxs(3, 1);

            mv.visitEnd();
        }

    }

    private void fieldReplace(ClassNode input) {
        for (TranslationInfo info : translationInfos) {
            // If the method name is not specified, the field will be replaced
            TargetClassInfo targetClass = info.getTargetClassInfo();
            if (!StringUtils.isBlank(targetClass.getMethod()) || targetClass.getOrdinal().first == -1) return;

            Pairs pairs = info.getPairs();
            for (FieldNode field : input.fields) {
                if (field.value instanceof String) {
                    String original = (String) field.value;
                    String value = MatchUtils.matchPairs(pairs, original, false);
                    Utils.printDebugInfo(-1, original, "ASMTransformField", value, input.name, info);
                    field.value = value;
                }
            }
        }
    }

    // for Fabric
    @Override
    public void accept(ClassNode input) {
        VaultPatcher.plugins.forEach(e -> e.onTransformClass(input, VaultPatcherPlugin.Phase.BEFORE));

        String className = input.name;
        if (Utils.debug.isUseCache()) {
            ClassCache cache = Caches.getClassCache(className);
            byte[] copy = Utils.nodeToBytes(input);

            if (cache != null) {
                VaultPatcher.debugInfo("Using Cache: {}", className);
                if (cache.updated(input)) {
                    VaultPatcher.debugInfo("Updating Cache: {}", className);
                    transform(input);
                    cache.put(input, copy);
                } else {
                    ClassNode taken = cache.take();
                    Utils.deepCopyClass(input, taken);
                }
            } else {
                VaultPatcher.debugInfo("[VaultPatcher] Generating Class Cache: {}", input.name);
                transform(input);
                Caches.addClassCache(input.name, input);
            }
        } else {
            transform(input);
        }

        // Recompute frames. Otherwise, it may cause java.lang.VerifyError on java8
        // Let stack frames treat everything as Object conservatively to avoid calling unloaded class
        if (VaultPatcher.platform == Platform.Forge1_6) {
            ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_FRAMES){
                @Override
                protected String getCommonSuperClass(String type1, String type2){
                    return "java/lang/Object";
                }
            };
            input.accept(wr);
            byte[] bytes = wr.toByteArray();

            ClassNode copied = new ClassNode();
            ClassReader cr = new ClassReader(bytes);
            cr.accept(copied, ClassReader.SKIP_DEBUG);
            Utils.deepCopyClass(input, copied);
        }

        VaultPatcher.plugins.forEach(e -> e.onTransformClass(input, VaultPatcherPlugin.Phase.AFTER));

        if (Utils.debug.isExportClass()) {
            Utils.exportClass(input, Utils.getVpPath().resolve("exported"));
        }
    }

    private void transform(ClassNode input) {
        if (transformed) {
            VaultPatcher.debugInfo("[VaultPatcher] Class {} has already been transformed!", input.name);
            return;
        }
        if (translationInfos == null) {
            disableLocal = true;
            translationInfos = Utils.translationInfoMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } else {
            disableLocal = translationInfos.stream()
                    .allMatch(i -> StringUtils.isBlank(i.getTargetClassInfo().getLocal()));
        }
        methodReplace(input);
        fieldReplace(input);
        transformed = true;
    }
}
