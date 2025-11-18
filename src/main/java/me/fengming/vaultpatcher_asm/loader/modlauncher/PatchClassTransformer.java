package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class PatchClassTransformer implements ITransformer<ClassNode> {
    private final Set<String> classNames;

    public PatchClassTransformer(Set<String> classNames) {
        this.classNames = classNames;
    }

    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        VaultPatcher.debugInfo("[VaultPatcher] Using Patch: {}", input.name);
        return ClassPatcher.patch(input);
    }

    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    public Set<Target> targets() {
        HashSet<Target> targets = new HashSet<>();
        classNames.forEach(n -> targets.add(Target.targetClass(n)));
        return targets;
    }

    // neoforge only
    @SuppressWarnings("unused")
    public cpw.mods.modlauncher.api.TargetType getTargetType() {
        return Utils.neoforgeGetTargetType("CLASS");
    }
}
