package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class VPNeoForgeTranslationClassProcessor extends SimpleClassProcessor {
    private final Set<TranslationInfo> translationInfos;
    private final String processorPath;

    public VPNeoForgeTranslationClassProcessor(Set<TranslationInfo> translationInfos, String processorPath) {
        this.translationInfos = translationInfos;
        this.processorPath = processorPath;
    }

    @Override
    public ProcessorName name() {
        return new ProcessorName("vaultpatcher", processorPath);
    }

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        new VPClassTransformer(this.translationInfos).accept(input);
    }

    @Override
    public Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        if (this.translationInfos == null) return targets;
        for (TranslationInfo info : this.translationInfos) {
            String name = info.getTargetClass();
            if (StringUtils.isBlank(name)) continue;
            targets.add(new Target(StringUtils.dotPackage(name)));
        }
        return targets;
    }
}
