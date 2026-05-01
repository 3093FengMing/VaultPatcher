package me.fengming.vaultpatcher_asm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static final DebugMode debug = new DebugMode();
    public static boolean enableClassPatch = false;
    public static boolean loadAllModules = false;
    public static Path config = null;
    public static File configFile = null;
    public static String defaultLanguage = "en_us";
    public static List<String> mods = new ArrayList<>();

    public static List<String> getMods() {
        return mods;
    }

    public static DebugMode getDebugMode() {
        return debug;
    }

    public static boolean isEnableClassPatch() {
        return enableClassPatch;
    }

    public static boolean isLoadAllModules() {
        return loadAllModules;
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    private static void writeConfig(JsonWriter jw) throws IOException {
        jw.setIndent("  ");
        jw.beginObject();

        jw.name("modules");
        jw.beginArray();
        for (String mod : mods) {
            jw.value(mod);
        }
        jw.endArray();

        jw.name("default_language");
        jw.value(defaultLanguage);

        jw.name("class_patch");
        jw.value(enableClassPatch);

        jw.name("load_all_modules");
        jw.value(loadAllModules);

        jw.name("debug_mode");
        debug.writeJson(jw);

        jw.endObject();
        jw.close();
    }

    public static void save() throws IOException {
        JsonWriter jw = new JsonWriter(Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8));
        jw.setIndent("  ");
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
            save();
        }

        JsonReader jr = new JsonReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8));

        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            switch (jr.nextName()) {
                case "debug_mode": {
                    if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                        debug.readJson(jr);
                    }
                    break;
                }
                case "class_patch": {
                    enableClassPatch = jr.nextBoolean();
                    break;
                }
                case "load_all_modules": {
                    loadAllModules = jr.nextBoolean();
                    break;
                }
                case "default_language": {
                    defaultLanguage = jr.nextString();
                    break;
                }
                case "modules":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        mods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
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
