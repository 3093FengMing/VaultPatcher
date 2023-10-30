package me.fengming.vaultpatcher_asm.loader.forge;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ForgeClassTransformer implements ITransformer<ClassNode> {

    private final TranslationInfo translationInfo;
    private final DebugMode debug = VaultPatcherConfig.getDebugMode();

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
        Set<Target> targets = new HashSet<>();

        if (translationInfo == null) {
            // Apply mods
            targets.addAll(VaultPatcherConfig.getApplyMods().stream().collect(
                    ArrayList::new,
                    (list, s) -> Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(s + ".jar").toString())
                            .forEach(s1 -> list.add(ITransformer.Target.targetClass(s1.substring(0, s1.length() - 6)))),
                    ArrayList::addAll)); // May cause unnecessary resource waste

            // Classes
            targets.addAll(VaultPatcherConfig.getClasses().stream().collect(
                    ArrayList::new,
                    (list, s) -> list.add(ITransformer.Target.targetClass(Utils.rawPackage(s))),
                    ArrayList::addAll));
        } else {
            String name = translationInfo.getTargetClassInfo().getName();
            if (!Utils.isBlank(name)) {
                targets.add(ITransformer.Target.targetClass(Utils.rawPackage(name)));
            }
        }

        targets.iterator().forEachRemaining(t -> VaultPatcher.debugInfo(String.format("[VaultPatcher] VPClassTransformer Target = %s", t.getClassName())));

        return targets;
    }

}
