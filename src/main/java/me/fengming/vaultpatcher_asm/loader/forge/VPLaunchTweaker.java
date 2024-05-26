package me.fengming.vaultpatcher_asm.loader.forge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class VPLaunchTweaker implements ITweaker {
    public static HashMap<String, TranslationInfo> classFinding = new HashMap<>();
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        VaultPatcher.init(gameDir.toPath());

        Utils.translationInfos.forEach(info -> classFinding.putIfAbsent(info.getTargetClassInfo().getName(), info));

        VaultPatcher.LOGGER.warn("[VaultPatcher] LT DONE!");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("me.fengming.vaultpatcher_asm.loader.forge.LinkedClassTransformer");
        VaultPatcher.LOGGER.warn("[VaultPatcher] CL DONE!");
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
