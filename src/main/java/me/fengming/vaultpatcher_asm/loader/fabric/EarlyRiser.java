package me.fengming.vaultpatcher_asm.loader.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.List;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        VaultPatcher.LOGGER.debug("[VaultPatcher] Loading VPEarlyRiser");

        Utils.platform = Utils.Platform.Fabric;

        Path mcPath = FabricLoader.getInstance().getGameDir();
        VaultPatcher.init(mcPath);
        // initial transformers

        // Class Patches
        if (VaultPatcherConfig.isEnableClassPatch()) {
            ClassPatcher.getPatchMap().forEach((k, v) -> ClassTinkerers.addTransformation(k, n -> n = v));
        }

        // Modules
        for (TranslationInfo info : Utils.translationInfos) {
            String cn = info.getTargetClassInfo().getName();
            if (!cn.isEmpty()) {
                ClassTinkerers.addTransformation(cn, new VPClassTransformer(info));
            }
        }

        // Apply Mods
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(targetMod + ".jar").toString())
                    .forEach(s -> ClassTinkerers.addTransformation(s.substring(0, s.length() - 6), new VPClassTransformer(null)));
        }

        // Classes
        VaultPatcherConfig.getClasses().forEach(s -> ClassTinkerers.addTransformation(s, new VPClassTransformer(null)));

        // minecraft transformers
        ClassTinkerers.addTransformation("net.minecraft.class_327", new VPMinecraftTransformer());
        ClassTinkerers.addTransformation("net.minecraft.class_2585", new VPMinecraftTransformer());

        VaultPatcher.LOGGER.debug("[VaultPatcher] ER DONE!");
    }
}
