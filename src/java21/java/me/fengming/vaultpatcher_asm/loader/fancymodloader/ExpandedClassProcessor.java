package me.fengming.vaultpatcher_asm.loader.fancymodloader;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.ModClassDiscovery;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ExpandedClassProcessor extends SimpleClassProcessor {
    private static final ProcessorName NAME = VPClassProcessorProvider.processorName("mod");

    @Override
    public ProcessorName name() {
        return NAME;
    }

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        new VPClassTransformer(null).accept(input);
    }

    @Override
    public Set<Target> targets() {
        var set = new LinkedHashSet<Target>();
        ModClassDiscovery.getApplyClassNames().map(Target::new).forEach(set::add);
        VaultPatcherConfig.getClasses().stream().map(StringUtils::dotPackage).map(Target::new).forEach(set::add);

        return set;
    }
}
