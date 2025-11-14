package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherModule;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.TransformChecker;
import me.fengming.vaultpatcher_asm.core.utils.I18n;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import me.fengming.vaultpatcher_asm.plugin.VaultPatcherPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class VaultPatcher {
    public static Logger LOGGER = LogManager.getLogger("VaultPatcher");

    public static List<VaultPatcherPlugin> plugins = new ArrayList<>();
    public static Path mcPath = null;
    public static String mcVersion = null;
    @Deprecated
    public static Platform javaVersion = Platform.getJavaVersion(System.getProperty("java.specification.version"));
    public static Platform platform = Platform.UNDEFINED;
    public static boolean isClient = false;

    public static void init(Path mcPath, String mcVersion) {
        VaultPatcher.mcPath = mcPath;
        VaultPatcher.mcVersion = mcVersion;

        Path pluginsPath = Utils.getVpPath().resolve("plugins");
        if (Files.notExists(pluginsPath)) {
            try {
                Files.createDirectories(pluginsPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed creating plugin directory: ", e);
            }
        }

        // Load Plugins
        File[] plugins = pluginsPath.toFile().listFiles(f -> f.getName().endsWith(".jar"));
        if (plugins != null) {
            for (File file : plugins) {
                try (JarFile jarFile = new JarFile(file)) {
                    String entryPoint = jarFile.getManifest().getMainAttributes().getValue("VaultPatcherPlugin");
                    if (entryPoint == null)
                        throw new RuntimeException("Failed loading plugin " + file.getName() + ": Couldn't find the entry point");

                    ClassLoader parentClassLoader = VaultPatcher.class.getClassLoader();
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, parentClassLoader);
                    VaultPatcherPlugin plugin = classLoader.loadClass(entryPoint).asSubclass(VaultPatcherPlugin.class).newInstance();

                    VaultPatcher.plugins.add(plugin);
                } catch (Exception e) {
                    throw new RuntimeException("Failed loading plugin: " + file, e);
                }
            }
        }

        _init(mcPath);
    }

    private static void _init(Path mcPath) {
        plugins.forEach(e -> e.start(mcPath));

        try {
            plugins.forEach(e -> e.onLoadConfig(VaultPatcherPlugin.Phase.BEFORE));
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            Utils.debug = VaultPatcherConfig.getDebugMode();
            VaultPatcher.debugInfo("[VaultPatcher] Loading Configs!");
            plugins.forEach(e -> e.onLoadConfig(VaultPatcherPlugin.Phase.AFTER));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: ", e);
        }

        VaultPatcher.debugInfo("[VaultPatcher] Loading I18n!");
        I18n.load(mcPath);

        try {
            VaultPatcher.debugInfo("[VaultPatcher] Loading Patches!");
            plugins.forEach(e -> e.onLoadPatches(VaultPatcherPlugin.Phase.BEFORE));
            ClassPatcher.init(Utils.getVpPath().resolve("patch"));
            plugins.forEach(e -> e.onLoadPatches(VaultPatcherPlugin.Phase.AFTER));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load patch: ", e);
        }

        try {
            VaultPatcher.debugInfo("[VaultPatcher] Loading Caches!");
            plugins.forEach(e -> e.onLoadCaches(VaultPatcherPlugin.Phase.BEFORE));
            Caches.init(Utils.getVpPath().resolve("cache"));
            plugins.forEach(e -> e.onLoadCaches(VaultPatcherPlugin.Phase.AFTER));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cache: ", e);
        }

        try {
            VaultPatcher.debugInfo("[VaultPatcher] Loading Modules!");
            Path p = Utils.getVpPath().resolve("modules");
            if (Files.notExists(p)){
                Files.createDirectories(p);
            }
            List<String> mods = VaultPatcherConfig.getMods();
            for (String mod : mods) {
                VaultPatcherModule vpp = new VaultPatcherModule(mod + ".json");
                plugins.forEach(e -> e.onLoadModule(vpp, VaultPatcherPlugin.Phase.BEFORE));
                vpp.read();
                plugins.forEach(e -> e.onLoadModule(vpp, VaultPatcherPlugin.Phase.AFTER));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load modules: ", e);
        }

        TransformChecker.init();

        // optimization
        Utils.needStacktrace = Utils.dynTranslationInfos.stream().anyMatch(e -> !e.getTargetClassInfo().getDynamicName().isEmpty());

        plugins.forEach(VaultPatcherPlugin::end);
    }

    public static void debugInfo(String s) {
        if (Utils.debug.isEnable()) VaultPatcher.LOGGER.info(s);
    }

    public static void debugInfo(String s, Object... args) {
        if (Utils.debug.isEnable()) VaultPatcher.LOGGER.info(s, args);
    }
}
