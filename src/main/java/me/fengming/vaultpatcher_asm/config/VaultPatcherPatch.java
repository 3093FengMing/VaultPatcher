package me.fengming.vaultpatcher_asm.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.fengming.vaultpatcher_asm.VaultPatcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VaultPatcherPatch {
    private static final Gson GSON = new Gson();
    private final Path patchFile;
    private final List<TranslationInfo> translationInfoList = new ArrayList<>();
    private boolean dynamic = false;

    public VaultPatcherPatch(String patchFile) {
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

        PatchInfo patchInfo = new PatchInfo();
        patchInfo.readJson(reader);
        dynamic = patchInfo.isDynamic();
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Loading %s!", patchInfo.getName()));
        VaultPatcher.debugInfo(/*JustPretty*/"[VaultPatcher] About Information:");
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Author(s): %s", patchInfo.getAuthors()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Apply to Mod(s): %s", patchInfo.getMods()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Description: %s", patchInfo.getDesc()));
        VaultPatcher.debugInfo(String.format("[VaultPatcher] Dynamic: %s", patchInfo.isDynamic()));

        while (reader.peek() != JsonToken.END_ARRAY) {
            TranslationInfo translationInfo = new TranslationInfo();
            translationInfo.readJson(reader);
            translationInfoList.add(translationInfo);
        }

        reader.endArray();
    }

    public void read() throws IOException {
        if (Files.notExists(patchFile)) {
            Files.createFile(patchFile);
        }
        try (JsonReader jsonReader = GSON.newJsonReader(new InputStreamReader(Files.newInputStream(patchFile), StandardCharsets.UTF_8))) {
            read(jsonReader);
        }
    }

    public List<TranslationInfo> getTranslationInfoList() {
        return dynamic ? new ArrayList<>() : translationInfoList;
    }

    public List<TranslationInfo> getDynTranslationInfoList() {
        return dynamic ? translationInfoList : new ArrayList<>();
    }
}
