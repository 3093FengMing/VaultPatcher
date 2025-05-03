package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.I18n;
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
    private final List<TranslationInfo> translationInfoList = new ArrayList<>();
    private boolean dynamic;

    public VaultPatcherModule(String moduleFile) {
        VaultPatcher.debugInfo("[VaultPatcher] Found Module {}",moduleFile);
        Path p = VaultPatcherConfig.config.resolve(moduleFile);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create", e);
        }
        this.moduleFile = p;
    }

    public void read(JsonReader reader) throws IOException {
        reader.beginArray();

        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.readJson(reader);
        dynamic = moduleInfo.isDataDynamic();
        VaultPatcher.debugInfo("[VaultPatcher] Loading Module: {}, Author(s): {}, Mod(s): {}, Desc: {}, Dyn: {}, I18n: {}",
                moduleInfo.getInfoName(), moduleInfo.getInfoAuthors(), moduleInfo.getInfoMods(),
                moduleInfo.isDataDynamic(), moduleInfo.isDataI18n());

        while (reader.peek() != JsonToken.END_ARRAY) {
            reader.beginObject();

            List<TargetClassInfo> targetClassInfos = new ArrayList<>();
            TranslationInfo.Mutable mutable = new TranslationInfo.Mutable();
            Pairs pairs = new Pairs(dynamic);

            while (reader.peek() != JsonToken.END_OBJECT) {
                switch (reader.nextName()) {
                    case "i":
                    case "i18n": {
                        mutable.setI18n(reader.nextBoolean());
                        break;
                    }
                    case "s":
                    case "target_classes": {
                        reader.beginArray();
                        while (reader.peek() != JsonToken.END_ARRAY) {
                            TargetClassInfo targetClassInfo = new TargetClassInfo();
                            targetClassInfo.readJson(reader);
                            targetClassInfos.add(targetClassInfo);
                        }
                        reader.endArray();
                        break;
                    }
                    case "t":
                    case "target_class": {
                        TargetClassInfo targetClassInfo = new TargetClassInfo();
                        targetClassInfo.readJson(reader);
                        targetClassInfos.add(targetClassInfo);
                        break;
                    }
                    case "k":
                    case "key": {
                        pairs.setKey(reader.nextString());
                        break;
                    }
                    case "v":
                    case "value": {
                        boolean i18n = moduleInfo.isDataI18n() || mutable.isI18n();
                        pairs.setValue(i18n ? I18n.getValue(reader.nextString()) : reader.nextString());
                        break;
                    }
                    case "p":
                    case "pairs": {
                        pairs.readJson(reader, moduleInfo.isDataI18n() || mutable.isI18n());
                        break;
                    }
                    default: {
                        reader.skipValue();
                        break;
                    }
                }
            }
            mutable.setPairs(pairs);
            for (TargetClassInfo targetClassInfo : targetClassInfos) {
                translationInfoList.add(mutable.setTargetClassInfo(targetClassInfo));
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

        TranslationInfo translationInfo = new TranslationInfo();
        translationInfo.write(writer);

        writer.endArray();
        writer.close();
    }

    public void read() throws IOException {
        if (Files.notExists(moduleFile)) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Not Found Module File {}, this file will be created and populated with initial content.",moduleFile);
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

    public List<TranslationInfo> getTranslationInfoList() {
        return dynamic ? Utils.EMPTY_LIST : translationInfoList;
    }

    public List<TranslationInfo> getDynTranslationInfoList() {
        return dynamic ? translationInfoList : Utils.EMPTY_LIST;
    }
}
