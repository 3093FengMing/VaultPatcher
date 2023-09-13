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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultPatcherPatch {
    private static final Gson GSON = new Gson();
    private final Path patchFile;
    private final List<TranslationInfo> translationInfoList = new ArrayList<>();

    public VaultPatcherPatch(String patchFile) {
        VaultPatcher.LOGGER.info("[VaultPatcher] Found Module " + patchFile);
        Path p = VaultPatcherConfig.config.resolve(patchFile);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Failed to create {}", p.getParent(), e);
            throw new RuntimeException(e);
        }
        this.patchFile = p;
    }

    public void read(JsonReader reader) throws IOException {
        reader.beginArray();

        PatchInfo patchInfo = new PatchInfo();
        patchInfo.readJson(reader);
        VaultPatcher.LOGGER.info("[VaultPatcher] Loading {}!", patchInfo.getName());
        VaultPatcher.LOGGER.info("[VaultPatcher] About Information:\nAuthor(s): {}\nApply to Mod(s): {}\nDescription: {}", patchInfo.getAuthors(), patchInfo.getMods(), patchInfo.getDesc());

        Map<String, List<TranslationInfo>> m = new HashMap<>();
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
        return translationInfoList;
    }
}
