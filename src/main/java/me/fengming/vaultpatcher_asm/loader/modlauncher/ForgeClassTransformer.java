package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
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
            targets.addAll(getExpandTargets());
        } else {
            String name = this.translationInfo.getTargetClass();
            if (!StringUtils.isBlank(name)) {
                targets.add(ITransformer.Target.targetClass(StringUtils.rawPackage(name)));
            }
        }
        return targets;
    }

    public static Set<ITransformer.Target> getExpandTargets() {
        Set<ITransformer.Target> targets = new HashSet<>();
        // Apply mods
        VaultPatcherConfig.getApplyMods().stream()
                .map(mod -> VaultPatcher.mcPath.resolve("mods").resolve(mod + ".jar").toString())
                .flatMap(jarPath -> Utils.getClassesNameByJar(jarPath).stream())
                .map(className -> className.substring(0, className.length() - 6))
                .map(ITransformer.Target::targetClass)
                .forEach(targets::add);

        // Classes
        VaultPatcherConfig.getClasses().stream()
                .map(StringUtils::rawPackage)
                .map(ITransformer.Target::targetClass)
                .forEach(targets::add);

        return targets;
    }

    // neoforge only
    public cpw.mods.modlauncher.api.TargetType getTargetType() {
        return Utils.neoforgeGetTargetType("CLASS");
    }

}
