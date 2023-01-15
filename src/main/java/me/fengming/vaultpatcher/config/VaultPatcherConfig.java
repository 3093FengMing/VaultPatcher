package me.fengming.vaultpatcher.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VaultPatcherConfig {
    private static final Gson GSON = new Gson();
    private static final Path configFile = FMLPaths.CONFIGDIR.get().resolve("vaultpatcher").resolve("config.json");
    private static List<String> mods = new ArrayList<>();
    private static DebugMode debug = new DebugMode();

    public static List<String> getMods() {
        return mods;
    }

    public static DebugMode getDebugMode() {
        return debug;
    }

    public static void readConfig() throws IOException {
        File f = configFile.toFile();
        if (!f.exists()) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            JsonWriter jw = GSON.newJsonWriter(Files.newBufferedWriter(configFile));
            debug.writeJson(jw);
            jw.name("mods").beginArray();
            jw.name("mods").endArray();
        }
        JsonReader jr = GSON.newJsonReader(Files.newBufferedReader(configFile));

        jr.beginObject();
        switch (jr.nextName()) {
            case "mods" :
                if (jr.peek() == JsonToken.BEGIN_ARRAY) {
                    mods = GSON.fromJson(jr, new TypeToken<List<String>>() {}.getType());
                }
                break;
            case "debug_mode" :
                if (jr.peek() == JsonToken.BEGIN_OBJECT) {
                    debug.readJson(jr);
                }
                break;
            default : jr.skipValue();
        }
        jr.endObject();
    }
}
