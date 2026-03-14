package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrap;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrapContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

final class NeoForgeRuntimeInfo {
    private NeoForgeRuntimeInfo() {}

    static void bootstrap(String source) {
        LoaderBootstrap.bootstrap(new LoaderBootstrapContext() {
            @Override
            public String loaderName() {
                return source;
            }

            @Override
            public Platform platform() {
                return Platform.Forge1_13;
            }

            @Override
            public Path gameDir() {
                return resolveGameDir();
            }

            @Override
            public boolean isClient() {
                return resolveClientFlag();
            }

            @Override
            public String resolveMinecraftVersion() {
                return resolveVersion();
            }
        });
    }

    static Path resolveGameDir() {
        try {
            Class<?> fmlLoader = Class.forName("net.neoforged.fml.loading.FMLLoader");
            Method getCurrent = fmlLoader.getDeclaredMethod("getCurrent");
            Object current = getCurrent.invoke(null);
            if (current != null) {
                Method getGameDir = current.getClass().getDeclaredMethod("getGameDir");
                Object path = getGameDir.invoke(current);
                if (path instanceof Path) return (Path) path;
            }
        } catch (Exception ignored) {}
        return null;
    }

    static boolean resolveClientFlag() {
        try {
            Class<?> envClass = Class.forName("net.neoforged.fml.loading.FMLEnvironment");
            try {
                Method getDist = envClass.getDeclaredMethod("getDist");
                Object dist = getDist.invoke(null);
                return dist != null && "CLIENT".equalsIgnoreCase(String.valueOf(dist));
            } catch (NoSuchMethodException ignored) {
                Field distField = envClass.getDeclaredField("dist");
                Object dist = distField.get(null);
                return dist != null && "CLIENT".equalsIgnoreCase(String.valueOf(dist));
            }
        } catch (Exception ignored) {}
        return false;
    }

    static String resolveVersion() {
        try {
            Class<?> fmlLoader = Class.forName("net.neoforged.fml.loading.FMLLoader");
            Method getCurrent = fmlLoader.getDeclaredMethod("getCurrent");
            Object current = getCurrent.invoke(null);
            if (current == null) return null;

            Method versionInfoMethod = current.getClass().getDeclaredMethod("versionInfo");
            Object versionInfo = versionInfoMethod.invoke(current);
            if (versionInfo == null) return null;

            for (String getter : new String[]{"mcVersion", "minecraftVersion"}) {
                try {
                    Method method = versionInfo.getClass().getDeclaredMethod(getter);
                    Object value = method.invoke(versionInfo);
                    if (value instanceof String) return (String) value;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return null;
    }
}
