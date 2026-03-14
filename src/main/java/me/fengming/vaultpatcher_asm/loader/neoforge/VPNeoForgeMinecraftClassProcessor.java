package me.fengming.vaultpatcher_asm.loader.neoforge;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class VPNeoForgeMinecraftClassProcessor extends SimpleClassProcessor {
    @Override
    public ProcessorName name() {
        return new ProcessorName("vaultpatcher", "minecraft_dynamic");
    }

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        new VPMinecraftTransformer().accept(input);
    }

    @Override
    public Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        targets.add(new Target("net.minecraft.network.chat.contents.PlainTextContents$LiteralContents"));
        targets.add(new Target("net.minecraft.client.gui.Font"));
        targets.add(new Target("net.minecraft.network.chat.FormattedText"));
        return targets;
    }
}
