package me.fengming.vaultpatcher_asm.loader.fancymodloader;

import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

public final class VanillaClassProcessor extends SimpleClassProcessor {
    private static final ProcessorName NAME = VPClassProcessorProvider.processorName("vanilla");

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        new VPMinecraftTransformer().accept(input);
    }

    @Override
    public Set<Target> targets() {
        return Set.of(
                // Font, does it work in 1.21.6+?
                new Target("net.minecraft.client.gui.Font"),
                // TextComponent, 1.20.3+
                new Target("net.minecraft.network.chat.contents.PlainTextContents$LiteralContents")
        );
    }

    @Override
    public ProcessorName name() {
        return NAME;
    }
}
