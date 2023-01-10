package me.fengming.vaultpatcher.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VaultPatcherPatch {
    private static final Gson GSON = new Gson();

    public VaultPatcherPatch(String patchFile) {
        System.out.println(patchFile);
        Path p = FMLPaths.CONFIGDIR.get().resolve("vaultpatcher").resolve(patchFile);
        System.out.println(p);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            VaultPatcher.LOGGER.error("Failed to create {}", p.getParent(), e);
            throw new RuntimeException(e);
        }
        this.patchFile = p;
    }

    private final Path patchFile;

    private Map<String, List<TranslationInfo>> map = new HashMap<>();

    private PatchInfo info = new PatchInfo();

    private static <K, T> void addEntry(Map<K, List<T>> p, K key, T val) {
        p.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
    }

    public void readConfig(JsonReader reader) throws IOException {
        reader.beginArray();

        PatchInfo patchInfo = new PatchInfo();
        patchInfo.readJson(reader);
        info = patchInfo;

        Map<String, List<TranslationInfo>> m = new HashMap<>();
        while (reader.peek() != JsonToken.END_ARRAY) {
            TranslationInfo translationInfo = new TranslationInfo();
            translationInfo.readJson(reader);
            addEntry(m, translationInfo.getKey(), translationInfo);
        }

        reader.endArray();
        map = m;
        VaultPatcher.LOGGER.info("map = " + map);
    }

    public void readConfig() throws IOException {
        if (Files.notExists(patchFile)) {
            throw new IOException("File is not exists.");
        }
        try (var jsonReader = GSON.newJsonReader(Files.newBufferedReader(patchFile))) {
            readConfig(jsonReader);
        }
    }

    public List<TranslationInfo> getList(Map<String, List<TranslationInfo>> map, String regex) {
        Set<String> set = map.keySet();
        for (String e : set) {
            if (e.matches(regex)) {
                return map.get(e);
            }
        }
        return map.get(regex);
    }

    public String patch(String text, StackTraceElement[] stackTrace) {
        List<TranslationInfo> list;
        if ((list = map.get(text)) == null) return null;

        for (TranslationInfo info : list) {
            if (info.getValue() == null || info.getKey() == null) continue;
            final TargetClassInfo targetClassInfo = info.getTargetClassInfo();
            if (targetClassInfo.getName().isEmpty() || targetClassInfo.getStackDepth() <= 0 || fuzzyMatch(targetClassInfo.getName(), stackTrace)) {
                return patchFormattingText(info.getValue(), text);
            }
            int index = targetClassInfo.getStackDepth();
            if (index >= stackTrace.length) continue;
            if (stackTrace[index].getClassName().contains(targetClassInfo.getName())) {
                return I18n.get(info.getValue());
            }
        }

        // not satisfied
        return null;
    }

    private boolean fuzzyMatch(String str, StackTraceElement[] stackTrace) {
        var s = str.toLowerCase();
        for (StackTraceElement ste : stackTrace) {
            if (s.startsWith("#")) {
                return ste.getClassName().endsWith(s);
            } else if (s.startsWith("@")) {
                return ste.getClassName().startsWith(s);
            } else return s.equals(ste.getClassName());
        }
        return false;
    }

    private String patchFormattingText(String s, String text) {
        /*
        int[] ftList = {};
        String ttext = text.replaceAll("\\d", "%d");
        ttext = ttext.replaceAll("\\w+", "%s");
        if (s.length() == text.length() && ttext.equals(s)) {
            s.substring(s.indexOf("%d"));
        }
        */
        return I18n.get(s);
    }

    @Override
    public String toString() {
        return "VaultPatcherPatch{" +
                "patchFile=" + patchFile +
                ", map=" + map +
                ", info=" + info +
                '}';
    }
}
