package me.fengming.vaultpatcher_asm.core;

import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
            VaultPatcher.LOGGER.info(String.format("[VaultPatcher Debug] Loading VPTransformer for Class: %s, Method: %s, Local: %s, Pairs: %s", info.getTargetClassInfo().getName(), info.getTargetClassInfo().getMethod(), info.getTargetClassInfo().getLocal(), info.getPairs()));
        }
    }

    private static void methodReplace(ClassNode input, TranslationInfo info) {
        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if (Utils.isBlank(methodName) || methodName.equals(method.name)) {
                // Initial Local Variable Map
                final HashMap<Integer, String> localVariableMap = new HashMap<>();
                boolean localVarEnable = false;
                if (!disableLocal && method.localVariables != null) {
                    localVarEnable = true;
                    method.localVariables.stream().filter(node -> node.desc.equals("Ljava/lang/String;")).forEach(node -> localVariableMap.put(node.index, node.name));
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

                    // Any method that returns String
                    else if (!disableLocal && instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                        if (methodInsnNode.desc.endsWith(")Ljava/lang/String;")
                                && !methodInsnNode.name.equals("__replaceMethod")
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
                    else if (!disableLocal && method.desc.endsWith("Ljava/lang/String;") && instruction.getType() == AbstractInsnNode.INSN) {
                        InsnNode insnNode = (InsnNode) instruction;
//                        if (insnNode.getOpcode() == Opcodes.ARETURN)
                        insertReplace(input.name, method, insnNode);
                    }
                }
            }
        }
        // add method

        if (!disableLocal) {
            MethodVisitor replaceMethodVisitor = input.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            replaceMethod_visit(replaceMethodVisitor, info.getPairs());
        }

    }

    private static void replaceMethod_visit(MethodVisitor mv, Pairs p) {
        Set<Map.Entry<String, String>> set = p.getMap().entrySet();

        mv.visitCode();

        // keys
        Label label0 = new Label();
        mv.visitLabel(label0);
        __index(set.size(), mv);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

        int keysIndex = 0;
        for (Map.Entry<String, String> entry : set) {
            mv.visitInsn(Opcodes.DUP);
            __index(keysIndex, mv);
            mv.visitLdcInsn(entry.getKey());
            mv.visitInsn(Opcodes.AASTORE);
            keysIndex++;
        }
        mv.visitVarInsn(Opcodes.ASTORE, 1);

        // values
        Label label1 = new Label();
        mv.visitLabel(label1);
        __index(set.size(), mv);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

        int valuesIndex = 0;
        for (Map.Entry<String, String> entry : set) {
            mv.visitInsn(Opcodes.DUP);
            __index(valuesIndex, mv);
            mv.visitLdcInsn(entry.getValue());
            mv.visitInsn(Opcodes.AASTORE);
            valuesIndex++;
        }
        mv.visitVarInsn(Opcodes.ASTORE, 2);


        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, 3);

        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitFrame(Opcodes.F_APPEND, 3, new Object[]{"[Ljava/lang/String;", "[Ljava/lang/String;", Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.ARRAYLENGTH);

        Label label4 = new Label();
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, label4);

        Label label5 = new Label();
        mv.visitLabel(label5);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);

        Label label6 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, label6);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitLabel(label6);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitIincInsn(3, 1);
        mv.visitJumpInsn(Opcodes.GOTO, label3);
        mv.visitLabel(label4);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARETURN);

        Label label7 = new Label();
        mv.visitLabel(label7);
        mv.visitLocalVariable("i", "I", null, label3, label4, 3);
        mv.visitLocalVariable("source", "Ljava/lang/String;", null, label0, label7, 0);
        mv.visitLocalVariable("keys", "[Ljava/lang/String;", null, label1, label7, 1);
        mv.visitLocalVariable("values", "[Ljava/lang/String;", null, label2, label7, 2);
        mv.visitMaxs(4, 4);
        mv.visitEnd();

    }

    private static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.rawPackage(className), "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", false));
    }

    private static void __index(int i, MethodVisitor mv) {
        if (i > 5) {
            mv.visitIntInsn(Opcodes.BIPUSH, i);
        } else {
            mv.visitInsn(Opcodes.ICONST_0 + i);
        }
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        if (Utils.isBlank(i.getLocal())) return false;
        if ((i.getLocalMode() == 1 && isMethod) || (i.getLocalMode() == 0 && !isMethod)) {
            return i.getLocal().equals(name);
        }
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
            methodReplace(input, this.translationInfo);
            fieldReplace(input, this.translationInfo);
        }

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
