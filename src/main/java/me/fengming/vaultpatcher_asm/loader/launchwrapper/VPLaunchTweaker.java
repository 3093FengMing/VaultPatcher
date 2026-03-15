package me.fengming.vaultpatcher_asm.loader.launchwrapper;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrap;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrapContext;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class VPLaunchTweaker implements ITweaker {
    private static final String CLIENT_MAIN = "net.minecraft.client.main.Main";
    private static final String SERVER_MAIN = "net.minecraft.server.MinecraftServer";
    private String launchTarget = CLIENT_MAIN;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        final File gameDirectory = gameDir;
        final File assetsDirectory = assetsDir;
        launchTarget = assetsDirectory == null ? SERVER_MAIN : CLIENT_MAIN;
        LoaderBootstrap.bootstrap(new LoaderBootstrapContext() {
            @Override
            public String loaderName() {
                return "LaunchWrapper";
            }

            @Override
            public Platform platform() {
                return Platform.Forge1_6;
            }

            @Override
            public java.nio.file.Path gameDir() {
                return gameDirectory == null ? null : gameDirectory.toPath();
            }

            @Override
            public boolean isClient() {
                return assetsDirectory != null;
            }

            @Override
            public String resolveMinecraftVersion() {
                return getMinecraftVersion();
            }
        });
        VaultPatcher.debugInfo("[VaultPatcher] LT DONE!");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("me.fengming.vaultpatcher_asm.loader.launchwrapper.LinkedClassTransformer");
        VaultPatcher.debugInfo("[VaultPatcher] CL DONE!");
    }

    @Override
    public String getLaunchTarget() {
        return launchTarget;
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
