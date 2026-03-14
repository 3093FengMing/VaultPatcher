package me.fengming.vaultpatcher_asm.loader;

import cpw.mods.modlauncher.Launcher;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LoaderBootstrap {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private LoaderBootstrap() {}

    public static boolean bootstrap(LoaderBootstrapContext context) {
        if (context == null) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Loader context is null!");
            return false;
        }

        Path gameDir = context.gameDir();
        if (gameDir == null) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Minecraft path not found in {}!", context.loaderName());
            return false;
        }

        if (!INITIALIZED.compareAndSet(false, true)) {
            VaultPatcher.debugInfo("[VaultPatcher] Skip duplicate init from {}", context.loaderName());
            return false;
        }

        VaultPatcher.platform = context.platform();
        VaultPatcher.isClient = context.isClient();

        String minecraftVersion = resolveMinecraftVersion(context);
        if (StringUtils.isBlank(minecraftVersion)) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Failed to get minecraft version in {}!", context.loaderName());
            minecraftVersion = "";
        }

        VaultPatcher.init(gameDir, minecraftVersion);
        return true;
    }

    public static String resolveMinecraftVersion(LoaderBootstrapContext context) {
        String version = safeResolveFromContext(context);
        if (!StringUtils.isBlank(version)) return version;

        version = getVersionFromSystemProperties();
        if (!StringUtils.isBlank(version)) return version;

        version = getVersionFromFMLLoaderCurrent();
        if (!StringUtils.isBlank(version)) return version;

        version = getVersionFromLauncherArgs();
        if (!StringUtils.isBlank(version)) return version;

        return "";
    }

    private static String safeResolveFromContext(LoaderBootstrapContext context) {
        try {
            return context.resolveMinecraftVersion();
        } catch (Throwable t) {
            return null;
        }
    }

    private static String getVersionFromSystemProperties() {
        String[] keys = new String[]{
                "fml.mcVersion",
                "minecraft.version",
                "mc.version"
        };
        for (String key : keys) {
            String v = System.getProperty(key);
            if (!StringUtils.isBlank(v)) return v;
        }
        return null;
    }

    private static String getVersionFromFMLLoaderCurrent() {
        try {
            Class<?> fmlLoader = Class.forName("net.neoforged.fml.loading.FMLLoader");
            Method getCurrent = fmlLoader.getDeclaredMethod("getCurrent");
            Object current = getCurrent.invoke(null);
            if (current == null) return null;

            try {
                Method versionInfoMethod = current.getClass().getDeclaredMethod("versionInfo");
                Object versionInfo = versionInfoMethod.invoke(current);
                if (versionInfo != null) {
                    for (String candidate : new String[]{"mcVersion", "minecraftVersion"}) {
                        try {
                            Method method = versionInfo.getClass().getDeclaredMethod(candidate);
                            Object value = method.invoke(versionInfo);
                            if (value instanceof String && !StringUtils.isBlank((String) value)) {
                                return (String) value;
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}

        return null;
    }

    private static String getVersionFromLauncherArgs() {
        try {
            Object launcher = Launcher.INSTANCE;
            Field argumentHandlerField = launcher.getClass().getDeclaredField("argumentHandler");
            argumentHandlerField.setAccessible(true);
            Object argumentHandler = argumentHandlerField.get(launcher);

            Field argsField = argumentHandler.getClass().getDeclaredField("args");
            argsField.setAccessible(true);
            String[] args = (String[]) argsField.get(argumentHandler);
            if (args == null) return null;

            for (int i = 0; i < args.length - 1; i++) {
                String key = args[i];
                if ("--fml.mcVersion".equals(key) || "--mcVersion".equals(key) || "--version".equals(key)) {
                    return args[i + 1];
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }
}
