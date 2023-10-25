package me.fengming.vaultpatcher_asm.forge;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class ForgeClassTransformer implements ITransformer<ClassNode> {

    private final TranslationInfo translationInfo;
    private final DebugMode debug = VaultPatcherConfig.getDebugMode();

    public ForgeClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
//        if (info != null && debug.isEnable()) {
//            VaultPatcher.LOGGER.debug(String.format("[VaultPatcher Debug] Loading VPTransformer for Class: %s, Method: %s, Local: %s, Pairs: %s", info.getTargetClassInfo().getName(), info.getTargetClassInfo().getMethod(), info.getTargetClassInfo().getLocal(), info.getPairs()));
//        }
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
            targets.addAll(Utils.addConfigApplyMods()); // May cause unnecessary resource waste
            targets.addAll(Utils.addConfigClasses());
        } else {
            Target t = Utils.addTargetClasses(this.translationInfo);
            if (t != null) targets.add(t);
        }

        if (debug.isEnable()) {
            VaultPatcher.LOGGER.info("loading1:" + translationInfo);
            targets.iterator().forEachRemaining(t -> VaultPatcher.LOGGER.info(String.format("[VaultPatcher] VPClassTransformer Target = %s", t.getClassName())));
        }

        return targets;
    }
}
