package me.fengming.vaultpatcher_asm.loader.fancymodloader;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class TargetedClassProcessor extends SimpleClassProcessor {
    private static final ProcessorName NAME = VPClassProcessorProvider.processorName("targeted");
    private final Set<TranslationInfo> translationInfos;

    public TargetedClassProcessor(Set<TranslationInfo> translationInfos) {
        Objects.requireNonNull(translationInfos);
        this.translationInfos = translationInfos;
    }

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        new VPClassTransformer(translationInfos).accept(input);
    }

    @Override
    public Set<Target> targets() {
        return translationInfos.stream()
                .map(TranslationInfo::getTargetClass)
                .filter(String::isBlank)
                .map(StringUtils::dotPackage)
                .map(Target::new)
                .collect(Collectors.toSet());
    }

    @Override
    public ProcessorName name() {
        return NAME;
    }
}
