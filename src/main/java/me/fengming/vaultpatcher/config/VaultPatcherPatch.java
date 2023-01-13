package me.fengming.vaultpatcher.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VaultPatcherPatch {
    private static final Gson GSON = new Gson();

    public VaultPatcherPatch(String patchFile) {
        Path p = FMLPaths.CONFIGDIR.get().resolve("vaultpatcher").resolve(patchFile);
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
    }

    public void readConfig() throws IOException {
        if (Files.notExists(patchFile)) {
            throw new IOException("File is not exists.");
        }
        try (JsonReader jsonReader = GSON.newJsonReader(Files.newBufferedReader(patchFile))) {
            readConfig(jsonReader);
        }
    }
    
    private List<TranslationInfo> getList(String str) {
        Set<String> set = map.keySet();
        for (String s : set) {
            if (str.contains(s)) {
                return map.get(s);
            }
        }
        return null;
    }

    public String patch(String text, StackTraceElement[] stackTrace) {
        List<TranslationInfo> list;
        if ((list = getList(text)) == null) return null;

        for (TranslationInfo info : list) {
            if (info.getValue() == null || info.getKey() == null) continue;
            final TargetClassInfo targetClassInfo = info.getTargetClassInfo();
            if (targetClassInfo.getName().isEmpty() || targetClassInfo.getStackDepth() <= 0 || matchStack(targetClassInfo.getName(), stackTrace)) {
                return patchText(info.getValue(), info.getKey(), text);
            }
            int index = targetClassInfo.getStackDepth();
            if (index >= stackTrace.length) continue;
            if (stackTrace[index].getClassName().contains(targetClassInfo.getName())) {
                return patchText(info.getValue(), info.getKey(), text);
            }
        }

        return null;
    }

    private boolean matchStack(String str, StackTraceElement[] stackTrace) {
        String s = str.toLowerCase();
        for (StackTraceElement ste : stackTrace) {
            if (s.startsWith("#")) {
                return ste.getClassName().endsWith(s);
            } else if (s.startsWith("@")) {
                return ste.getClassName().startsWith(s);
            } else return s.equals(ste.getClassName());
        }
        return false;
    }

    private String patchText(String value, String key, String text) {
        char[] charList = {};
        List<Integer> numList = null;
        if (value.charAt(0) != '@' || value.contains("%d")) {
            if (value.length() == text.length()) {
                text.getChars(0, text.length(), charList, 0);
                numList = getNumbers(charList);
            }
        }

        if (value.startsWith("@") && !value.startsWith("@@")) {
            value = value.replace("@@", "@").substring(1);
            return text.replace(key, I18n.get(value));
        } else return I18n.get(value, numList);
    }

    private List<Integer> getNumbers(char[] str) {
        List<Integer> rList = new ArrayList<>();
        StringBuilder tmp = new StringBuilder();
        for (char c : str) {
            if (c >= '0' && c <= '9') {
                tmp.append(c);
            } else {
                rList.add(Integer.valueOf(tmp.toString()));
            }
        }
        return rList;
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
