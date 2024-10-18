package me.fengming.vaultpatcher_asm.loader.launchwrapper;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VPLaunchTweaker implements ITweaker {
    // MultiMap
    public static HashMap<String, Set<TranslationInfo>> classFinding = new HashMap<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        VaultPatcher.platform = Platform.Forge1_6;

        if (assetsDir != null) VaultPatcher.isClient = true;

        VaultPatcher.init(gameDir.toPath(), getMinecraftVersion(), Platform.Forge1_6);
        Utils.translationInfos.forEach(info -> {
            Set<TranslationInfo> set = classFinding.getOrDefault(info.getTargetClassInfo().getName(), new HashSet<>());
            set.add(info);
            classFinding.put(info.getTargetClassInfo().getName(), set);
        });

        VaultPatcher.LOGGER.debug("[VaultPatcher] LT DONE!");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("me.fengming.vaultpatcher_asm.loader.launchwrapper.LinkedClassTransformer");
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

    private static String getMinecraftVersion() {
        // https://github.com/CFPAOrg/I18nUpdateMod3/tree/main/src/main/java/i18nupdatemod/launchwrapper/LaunchWrapperTweaker.java#L41-L67
        try {
            // 1.6~1.7.10
            // 1.6: https://github.com/MinecraftForge/FML/blob/16launch/common/cpw/mods/fml/relauncher/FMLInjectionData.java#L32
            // 1.7.10: https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/fml/src/main/java/cpw/mods/fml/relauncher/FMLInjectionData.java#L32
            return getField(Class.forName("cpw.mods.fml.relauncher.FMLInjectionData"), "mcversion");
        } catch (Exception ignored) {
        }

        try {
            // 1.8
            // https://github.com/MinecraftForge/FML/blob/1.8/src/main/java/net/minecraftforge/fml/relauncher/FMLInjectionData.java#L32
            return getField(Class.forName("net.minecraftforge.fml.relauncher.FMLInjectionData"), "mcversion");
        } catch (Exception ignored) {
        }

        try {
            // 1.8.8~1.12.2
            // 1.8.8: https://github.com/MinecraftForge/MinecraftForge/blob/1.8.8/src/main/java/net/minecraftforge/common/ForgeVersion.java#L42
            // 1.12.2: https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/common/ForgeVersion.java#L64
            return getField(Class.forName("net.minecraftforge.common.ForgeVersion"), "mcVersion");
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String getField(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(clazz);
    }
}
