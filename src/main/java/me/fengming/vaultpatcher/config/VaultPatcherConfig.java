package me.fengming.vaultpatcher.config;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VaultPatcherConfig {
    private static final Gson GSON = new Gson();
    private static final Supplier<VaultPatcherConfig> INSTANCE = Suppliers.memoize(() -> {
        Path p = FMLPaths.CONFIGDIR.get().resolve("vaultpatcher").resolve("config.json");
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            VaultPatcher.LOGGER.error("Failed to create {}", p.getParent(), e);
            throw new RuntimeException(e);
        }
        return new VaultPatcherConfig(p);
    });

    private final Path configFile;

    private Map<String, List<TranslationInfo>> map = new HashMap<>();

    public VaultPatcherConfig(Path configFile) {
        this.configFile = configFile;
    }

    private static <K, T> void addEntry(Map<K, List<T>> p, K key, T val) {
        p.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
    }

    public static VaultPatcherConfig getInstance() {
        return INSTANCE.get();
    }

    public void readConfig(JsonReader reader) throws IOException {
        reader.beginArray();
        Map<String, List<TranslationInfo>> m = new HashMap<>();
        while (reader.peek() != JsonToken.END_ARRAY) {
            TranslationInfo translationInfo = new TranslationInfo();
            translationInfo.readJson(reader);
            addEntry(m, translationInfo.getKey(), translationInfo);
        }
        reader.endArray();
        map = m;
    }

    public void writeConfig(JsonWriter writer) throws IOException {
        writer.beginArray();
        for (List<TranslationInfo> list : map.values()) {
            for (TranslationInfo info : list) {
                info.writeJson(writer);
            }
        }
        writer.endArray();
    }

    public void writeConfig() throws IOException {
        try (JsonWriter jsonWriter = GSON.newJsonWriter(Files.newBufferedWriter(configFile))) {
            writeConfig(jsonWriter);
        }
    }

    public void readConfig() throws IOException {
        if (Files.notExists(configFile)) {
            writeConfig();
            return;
        }
        try (JsonReader jsonReader = GSON.newJsonReader(Files.newBufferedReader(configFile))) {
            readConfig(jsonReader);
        }
    }

    public String patch(String text, StackTraceElement[] stackTrace) {
        List<TranslationInfo> list;
        if ((list = map.get(text)) == null) return null;

        for (TranslationInfo info : list) {
            final TargetClassInfo targetClassInfo = info.getTargetClassInfo();
            if (targetClassInfo.getName().isEmpty() || targetClassInfo.getStackDepth() <= 0) {
                return I18n.get(info.getValue());
            }
            int index = targetClassInfo.getStackDepth();
            if (index >= stackTrace.length) continue;
            if (info.getKey().contains(stackTrace[index].getClassName()) || fuzzyMatch(info.getKey(), stackTrace)) {
                return I18n.get(info.getValue());
            }
        }

        // not satisfied
        return null;
    }

    private boolean fuzzyMatch(String str, StackTraceElement[] stackTrace) {
        String s = str.toLowerCase();
        for (StackTraceElement ste : stackTrace) {
            return s.startsWith("#") ? ste.getClassName().endsWith(s) : s.equals(ste.getClassName());
        }
        return false;
    }
}
