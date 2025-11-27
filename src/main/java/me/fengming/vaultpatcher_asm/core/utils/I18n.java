package me.fengming.vaultpatcher_asm.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;

import java.io.BufferedReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class I18n {
    private static final Gson GSON = new Gson();
    private static final Path PATH = Utils.getVpPath().resolve("i18n");
    private static String currentCode = "en_us";
    private static Map<String, String> langugesMap = new HashMap<>();

    public static void load(Path mcPath) {
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        try {

            // Only In Client
            Path optionsFile = mcPath.resolve("options.txt");
            if (VaultPatcher.isClient && Files.exists(optionsFile)) {
                try {
                    br1 = Files.newBufferedReader(optionsFile, StandardCharsets.UTF_8);
                    br1.lines().filter(line -> line.startsWith("lang:"))
                            .findFirst()
                            .ifPresent(line -> currentCode = line.substring("lang:".length()));
                } catch (UncheckedIOException e) {
                    br1 = Files.newBufferedReader(optionsFile, Charset.defaultCharset());
                    br1.lines().filter(line -> line.startsWith("lang:"))
                            .findFirst()
                            .ifPresent(line -> currentCode = line.substring("lang:".length()));
                }
                if (notExists()) {
                    currentCode = VaultPatcherConfig.getDefaultLanguage();
                    if (notExists()) currentCode = "en_us";
                }
            } else {
                currentCode = VaultPatcherConfig.getDefaultLanguage();
            }

            if (Files.notExists(PATH)) {
                Files.createDirectories(PATH);
            }
            if (Files.notExists(PATH.resolve(currentCode + ".json"))) {
                VaultPatcher.LOGGER.warn("[VaultPatcher] Not found file {}.json. Will skip I18n loading.", currentCode);
                return;
            }

            br2 = Files.newBufferedReader(PATH.resolve(currentCode + ".json"));
            langugesMap = GSON.fromJson(br2, new TypeToken<Map<String, String>>() {}.getType());
            if (langugesMap == null) {
                langugesMap = new HashMap<>();
                VaultPatcher.LOGGER.error("[VaultPatcher] Error loading I18n file.");
            }
        } catch (Exception e) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Error loading I18n file: {}", e);
        } finally {
            try {
                if (br1 != null) {
                    br1.close();
                }
                if (br2 != null) {
                    br2.close();
                }
            } catch (Exception e) {
                VaultPatcher.LOGGER.error("[VaultPatcher] Error loading I18n file: {}", e);
            }
        }
    }

    private static boolean notExists() {
        return Files.notExists(PATH.resolve(currentCode + ".json"));
    }

    public static String getValue(String key) {
        return langugesMap.getOrDefault(key, key);
    }
}
