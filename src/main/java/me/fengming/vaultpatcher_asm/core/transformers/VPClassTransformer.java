package me.fengming.vaultpatcher_asm.core.transformers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.cache.ClassCache;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.node.handlers.NodeHandler;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class VPClassTransformer implements Consumer<ClassNode> {
    private final DebugMode debug = VaultPatcherConfig.getDebugMode();
    private final TranslationInfo translationInfo;
    private static boolean disableLocal = false;

    public VPClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
        if (info != null) {
            VaultPatcher.debugInfo(String.format("[VaultPatcher] Loading VPTransformer for translation info: %s", info));
        }
    }

    private static void methodReplace(ClassNode input, TranslationInfo info) {
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
                    instruction = handler.modifyNode();
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
                    list.add(new InsnNode(Opcodes.ICONST_0 + set.size()));
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
                    mv.visitInsn(Opcodes.ICONST_0 + set.size());
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

            // __vp_init
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
                VPClassLoader.newClass(innerClassName, bytes);

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
                String o = (String) field.value;
                String v = Utils.matchPairs(pairs, o, false);
                Utils.printDebugInfo(o, "ASMTransformField", v, input.name, info);
                field.value = v;
            }
        }
    }

    // for Fabric
    @Override
    public void accept(ClassNode input) {
        if (VaultPatcherConfig.getDebugMode().isUseCache()) {
            // check cache
            ClassCache cache = Caches.getClassCache(input.name);
            byte[] copy = Utils.nodeToBytes(input);

            if (cache != null) {
                ClassNode taken = cache.take();
                VaultPatcher.debugInfo("Using Cache: " + input.name);
                if (!cache.updated(input)) {
                    VaultPatcher.debugInfo("Updating Cache: " + input.name);
                    generate(taken);
                    cache.put(taken, copy);
                }
                input.methods = taken.methods;
                input.fields = taken.fields;
                input.innerClasses = taken.innerClasses;
            } else {
                VaultPatcher.debugInfo("Generating Class Cache: " + input.name);
                generate(input);
                Caches.addClassCache(input.name, input, copy);
            }
        } else {
            generate(input);
        }

        if (debug.isExportClass()) ASMUtils.exportClass(input, Utils.mcPath.resolve("vaultpatcher").resolve("exported"));

    }

    private void generate(ClassNode input) {
        if (this.translationInfo == null) {
            disableLocal = true;
            for (TranslationInfo info : Utils.translationInfos) {
                if (Utils.isBlank(info.getTargetClassInfo().getName()) || input.name.equals(Utils.rawPackage(info.getTargetClassInfo().getName()))) {
                    methodReplace(input, info);
                    fieldReplace(input, info);
                }
            }
        } else if (Utils.isBlank(this.translationInfo.getTargetClassInfo().getName()) || input.name.equals(Utils.rawPackage(this.translationInfo.getTargetClassInfo().getName()))) {
            disableLocal = Utils.isBlank(this.translationInfo.getTargetClassInfo().getLocal());
            methodReplace(input, this.translationInfo);
            fieldReplace(input, this.translationInfo);
        }
    }
}
