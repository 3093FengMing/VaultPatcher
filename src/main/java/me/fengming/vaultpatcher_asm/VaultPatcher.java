package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VaultPatcher {
    public static Logger LOGGER = LogManager.getLogger();

    public static void init(Path mcPath) {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Configs!");
        Utils.mcPath = mcPath;
        try {
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            List<String> mods = VaultPatcherConfig.getMods();
            for (String mod : mods) {
                VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                try {
                    vpp.read();
                    Utils.vpps.add(vpp);
                    Utils.translationInfos.addAll(vpp.getTranslationInfoList());
                    Utils.dynTranslationInfos.addAll(vpp.getDynTranslationInfoList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            LOGGER.error("[VaultPatcher] Failed to load config: ", e);
            throw new RuntimeException(e);
        }
    }
}
