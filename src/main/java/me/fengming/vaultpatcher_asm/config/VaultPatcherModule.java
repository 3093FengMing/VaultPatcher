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
    private final Path patchFile;
    private final List<TranslationInfo> translationInfoList = new ArrayList<>();
    private boolean dynamic;

    public VaultPatcherModule(String patchFile) {
        VaultPatcher.debugInfo("[VaultPatcher] Found Module " + patchFile);
        Path p = VaultPatcherConfig.config.resolve(patchFile);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create", e);
        }
        this.patchFile = p;
    }

    public void read(JsonReader reader) throws IOException {
        reader.beginArray();

        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.readJson(reader);
        dynamic = moduleInfo.isDataDynamic();
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Loading %s!", moduleInfo.getInfoName()));
        VaultPatcher.debugInfo(/*JustPretty*/"[VaultPatcher] About Information:");
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Author(s): %s", moduleInfo.getInfoAuthors()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Mod(s): %s", moduleInfo.getInfoMods()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Description: %s", moduleInfo.getInfoDesc()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Dynamic: %s", moduleInfo.isDataDynamic()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] I18n: %s", moduleInfo.isDataI18n()));

        while (reader.peek() != JsonToken.END_ARRAY) {
            reader.beginObject();

            List<TargetClassInfo> targetClassInfos = new ArrayList<>();
            TranslationInfo.Mutable mutable = new TranslationInfo.Mutable();
            Pairs pairs = new Pairs(dynamic);

            while (reader.peek() != JsonToken.END_OBJECT) {
                switch (reader.nextName()) {
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
                        pairs.setValue(moduleInfo.isDataI18n() ? Utils.getI18n(reader.nextString()) : reader.nextString());
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
        if (Files.notExists(patchFile)) {
            Files.createFile(patchFile);
            try (JsonWriter jw = new JsonWriter(Files.newBufferedWriter(patchFile, StandardCharsets.UTF_8))) {
                jw.setIndent("  ");
                write(jw);
            }
        }
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(Files.newInputStream(patchFile), StandardCharsets.UTF_8))) {
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
