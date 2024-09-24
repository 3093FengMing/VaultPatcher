package me.fengming.vaultpatcher_asm.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class I18n {
    private static final Gson GSON = new Gson();
    private static String currentCode = "en_us";
    private static Map<String, String> langugesMap = new HashMap<>();

    public static void load(Path mcPath) {
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        try {
            // Only In Client
            Path optionsFile = mcPath.resolve("options.txt");
            if (Utils.isClient && Files.exists(optionsFile)) {
                br1 = Files.newBufferedReader(optionsFile);
                String line;
                while ((line = br1.readLine()) != null) {
                    if (line.startsWith("lang:")) {
                        currentCode = line.substring("lang:".length());
                        break;
                    }
                }
            } else {
                currentCode = VaultPatcherConfig.defaultLanguage;
            }

            Path i18nPath = Utils.getVpPath().resolve("i18n");
            if (Files.notExists(i18nPath)) {
                Files.createDirectories(i18nPath);
            }
            if (Files.notExists(i18nPath.resolve(currentCode + ".json"))) {
                VaultPatcher.LOGGER.error("Not found file {}.json", currentCode);
                return;
            }

            br2 = Files.newBufferedReader(i18nPath.resolve(currentCode + ".json"));
            langugesMap = GSON.fromJson(br2, new TypeToken<Map<String, String>>() {}.getType());
            if (langugesMap == null) {
                langugesMap = new HashMap<>();
                VaultPatcher.LOGGER.error("Error loading I18n file.");
            }
        } catch (Exception e) {
            VaultPatcher.LOGGER.error("Error loading I18n file: {}", e);
        } finally {
            try {
                if (br1 != null) {
                    br1.close();
                }
                if (br2 != null) {
                    br2.close();
                }
            } catch (Exception e) {
                VaultPatcher.LOGGER.error("Error loading I18n file: {}", e);
            }
        }
    }

    public static String getValue(String key) {
        return langugesMap.getOrDefault(key, key);
    }
}
