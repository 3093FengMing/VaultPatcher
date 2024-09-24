package me.fengming.vaultpatcher_asm.loader.forge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VPLaunchTweaker implements ITweaker {
    // MultiMap
    public static HashMap<String, Set<TranslationInfo>> classFinding = new HashMap<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        Utils.platform = Utils.Platform.Forge1_6;

        if (assetsDir != null) Utils.isClient = true;

        VaultPatcher.init(gameDir.toPath());
        Utils.translationInfos.forEach(info -> {
            Set<TranslationInfo> set = classFinding.getOrDefault(info.getTargetClassInfo().getName(), new HashSet<>());
            set.add(info);
            classFinding.put(info.getTargetClassInfo().getName(), set);
        });

        VaultPatcher.LOGGER.debug("[VaultPatcher] LT DONE!");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("me.fengming.vaultpatcher_asm.loader.forge.LinkedClassTransformer");
        VaultPatcher.LOGGER.debug("[VaultPatcher] CL DONE!");
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
