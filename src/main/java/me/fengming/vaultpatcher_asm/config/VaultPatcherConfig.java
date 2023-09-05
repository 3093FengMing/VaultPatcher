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
    private static final DebugMode debug = new DebugMode();
    public static Path config = null;
    private static List<String> mods = new ArrayList<>();
    private static List<String> classes = new ArrayList<>();
    private static List<String> applyMods = new ArrayList<>();

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
        jw.value("example");
        jw.endArray();

        jw.name("classes");
        jw.beginArray();
        jw.endArray();

        jw.name("apply_mods");
        jw.beginArray();
        jw.endArray();

        jw.name("debug_mode");
        debug.writeJson(jw);

        jw.endObject();
        jw.close();
    }

    public static void readConfig(Path path) throws IOException {
        config = path;
        Path configFile = config.resolve("config.json");
        File f = configFile.toFile();
        if (Files.notExists(configFile)) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            Files.createFile(configFile);
            JsonWriter jw = GSON.newJsonWriter(Files.newBufferedWriter(configFile, StandardCharsets.UTF_8));
            writeConfig(jw);
        }

        JsonReader jr = GSON.newJsonReader(new InputStreamReader(Files.newInputStream(f.toPath()), StandardCharsets.UTF_8));

        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            switch (jr.nextName()) {
                case "debug_mode": {
                    if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                        debug.readJson(jr);
                    }
                    break;
                }
                case "mods": {
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        mods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                }
                case "classes": {
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        classes = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                }
                case "apply_mods": {
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        applyMods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                    }
                    break;
                }
                default: {
                    jr.skipValue();
                    break;
                }
            }
        }
        jr.endObject();
    }
}
