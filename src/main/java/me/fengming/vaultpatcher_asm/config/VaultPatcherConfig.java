package me.fengming.vaultpatcher_asm.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VaultPatcherConfig {
    private static final Gson GSON = new Gson();
    public static final DebugMode debug = new DebugMode();
    public static Path config = null;
    public static File configFile = null;
    public static List<String> mods = new ArrayList<>();
    public static List<String> classes = new ArrayList<>();
    public static List<String> applyMods = new ArrayList<>();

    public static List<String> getMods() {
        return mods;
    }

    public static List<String> getClasses() {
        return classes;
    }

    public static List<String> getApplyMods() {
        return applyMods;
    }

    public static DebugMode getDebugMode() {
        return debug;
    }

    private static void writeConfig(JsonWriter jw) throws IOException {
        jw.setIndent("  ");
        jw.beginObject();

        jw.name("mods");
        jw.beginArray();
        mods.forEach(e -> {
            try {
                jw.value(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        jw.endArray();

        jw.name("classes");
        jw.beginArray();
        classes.forEach(e -> {
            try {
                jw.value(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        jw.endArray();

        jw.name("apply_mods");
        jw.beginArray();
        applyMods.forEach(e -> {
            try {
                jw.value(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        jw.endArray();

        jw.name("debug_mode");
        debug.writeJson(jw);

        jw.endObject();
        jw.close();
    }

    public static void save() throws IOException {
        JsonWriter jw = GSON.newJsonWriter(Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8));
        writeConfig(jw);
    }

    public static void readConfig(Path path) throws IOException {
        config = path;
        configFile = config.resolve("config.json").toFile();
        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            configFile.createNewFile();
            JsonWriter jw = GSON.newJsonWriter(Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8));
            writeConfig(jw);
        }

        JsonReader jr = GSON.newJsonReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8));

        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            switch (jr.nextName()) {
                case "d":
                case "debug_mode":
                    if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                        debug.readJson(jr);
                    }
                    break;
                case "m":
                case "mods":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        mods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                case "c":
                case "classes":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        classes = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                case "a":
                case "apply_mods":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        applyMods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                default: {
                    jr.skipValue();
                    break;
                }
            }
        }
        jr.endObject();
    }
}
