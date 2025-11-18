package me.fengming.vaultpatcher_asm.loader.fancymodloader;

import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;

public final class ClassPatchProcessor extends SimpleClassProcessor {
    private static final ProcessorName NAME = VPClassProcessorProvider.processorName("class_patch");

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        ClassNode output = ClassPatcher.patch(input);
        Utils.deepCopyClass(/*target=*/input, /*source=*/output);
    }

    @Override
    public Set<Target> targets() {
        return ClassPatcher.getPatches().keySet().stream().map(Target::new).collect(Collectors.toSet());
    }

    @Override
    public ProcessorName name() {
        return NAME;
    }
}
