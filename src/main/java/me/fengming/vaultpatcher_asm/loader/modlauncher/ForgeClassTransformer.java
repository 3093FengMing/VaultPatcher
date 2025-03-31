package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class ForgeClassTransformer implements ITransformer<ClassNode> {

    private final TranslationInfo translationInfo;

    public ForgeClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
    }

    // for Forge
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        new VPClassTransformer(this.translationInfo).accept(input);
        return input;
    }

    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public Set<Target> targets() {
        Set<ITransformer.Target> targets = new HashSet<>();

        if (this.translationInfo == null) {
            targets.addAll(Utils.getExpandTargets());
        } else {
            String name = this.translationInfo.getTargetClassInfo().getName();
            if (!StringUtils.isBlank(name)) {
                targets.add(ITransformer.Target.targetClass(StringUtils.rawPackage(name)));
            }
        }
        return targets;
    }

    // neoforge only
    public cpw.mods.modlauncher.api.TargetType getTargetType() {
        return Utils.neoforgeGetTargetType("CLASS");
    }

}
