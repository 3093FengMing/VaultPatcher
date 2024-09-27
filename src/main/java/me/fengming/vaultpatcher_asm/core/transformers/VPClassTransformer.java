package me.fengming.vaultpatcher_asm.core.transformers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.cache.ClassCache;
import me.fengming.vaultpatcher_asm.core.misc.VPClassLoader;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.node.handlers.NodeHandler;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import me.fengming.vaultpatcher_asm.plugin.VaultPatcherPlugin;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class VPClassTransformer implements Consumer<ClassNode> {
    private final TranslationInfo translationInfo;
    private boolean disableLocal = false;

    private boolean transformed = false;

    public VPClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
        if (info != null) {
            Utils.setTransformed(info);
            VaultPatcher.debugInfo("[VaultPatcher] Loading VPTransformer for translation info: {}", info);
        }
    }

    private void methodReplace(ClassNode input, TranslationInfo info) {
        boolean hasClinit = false;
        boolean isInterface = (input.access & Opcodes.ACC_INTERFACE) != 0;
        boolean needPatch = input.fields.stream().noneMatch(node -> node.name.equals("__vp_map"));

        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if ((Utils.isBlank(methodName) || methodName.equals(method.name)) && !method.name.equals("__vp_init") && !method.name.equals("__vp_replace")) {
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

            if (!disableLocal && needPatch && method.name.equals("<clinit>")) {
                InsnList list = new InsnList();

                list.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashMap"));
                list.add(new InsnNode(Opcodes.DUP));
                Set<Map.Entry<String, String>> set = info.getPairs().getMap().entrySet();
                if (set.size() > 5) {
                    list.add(new IntInsnNode(Opcodes.BIPUSH, set.size()));
                } else {
                    list.add(new InsnNode(Opcodes.ICONST_0 + set.size())); // max to 255
                }
                list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "(I)V", false));
                list.add(new FieldInsnNode(Opcodes.PUTSTATIC, input.name, "__vp_map", "Ljava/util/HashMap;"));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, input.name, "__vp_init", "()V"));

                method.instructions.insertBefore(method.instructions.getLast(), list);

                hasClinit = true;
            }
        }

        // patch it (add replace method)
        if (!disableLocal && needPatch) {
            patchClass(input, input.name, info.getPairs().getMap().entrySet(), hasClinit, isInterface);
        }

    }

    private static void patchClass(ClassVisitor cv, String className, Set<Map.Entry<String, String>> set, boolean hasClinit, boolean isInterface) {

        // field __vp_map
        {
            FieldVisitor fv = cv.visitField((isInterface ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PRIVATE) | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "__vp_map", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            fv.visitEnd();
        }

        // fix
        if (isInterface) {
            String innerClassName = className + "$vp_map";
            // <clinit>
            if (!hasClinit) {
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitTypeInsn(Opcodes.NEW, innerClassName);
                mv.visitInsn(Opcodes.DUP);
                if (set.size() > 5) {
                    mv.visitIntInsn(Opcodes.BIPUSH, set.size());
                } else {
                    mv.visitInsn(Opcodes.ICONST_0 + set.size()); // max to 255
                }
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, innerClassName, "<init>", "(I)V", false);
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
                // inner class
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                mv.visitCode();

                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
                mv.visitInsn(Opcodes.DUP);
                if (set.size() > 5) {
                    mv.visitIntInsn(Opcodes.BIPUSH, set.size());
                } else {
                    mv.visitInsn(Opcodes.ICONST_0 + set.size());
                }
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "(I)V", false);
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

    private static void fieldReplace(ClassNode input, TranslationInfo info) {
        Pairs pairs = info.getPairs();
        for (FieldNode field : input.fields) {
            if (field.value instanceof String) {
                String original = (String) field.value;
                String value = Utils.matchPairs(pairs, original, false);
                Utils.printDebugInfo(-1, original, "ASMTransformField", value, input.name, info);
                field.value = value;
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
                if (!cache.updated(input)) {
                    VaultPatcher.debugInfo("Updating Cache: {}", className);
                    generate(input);
                    cache.put(input, copy);
                }
                // copy class
                ClassNode taken = cache.take();
                input.methods = taken.methods;
                input.fields = taken.fields;
                input.innerClasses = taken.innerClasses;
            }

            // Ensure that all TranslationInfo is transformed before adding to the cache
            if (Utils.isTransformed(className)) {
                VaultPatcher.debugInfo("Generating Class Cache: {}", input.name);
                generate(input);
                Caches.addClassCache(input.name, input, copy);
            } else {
                generate(input);
            }
        } else {
            generate(input);
        }

        // Recompute frames. Otherwise, it may cause java.lang.VerifyError in java8
        if (Utils.platform == Utils.Platform.Forge1_6) {
            ClassNode copied = new ClassNode();
            byte[] bytes = Utils.nodeToBytes(input);
            ClassReader cr = new ClassReader(bytes);
            cr.accept(copied, ClassReader.SKIP_DEBUG);
            // copy class
            input.methods = copied.methods;
            input.fields = copied.fields;
            input.innerClasses = copied.innerClasses;
        }

        VaultPatcher.plugins.forEach(e -> e.onTransformClass(input, VaultPatcherPlugin.Phase.AFTER));

        if (Utils.debug.isExportClass()) ASMUtils.exportClass(input, Utils.getVpPath().resolve("exported"));
    }

    private void generate(ClassNode input) {
        if (transformed) return;
        if (translationInfo == null) {
            disableLocal = true;
            for (TranslationInfo info : Utils.translationInfos) {
                if (Utils.isBlank(info.getTargetClassInfo().getName()) || input.name.equals(Utils.rawPackage(info.getTargetClassInfo().getName()))) {
                    methodReplace(input, info);
                    fieldReplace(input, info);
                }
            }
        } else if (Utils.isBlank(translationInfo.getTargetClassInfo().getName()) || input.name.equals(Utils.rawPackage(translationInfo.getTargetClassInfo().getName()))) {
            disableLocal = Utils.isBlank(translationInfo.getTargetClassInfo().getLocal());
            methodReplace(input, translationInfo);
            fieldReplace(input, translationInfo);
        }
        transformed = true;
    }
}
