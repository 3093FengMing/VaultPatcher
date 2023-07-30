package me.fengming.vaultpatcher_asm.core;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.*;

import java.util.*;

public class VPClassTransformer implements ITransformer<ClassNode> {

    VPClassTransformer() {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformer!");
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        for (TranslationInfo info : Utils.translationInfos) {
            DebugMode debug = VaultPatcherConfig.getDebugMode();
            if (info.getTargetClassInfo().getName().isEmpty()
                    || input.name.equals(info.getTargetClassInfo().getName().replace('.', '/'))
            ) {
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
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN) { // String Constants
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                        if (ldcInsnNode.cst instanceof String v && v.equals(info.getKey())) {
                            if (debug.isEnable()) {
                                VaultPatcher.LOGGER.warn("[VaultPatcher] Trying replacing!");
                                Utils.outputDebugIndo((String) ldcInsnNode.cst, "ASMTransformMethod-Ldc", info.getValue(), input.name, debug);
                            }
                            ldcInsnNode.cst = info.getValue();
                        }
                    } else if (instruction.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) { // String Concatenation
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        if (invokeDynamicInsnNode.name.equals("makeConcatWithConstants")) {
                            for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                                if (invokeDynamicInsnNode.bsmArgs[i] instanceof String str) {
                                    String[] parts = str.split("\u0001", -1);
                                    for (int j = 0; j < parts.length; j++) {
                                        if (parts[j].equals(info.getKey())) {
                                            parts[j] = info.getValue();
                                        }
                                    }
                                    String v = String.join("\u0001", parts);
                                    if (debug.isEnable()) {
                                        VaultPatcher.LOGGER.warn("[VaultPatcher] Trying replacing!");
                                        Utils.outputDebugIndo(str, "ASMTransformMethod-InvokeDynamic", v, input.name, debug);
                                    }
                                    invokeDynamicInsnNode.bsmArgs[i] = v;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void fieldReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        for (FieldNode field : input.fields) {
            if (field.value instanceof String v && info.getKey().equals(v)) {
                if (debug.isEnable()) {
                    VaultPatcher.LOGGER.warn("[VaultPatcher] Trying replacing!");
                    Utils.outputDebugIndo((String) field.value, "ASMTransformField", info.getValue(), input.name, debug);
                }
                field.value = info.getValue();
            }
        }
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        Set<Target> targetModClasses = new HashSet<>();
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            List<String> classes = Utils.getClassNameByJar(Utils.mcPath.resolve("mods").resolve(targetMod + ".jar").toString());
            classes.forEach((s) -> targetModClasses.add(Target.targetClass(s)));
        }
        HashSet<Target> targets = new HashSet<>();
        targets.addAll(Utils.addTargetClasses());
        targets.addAll(targetModClasses); // May cause unnecessary resource waste
        return targets;
    }
}
