package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VaultPatcher {
    public static final Logger LOGGER = LoggerFactory.getLogger("vaultpatcher");

    public static void init(Path mcPath) {
        try {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Config!");
            Utils.mcPath = mcPath;
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            List<String> mods = VaultPatcherConfig.getMods();
            for (String mod : mods) {
                VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                try {
                    vpp.read();
                    Utils.vpps.add(vpp);
                    Utils.translationInfos.addAll(vpp.getTranslationInfoList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config: ", e);
            throw new RuntimeException(e);
        }
    }
}
