package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class PatchClassTransformer implements ITransformer<ClassNode> {
    private final String className;

    public PatchClassTransformer(String className) {
        this.className = className;
    }

    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        VaultPatcher.debugInfo("[VaultPatcher] Using Patch: {}", input.name);
        return ClassPatcher.patch(className);
    }

    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    public Set<Target> targets() {
        HashSet<Target> targets = new HashSet<>();
        targets.add(Target.targetClass(className));
        return targets;
    }
}
