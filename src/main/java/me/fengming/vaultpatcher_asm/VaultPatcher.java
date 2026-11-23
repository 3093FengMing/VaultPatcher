package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherModule;
import me.fengming.vaultpatcher_asm.core.cache.Caches;
import me.fengming.vaultpatcher_asm.core.misc.ILogger;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.I18n;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VaultPatcher {
    public static ILogger LOGGER = new ILogger();

    public static Path mcPath = null;
    public static String mcVersion = null;
    public static Platform platform = Platform.UNDEFINED;
    public static boolean isClient = false;

    public static void init(Path mcPath, String mcVersion) {
        VaultPatcher.mcPath = mcPath;
        VaultPatcher.mcVersion = mcVersion;

        _init(mcPath);
    }

    private static void _init(Path mcPath) {

        try {
            VaultPatcherConfig.readConfig(mcPath.resolve("config").resolve("vaultpatcher_asm"));
            Utils.debug = VaultPatcherConfig.getDebugMode();
            VaultPatcher.debugInfo("Loading Configs!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: ", e);
        }

        VaultPatcher.debugInfo("Loading I18n!");
        I18n.load(mcPath);

        try {
            VaultPatcher.debugInfo("Loading Patches!");
            ClassPatcher.init(Utils.getVpPath().resolve("patch"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load patch: ", e);
        }

        try {
            VaultPatcher.debugInfo("Loading Caches!");
            Caches.init(Utils.getVpPath().resolve("cache"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cache: ", e);
        }

        try {
            VaultPatcher.debugInfo("Loading Modules!");
            Path p = Utils.getVpPath().resolve("modules");
            if (Files.notExists(p)) {
                Files.createDirectories(p);
            }
            if (VaultPatcherConfig.isLoadAllModules()) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.json")) {
                    for (Path jsonFile : stream) {
                        VaultPatcherModule vpp = new VaultPatcherModule(jsonFile.getFileName().toString());
                        vpp.read();
                    }
                }

            } else {
                List<String> modules = VaultPatcherConfig.getModules();
                for (String module : modules) {
                    VaultPatcherModule vpp = new VaultPatcherModule(module + ".json");
                    vpp.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load modules: ", e);
        }

        // optimization
        Utils.needStacktrace = Utils.dynTranslationInfos.stream().anyMatch(e -> !e.getTargetClassInfo().getDynamicName().isEmpty());
    }

    public static void debugInfo(String s) {
        if (Utils.debug.isEnable()) VaultPatcher.LOGGER.info(s);
    }

    public static void debugInfo(String s, Object... args) {
        if (Utils.debug.isEnable()) VaultPatcher.LOGGER.info(s, args);
    }
}
