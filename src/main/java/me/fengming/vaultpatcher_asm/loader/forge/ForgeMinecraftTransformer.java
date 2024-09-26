package me.fengming.vaultpatcher_asm.loader.forge;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class ForgeMinecraftTransformer implements ITransformer<ClassNode> {
    public ForgeMinecraftTransformer() {
        VaultPatcher.LOGGER.debug("[VaultPatcher] Loading MinecraftTransformer");
    }

    // for Forge
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        new VPMinecraftTransformer().accept(input);
        return input;
    }

    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }


    @Override
    public Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        // TextComponent
        targets.add(Target.targetClass("net.minecraft.util.text.StringTextComponent"));
        targets.add(Target.targetClass("net.minecraft.network.chat.TextComponent"));
        targets.add(Target.targetClass("net.minecraft.network.chat.contents.LiteralContents"));
        // Font
        targets.add(Target.targetClass("net.minecraft.client.gui.Font"));
        targets.add(Target.targetClass("net.minecraft.client.gui.FontRenderer"));
        return targets;
    }

    // neoforge only
    public cpw.mods.modlauncher.api.TargetType getTargetType() {
        return Utils.neoforgeGetTargetType("CLASS");
    }
}
