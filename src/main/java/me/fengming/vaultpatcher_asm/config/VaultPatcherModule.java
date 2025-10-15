package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VaultPatcherModule {
    private final Path moduleFile;

    public VaultPatcherModule(String moduleFile) {
        VaultPatcher.debugInfo("[VaultPatcher] Found Module {}",moduleFile);
        Path p = Utils.getVpPath().resolve("modules").resolve(moduleFile);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory for module file: ", e);
        }
        this.moduleFile = p;
    }

    public void read(JsonReader reader) throws IOException {
        reader.beginArray();

        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.readJson(reader);
        boolean dynamic = moduleInfo.isDataDynamic();
        VaultPatcher.debugInfo("[VaultPatcher] Loading Module: {}, Author(s): {}, Mod(s): {}, Desc: {}, Dyn: {}, I18n: {}",
                moduleInfo.getInfoName(), moduleInfo.getInfoAuthors(), moduleInfo.getInfoMods(),
                moduleInfo.isDataDynamic(), moduleInfo.isDataI18n());

        while (reader.peek() != JsonToken.END_ARRAY) {
            reader.beginObject();

            List<String> targetClasses = new ArrayList<>();
            TargetClassInfo targetClassInfo = new TargetClassInfo();
            Pairs pairs = new Pairs(dynamic);

            while (reader.peek() != JsonToken.END_OBJECT) {
                switch (reader.nextName()) {
                    case "t":
                    case "s":
                    case "target_class":
                    case "target_classes": {
                        reader.beginArray();
                        while (reader.peek() != JsonToken.END_ARRAY) {
                            targetClasses.add(reader.nextString());
                        }
                        reader.endArray();
                        break;
                    }
                    case "info": {
                        targetClassInfo.readJson(reader);
                        break;
                    }
                    case "p":
                    case "pairs": {
                        pairs.readJson(reader, moduleInfo.isDataI18n());
                        break;
                    }
                    default: {
                        reader.skipValue();
                        break;
                    }
                }
            }
            for (String targetClass : targetClasses) {
                TranslationInfo.Mutable mutable = new TranslationInfo.Mutable(targetClass);
                mutable.setPairs(pairs).setTargetClassInfo(targetClassInfo);
                Utils.addTranslationInfo(targetClass, mutable);
            }

            reader.endObject();
        }
        reader.endArray();
    }

    public void write(JsonWriter writer) throws IOException {
        writer.setIndent("  ");
        writer.beginArray();

        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.writeJson(writer);

        writer.endArray();
        writer.close();
    }

    public void read() throws IOException {
        if (Files.notExists(moduleFile)) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Not Found Module File {}, this file will be created and populated with initial content.", moduleFile);
            Files.createFile(moduleFile);
            try (JsonWriter jw = new JsonWriter(Files.newBufferedWriter(moduleFile, StandardCharsets.UTF_8))) {
                jw.setIndent("  ");
                write(jw);
            }
        }
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(Files.newInputStream(moduleFile), StandardCharsets.UTF_8))) {
            read(jsonReader);
        }
    }
}
