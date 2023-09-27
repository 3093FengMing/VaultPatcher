package me.fengming.vaultpatcher_asm.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import cpw.mods.modlauncher.api.IEnvironment;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.VPMinecraftTransformer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformationService!");

        VaultPatcher.init(FabricLoader.getInstance().getGameDir());
        // initial transformers

        // Patch
        for (TranslationInfo info : Utils.translationInfos) {
            String cn = info.getTargetClassInfo().getName();
            if (!cn.isEmpty()) {
                ClassTinkerers.addTransformation(cn, new VPClassTransformer(info));
            }
        }
        // Apply Mods
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(targetMod + ".jar").toString()).forEach((s) -> ClassTinkerers.addTransformation(s.substring(0, s.length() - 6), new VPClassTransformer(null)));
        }
        // Classes
        VaultPatcherConfig.getClasses().forEach((s) -> ClassTinkerers.addTransformation(s, new VPClassTransformer(null)));

        ClassTinkerers.addTransformation("net.minecraft.class_327", new VPMinecraftTransformer());
        ClassTinkerers.addTransformation("net.minecraft.class_2585", new VPMinecraftTransformer());
    }
}
