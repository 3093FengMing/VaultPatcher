package me.fengming.vaultpatcher.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VaultPatcherConfig {
    private static final Gson GSON = new Gson();
    private static List<String> mods = new ArrayList<>();
    private static List<String> classes = new ArrayList<>();
    private static boolean allClasses = false;
    private static final DebugMode debug = new DebugMode();

    public static Path config = null;

    public static List<String> getMods() {
        return mods;
    }

    public static List<String> getClasses() {
        return classes;
    }

    public static boolean isAllClasses() {
        return allClasses;
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

        jw.name("all_classes");
        jw.value(false);

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

        JsonReader jr = GSON.newJsonReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));

        jr.beginObject();
        while (jr.peek() != JsonToken.END_OBJECT) {
            switch (jr.nextName()) {
                case "debug_mode":
                    if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                        debug.readJson(jr);
                    }
                    break;
                case "mods":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        mods = GSON.fromJson(jr, new TypeToken<List<String>>(){}.getType());
                    }
                    break;
                case "classes":
                    if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                        classes = GSON.fromJson(jr, new TypeToken<List<String>>(){}.getType());
                    }
                    break;
                case "all_classes":
                    if (jr.peek() == JsonToken.BOOLEAN) {
                        allClasses = jr.nextBoolean();
                    }
                default:
                    jr.skipValue();
                    break;
            }
        }
        jr.endObject();
    }
}
