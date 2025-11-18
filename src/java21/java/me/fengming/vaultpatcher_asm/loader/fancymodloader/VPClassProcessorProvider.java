package me.fengming.vaultpatcher_asm.loader.fancymodloader;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforgespi.transformation.ClassProcessorProvider;
import net.neoforged.neoforgespi.transformation.ProcessorName;

import java.util.Set;

/**
 * Register transformer for NeoForge 1.21.9+.
 * @author teddyxlandlee
 */
public final class VPClassProcessorProvider implements ClassProcessorProvider {
    public VPClassProcessorProvider() {
        var loader = FMLLoader.getCurrent();

        VaultPatcher.isClient = FMLEnvironment.getDist() == Dist.CLIENT;
        VaultPatcher.platform = Platform.Neo21_9;   // Shall we get rid of the enum Platform?

        VaultPatcher.init(loader.getGameDir(), loader.getVersionInfo().mcVersion());
    }

    @Override
    public void createProcessors(Context context, Collector collector) {
        // ClassPatch
        if (VaultPatcherConfig.isEnableClassPatch()) {
            collector.add(new ClassPatchProcessor());
        }
        // Dynamic
        collector.add(new VanillaClassProcessor());
        // Targeted
        for (Set<TranslationInfo> translationInfos : Utils.translationInfoMap.values()) {
            collector.add(new TargetedClassProcessor(translationInfos));
        }

        // Expanded
        collector.add(new ExpandedClassProcessor());
    }

    static ProcessorName processorName(String name) {
        return new ProcessorName("vaultpatcher", name);
    }
}
