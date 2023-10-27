package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VaultPatcher {
    public static Logger LOGGER = LogManager.getLogger();

    public static void init(Path mcPath) {
        try {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Caches!");
            Caches.initCache(mcPath.resolve("vaultpatcher").resolve("cache"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cache", e);
        }
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Configs!");
        Utils.mcPath = mcPath;
        try {
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            List<String> mods = VaultPatcherConfig.getMods();
            for (String mod : mods) {
                VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                vpp.read();
                Utils.translationInfos.addAll(vpp.getTranslationInfoList());
                Utils.dynTranslationInfos.addAll(vpp.getDynTranslationInfoList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}
