package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.neoforged.neoforgespi.transformation.ClassProcessorProvider;

public class VPNeoForgeClassProcessorProvider implements ClassProcessorProvider {
    @Override
    public void createProcessors(Context context, Collector collector) {
        NeoForgeRuntimeInfo.bootstrap("NeoForgeProvider");

        if (VaultPatcherConfig.isEnableClassPatch()) {
            collector.add(new VPNeoForgePatchClassProcessor(ClassPatcher.getPatches().keySet()));
        }

        int i = 0;
        for (java.util.Set<me.fengming.vaultpatcher_asm.config.TranslationInfo> infos : Utils.translationInfoMap.values()) {
            collector.add(new VPNeoForgeTranslationClassProcessor(infos, "translation_" + i));
            i++;
        }
        collector.add(new VPNeoForgeTranslationClassProcessor(null, "translation_expand"));

        boolean hasDynamicRules = !Utils.dynTranslationInfos.isEmpty();
        if (hasDynamicRules) {
            collector.add(new VPNeoForgeMinecraftClassProcessor());
        }
    }
}
