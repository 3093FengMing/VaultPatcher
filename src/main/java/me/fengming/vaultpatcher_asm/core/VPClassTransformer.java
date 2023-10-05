package me.fengming.vaultpatcher_asm.core;

import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class VPClassTransformer implements Consumer<ClassNode> {
    private final DebugMode debug = VaultPatcherConfig.getDebugMode();
    private final TranslationInfo translationInfo;
    private static boolean disableLocal = false;
    public VPClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
        if (info != null && debug.isEnable()) {
            VaultPatcher.LOGGER.info(String.format("[VaultPatcher] Loading VPTransformer for translation info: %s", info));
        }
    }

    private static void methodReplace(ClassNode input, TranslationInfo info) {
        boolean hasClinit = false;
        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if (Utils.isBlank(methodName) || methodName.equals(method.name)) {
                // Initial Local Variable Map
                final HashMap<Integer, String> localVariableMap = new HashMap<>();
                boolean localVarEnable = false;
                if (!disableLocal && method.localVariables != null) {
                    localVarEnable = true;
                    method.localVariables.stream()
                            .filter(node -> node.desc.equals("Ljava/lang/String;"))
                            .forEach(node -> localVariableMap.put(node.index, node.name));
                }

                for (ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode instruction = it.next();
                    Pairs pairs = info.getPairs();

                    // Start checking instructions and replacing it here

                    // String Constants
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN) {
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                        if (ldcInsnNode.cst instanceof String) {
                            String v = Utils.matchPairs(pairs, (String) ldcInsnNode.cst, false);
                            Utils.printDebugInfo((String) ldcInsnNode.cst, "ASMTransformMethod-Ldc", v, input.name, info);
                            ldcInsnNode.cst = v;
                        }
                    }

                    // String Concatenation (jdk8+)
                    else if (instruction.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        if (invokeDynamicInsnNode.name.equals("makeConcatWithConstants")) {
                            for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                                if (invokeDynamicInsnNode.bsmArgs[i] instanceof String) {
                                    String str = (String) invokeDynamicInsnNode.bsmArgs[i];
                                    String[] parts = str.split("\u0001", -1);
                                    for (int j = 0; j < parts.length; j++) {
                                        parts[j] = Utils.matchPairs(pairs, parts[j], false);
                                    }
                                    String v = String.join("\u0001", parts);
                                    Utils.printDebugInfo(str.replace("\u0001", "<p>"), "ASMTransformMethod-StringConcat", v.replace("\u0001", "<p>"), input.name, info);
                                    invokeDynamicInsnNode.bsmArgs[i] = v;
                                }
                            }
                        }
                    }

                    // Local

                    // Any method that returns String
                    else if (!disableLocal && instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                        if (methodInsnNode.desc.endsWith(")Ljava/lang/String;")
                                && !methodInsnNode.name.equals("__vp_replace")
                                && matchLocal(info, methodInsnNode.name, true)) {
                            insertReplace(input.name, method, methodInsnNode);
                            Utils.printDebugInfo("Runtime Determination", "ASMTransformMethod-InsertMethodReturn", "Runtime Determination", input.name, info);
                        }
                    }

                    // For any local variable of String
                    else if (localVarEnable && instruction.getType() == AbstractInsnNode.VAR_INSN) {
                        VarInsnNode varInsnNode = (VarInsnNode) instruction;

                        // Local Variable
                        if ((varInsnNode.getOpcode() == Opcodes.ASTORE || varInsnNode.getOpcode() == Opcodes.ALOAD)
                                && matchLocal(info, localVariableMap.getOrDefault(varInsnNode.var, null), false)) {
                            insertReplace(input.name, method, varInsnNode);
                            Utils.printDebugInfo("Runtime Determination", "ASMTransformMethod-InsertLocalVariableStore/Load", "Runtime Determination", input.name, info);
                        }

//                        // Parameters
//                        method.parameters.forEach(p -> {
//                            if (p.name.equals(localVariableMap.getOrDefault(varInsnNode.var, null))) {
//                                insertReplace(info, method, varInsnNode);
//                                Utils.printDebugInfo("Runtime Determination", "ASMTransformMethod-InsertLocalVariableLoad", "Runtime Determination", input.name, info);
//                            }
//                        });
//
                    }

                    // Return String
                    else if (!disableLocal && method.desc.endsWith(")Ljava/lang/String;") && instruction.getType() == AbstractInsnNode.INSN) {
                        InsnNode insnNode = (InsnNode) instruction;
                        if (insnNode.getOpcode() == Opcodes.ARETURN && matchLocal(info, method.name, false)) {
                            insertReplace(input.name, method, insnNode);
                            Utils.printDebugInfo("Runtime Determination", "ASMTransformMethod-InsertReturn", "Runtime Determination", input.name, info);
                        }
                    }
                }
            }

            if (!disableLocal && method.name.equals("<clinit>")) {
                hasClinit = true;
                method.instructions.insertBefore(method.instructions.getLast(), new MethodInsnNode(Opcodes.INVOKESTATIC, input.name, "__vp_init", "()V"));
            }
        }

        // patch it (add replace method)
        if (!disableLocal) {
            patchClass(input, input.name, info.getPairs().getMap().entrySet(), hasClinit);
        }

    }

    private static void patchClass(ClassVisitor cv, String className, Set<Map.Entry<String, String>> set, boolean hasClinit) {

        // field __vp_map
        {
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "__vp_map", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            fv.visitEnd();
        }

        // <clinit>
        if (!hasClinit) {
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();

            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "__vp_init", "()V", false);

            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // __vp_init
        {
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "__vp_init", "()V", null, null);
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
            for (Map.Entry<String, String> entry : set) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "__vp_map", "Ljava/util/HashMap;");
                mv.visitLdcInsn(entry.getKey());
                mv.visitLdcInsn(entry.getValue());
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                mv.visitInsn(Opcodes.POP);
            }

            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(3, 0);
            mv.visitEnd();
        }

        // __vp_replace
        {
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
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

    }

    private static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.rawPackage(className), "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", false));
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        if (Utils.isBlank(i.getLocal())) return false;
        if (i.getLocalMode() == 2) return i.getLocal().equals(name);
        if ((i.getLocalMode() == 0 && isMethod) || (i.getLocalMode() == 1 && !isMethod)) return i.getLocal().equals(name);
        return false;
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

        // Export
        if (debug.isExportClass()) {
            ClassWriter w = new ClassWriter(0);
            input.accept(w);
            byte[] b = w.toByteArray();
            try {
                File file = Utils.mcPath.resolve("exported").resolve(input.name + ".class").toFile();
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                file.setWritable(true);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new IllegalStateException("Failed Exporting Class", e);
            }
        }

    }
}
