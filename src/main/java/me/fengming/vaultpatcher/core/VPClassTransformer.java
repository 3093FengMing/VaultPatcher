package me.fengming.vaultpatcher.core;

import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher.Utils;
import me.fengming.vaultpatcher.VaultPatcher;
import me.fengming.vaultpatcher.config.DebugMode;
import me.fengming.vaultpatcher.config.TranslationInfo;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VPClassTransformer implements ITransformer<ClassNode> {

    VPClassTransformer() {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformer!");
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        Iterator<TranslationInfo> it = Utils.getIterator();
        while (it.hasNext()) {
            TranslationInfo info = it.next();
            DebugMode debug = VaultPatcherConfig.getDebugMode();
            if (input.name.equals(info.getTargetClassInfo().getName().replace('.', '/'))) {
                // Method
                methodReplace(input, info, debug);
                // Field
                fieldReplace(input, info, debug);
            }
            it.remove();
        }
        return input;
    }

    private static void methodReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if (methodName.isEmpty() || methodName.equals(method.name)) {
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN) {
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                        if (ldcInsnNode.cst instanceof String v && v.equals(info.getKey())) {
                            if (debug.isEnable()) {
                                VaultPatcher.LOGGER.warn("[VaultPatcher] Trying replacing!");
                                Utils.outputDebugIndo((String) ldcInsnNode.cst, "ASMTransformMethod", info.getValue(), input.name, debug);
                            }
                            ldcInsnNode.cst = info.getValue();
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
        return VaultPatcherConfig.isAllClasses() ? new HashSet<>() : new HashSet<>(Utils.addTargetClasses());
    }
}
