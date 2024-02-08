package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import me.fengming.vaultpatcher_asm.plugin.VaultPatcherPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarFile;

public class VaultPatcher {
    public static Logger LOGGER = LogManager.getLogger();

    public static List<VaultPatcherPlugin> plugins = new ArrayList<>();

    public static void init(Path mcPath) {
        // Load Plugins
        File[] plugins = mcPath.resolve("vaultpatcher").resolve("plugins").toFile().listFiles(f -> f.getName().endsWith(".jar"));
        if (plugins != null) {
            for (File file : plugins) {
                try (JarFile jarFile = new JarFile(file)) {
                    String entryPoint = jarFile.getManifest().getMainAttributes().getValue("VaultPatcherPlugin");
                    if (entryPoint == null) throw new RuntimeException("Failed loading plugin: Couldn't find the entry point");
                    ClassLoader cl1 = Thread.currentThread().getContextClassLoader();
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, cl1);
                    VaultPatcherPlugin plugin = new VaultPatcherPlugin() {
                        private Runnable runnable = null;
                        private final Function instance = classLoader.loadClass(entryPoint).asSubclass(Function.class).newInstance();
                        @Override
                        public void start(Path mcPath) {
                            runnable = (Runnable) this.instance.apply(mcPath);
                        }

                        @Override
                        public void end() {
                            runnable.run();
                        }
                    };
                    VaultPatcher.plugins.add(plugin);
                } catch (Exception e) {
                    throw new RuntimeException("Failed loading plugin: " + file, e);
                }
            }
        }

        _init(mcPath);
    }

    private static void _init(Path mcPath) {
        Utils.mcPath = mcPath;

        plugins.forEach(e -> e.start(mcPath));

        try {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Caches!");
            Caches.initCache(mcPath.resolve("vaultpatcher").resolve("cache"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cache", e);
        }

        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading Configs!");
        try {
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            List<String> mods = VaultPatcherConfig.getMods();
            for (String mod : mods) {
                VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                vpp.read();
                Utils.translationInfos.addAll(vpp.getTranslationInfoList());
                Utils.dynTranslationInfos.addAll(vpp.getDynTranslationInfoList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        plugins.forEach(VaultPatcherPlugin::end);
    }

    public static void debugInfo(String s) {
        if (VaultPatcherConfig.getDebugMode().isEnable()) VaultPatcher.LOGGER.info(s);
    }
}
