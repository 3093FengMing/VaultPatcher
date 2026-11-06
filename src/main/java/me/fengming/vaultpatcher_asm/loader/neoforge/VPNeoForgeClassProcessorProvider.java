package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.fabricmc.loader.impl.discovery.ClasspathModCandidateFinder;
import net.neoforged.neoforgespi.transformation.ClassProcessorProvider;

import java.util.HashSet;
import java.util.Set;

public class VPNeoForgeClassProcessorProvider implements ClassProcessorProvider {
    @Override
    public void createProcessors(Context context, Collector collector) {
        NeoForgeRuntimeInfo.bootstrap("NeoForgeProvider");

        if (VaultPatcherConfig.isEnableClassPatch()) {
            Set<String> wholeSet=new HashSet<>(ClassPatcher.getPatches().keySet());
            wholeSet.addAll(ClassPatcher.getDeletes());
            collector.add(new VPNeoForgePatchClassProcessor(wholeSet));
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
        VaultPatcher.debugInfo("NF DONE!");
    }
}
