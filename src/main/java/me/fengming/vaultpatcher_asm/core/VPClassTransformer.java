package me.fengming.vaultpatcher_asm.core;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

public class VPClassTransformer implements ITransformer<ClassNode> {

    private static final HashMap<String, HashMap<Integer, String>> localVariableMap = new HashMap<>();

    VPClassTransformer() {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformer!");
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        for (TranslationInfo info : Utils.translationInfos) {
            DebugMode debug = VaultPatcherConfig.getDebugMode();
            if (info.getTargetClassInfo().getName().isEmpty() || input.name.equals(info.getTargetClassInfo().getName().replace('.', '/'))) {
                methodReplace(input, info, debug);
                fieldReplace(input, info, debug);
            }
        }
        return input;
    }

    private static void methodReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if (methodName.isEmpty() || methodName.equals(method.name)) {
                // Initial Local Variable Map
                final HashMap<Integer, String> localVariableMap = new HashMap<>();
                method.localVariables.stream().filter(node -> node.desc.equals("Ljava/lang/String;")).forEach(node -> localVariableMap.put(node.index, node.name));

                for (ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode instruction = it.next();
                    Pairs pairs = info.getPairs();

                    // Start checking instructions and replacing it here
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN) {
                        // String Constants
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                        if (ldcInsnNode.cst instanceof String) {
                            String v = Utils.matchPairs(pairs, (String) ldcInsnNode.cst);
                            Utils.printDebugIndo((String) ldcInsnNode.cst, "ASMTransformMethod-Ldc", v, input.name, debug);
                            ldcInsnNode.cst = v;
                        }
                    } else if (instruction.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
                        // String Concatenation (jdk8+)
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        if (invokeDynamicInsnNode.name.equals("makeConcatWithConstants")) {
                            for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                                if (invokeDynamicInsnNode.bsmArgs[i] instanceof String) {
                                    String str = (String) invokeDynamicInsnNode.bsmArgs[i];
                                    String[] parts = str.split("\u0001", -1);
                                    for (int j = 0; j < parts.length; j++) {
                                        parts[j] = Utils.matchPairs(pairs, parts[j]);
                                    }
                                    String v = String.join("\u0001", parts);
                                    Utils.printDebugIndo(str, "ASMTransformMethod-InvokeDynamic", v, input.name, debug);
                                    invokeDynamicInsnNode.bsmArgs[i] = v;
                                }
                            }
                        }
                    } else if (instruction.getType() == AbstractInsnNode.METHOD_INSN) { // Method Insn
                        MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                        if (methodInsnNode.desc.endsWith(")Ljava/lang/String;") && !methodInsnNode.name.equals("__replaceMethod")) {
                            // For any method that returns String
                            if (Utils.matchLocal(info, methodInsnNode.name, true)) {
                                InsnList list = new InsnList();
                                // set param (pairs)
                                list.add(new LdcInsnNode(info.getPairs()));
                                // call Utils.__replaceMethod(source, key, value);
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__replaceMethod", "(Ljava/lang/String;Lme/fengming/vaultpatcher_asm/config/Pairs;)Ljava/lang/String;", false));
                                method.instructions.insertBefore(methodInsnNode, list);
                                Utils.printDebugIndo("Unknown", "ASMTransformMethod-InsertMethodCalled", "Unknown", input.name, debug);
                            }
                        }
                    } else if (instruction.getType() == AbstractInsnNode.VAR_INSN) { // Var Insn
                        // For any local variable with String type
                        VarInsnNode varInsnNode = (VarInsnNode) instruction;
                        if (varInsnNode.getOpcode() == Opcodes.ASTORE) {
                            if (Utils.matchLocal(info, localVariableMap.getOrDefault(varInsnNode.var, null), false)) {
                                InsnList list = new InsnList();
                                // set param (pairs)
                                list.add(new LdcInsnNode(info.getPairs()));
                                // call Utils.__replaceMethod(source, key, value);
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__replaceMethod", "(Ljava/lang/String;Lme/fengming/vaultpatcher_asm/config/Pairs;)Ljava/lang/String;", false));
                                method.instructions.insertBefore(varInsnNode, list);
                                Utils.printDebugIndo("Unknown", "ASMTransformMethod-InsertLocalVariableStore", "Unknown", input.name, debug);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void fieldReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        Pairs pairs = info.getPairs();
        for (FieldNode field : input.fields) {
            if (field.value instanceof String) {
                String v = Utils.matchPairs(pairs, (String) field.value);
                if (debug.isEnable()) {
                    VaultPatcher.LOGGER.warn("[VaultPatcher] Trying replacing!");
                    Utils.printDebugIndo((String) field.value, "ASMTransformField", v, input.name, debug);
                }
                field.value = v;
            }
        }
    }

    @Override
    public @org.jetbrains.annotations.NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        Set<Target> targetModClasses = new HashSet<>();
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(targetMod + ".jar").toString()).forEach((s) -> targetModClasses.add(Target.targetClass(s)));
        }
        HashSet<Target> targets = new HashSet<>();
        targets.addAll(Utils.addTargetClasses());
        targets.addAll(targetModClasses); // May cause unnecessary resource waste
        return targets;
    }
}
