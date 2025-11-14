package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeMinecraftTransformer implements ITransformer<ClassNode> {
    public ForgeMinecraftTransformer() {}

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

    private static final String[] TARGETS = {
            // TextComponent
            "net.minecraft.util.text.StringTextComponent",
            "net.minecraft.network.chat.TextComponent",
            "net.minecraft.network.chat.contents.LiteralContents",
            "net.minecraft.network.chat.contents.PlainTextContents$LiteralContents",

            // Font
            "net.minecraft.client.gui.Font",
            "net.minecraft.client.gui.FontRenderer",
    };


    @Override
    public Set<Target> targets() {
        return Arrays.stream(TARGETS).map(Target::targetClass).collect(Collectors.toSet());
    }

    // neoforge only
    @SuppressWarnings("unused")
    public cpw.mods.modlauncher.api.TargetType getTargetType() {
        return Utils.neoforgeGetTargetType("CLASS");
    }
}
