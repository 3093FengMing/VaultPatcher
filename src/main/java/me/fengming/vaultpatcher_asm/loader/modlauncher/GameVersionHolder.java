package me.fengming.vaultpatcher_asm.loader.modlauncher;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

final class GameVersionHolder {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    static final boolean IS_NEO = isNeoForge();
    static final String VERSION;

    static {
        try {
            VERSION = IS_NEO ? lookupNeo() : lookupForge();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static boolean isNeoForge() {
        try {
            Class.forName("net.neoforged.api.distmarker.Dist");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static String lookupNeo() throws Throwable {
        Class<?> loaderClass = Class.forName("net.neoforged.fml.loading.FMLLoader");
        Class<?> versionInfoClass = Class.forName("net.neoforged.fml.loading.VersionInfo");

        MethodHandle versionInfoGetter;
        // loader <10: FMLLoader.versionInfo();
        // loader >=10: FMLLoader.getCurrent().getVersionInfo();
        try {
            Object loader = LOOKUP.findStatic(loaderClass, "getCurrent", MethodType.methodType(loaderClass)).invoke();
            versionInfoGetter = LOOKUP.findVirtual(loaderClass, "getVersionInfo", MethodType.methodType(versionInfoClass)).bindTo(loader);
        } catch (NoSuchMethodException e) {
            versionInfoGetter = LOOKUP.findStatic(loaderClass, "versionInfo", MethodType.methodType(versionInfoClass));
        }
        Object versionInfo = versionInfoGetter.invoke();

        // VersionInfo.mcVersion()
        return (String) LOOKUP.findVirtual(versionInfoClass, "mcVersion", MethodType.methodType(String.class)).bindTo(versionInfo).invokeExact();
    }

    private static String lookupForge() throws Throwable {
        // MCPVersion.getMCVersion()
        // Introduced since 1.13, the same time modlauncher was introduced
        Class<?> mcpVersionClass = Class.forName("net.minecraftforge.versions.mcp.MCPVersion");
        return (String) LOOKUP.findStatic(mcpVersionClass, "getMCVersion", MethodType.methodType(String.class)).invokeExact();
    }
}
