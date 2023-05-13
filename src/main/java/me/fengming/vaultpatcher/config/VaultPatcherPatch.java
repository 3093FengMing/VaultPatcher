package me.fengming.vaultpatcher.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.fengming.vaultpatcher.Utils;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VaultPatcherPatch {
    private static final Gson GSON = new Gson();
    private final Path patchFile;
    private Map<String, List<TranslationInfo>> map = new HashMap<>();
    private PatchInfo info = new PatchInfo();
    private String pname = "";

    public VaultPatcherPatch(String patchFile) {
        this.pname = patchFile;
        VaultPatcher.LOGGER.info("Load Module " + patchFile);
        Path p = FMLPaths.CONFIGDIR.get().resolve("vaultpatcher").resolve(patchFile);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            VaultPatcher.LOGGER.error("Failed to create {}", p.getParent(), e);
            throw new RuntimeException(e);
        }
        this.patchFile = p;
    }

    private static <K, T> void addEntry(Map<K, List<T>> p, K key, T val) {
        p.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
    }

    public void read(JsonReader reader) throws IOException {
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

    public void read() throws IOException {
        if (Files.notExists(patchFile)) {
            Files.createFile(patchFile);
        }
        try (JsonReader jsonReader = GSON.newJsonReader(new InputStreamReader(new FileInputStream(patchFile.toFile()), StandardCharsets.UTF_8))) {
            read(jsonReader);
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
            boolean isSemimatch = info.getValue().startsWith("@");
            if (!isSemimatch && !text.equals(info.getKey())) continue;
            if (info.getValue() == null || info.getKey() == null || info.getKey().isEmpty() || info.getValue().isEmpty()) {
                continue;
            }

            final TargetClassInfo targetClassInfo = info.getTargetClassInfo();
            if (stackTrace == null || targetClassInfo.getName().isEmpty() || targetClassInfo.getStackDepth() <= 0 || matchStack(targetClassInfo.getName(), targetClassInfo.getMethod(), stackTrace)) {
                return patchText(info.getValue(), info.getKey(), text, isSemimatch);
            }

            int index = targetClassInfo.getStackDepth();
            if (index >= stackTrace.length) continue;
            if (stackTrace[index].getClassName().contains(targetClassInfo.getName())) {
                return patchText(info.getValue(), info.getKey(), text, isSemimatch);
            }
        }

        return null;
    }

    private boolean matchStack(String className, String methodName, StackTraceElement[] stack) {
        int min = VaultPatcherConfig.getOptimize().getStackMin();
        int max = VaultPatcherConfig.getOptimize().getStackMax();
        stack = Arrays.copyOfRange(stack, min == -1 ? 0 : min, max == -1 ? stack.length : max);
        for (StackTraceElement ste : stack) {
            if (className.startsWith("#") && ste.getClassName().endsWith(className.substring(1))) {
                return methodName.equals("") || methodName.equals(ste.getMethodName());
            } else if (className.startsWith("@") && ste.getClassName().startsWith(className.substring(1))) {
                return methodName.equals("") || methodName.equals(ste.getMethodName());
            } else if (className.equals(ste.getClassName())) {
                return methodName.equals("") || methodName.equals(ste.getMethodName());
            }
        }
        return false;
    }

    private String patchText(String value, String key, String text, boolean isSemimatch) {
        boolean isMarked = VaultPatcherConfig.getDebugMode().getTestMode();
        boolean isSimilarity = isMarked && Utils.getSimilarityRatio(text, key) >= 0.5;
        if (isSemimatch && !value.startsWith("@@")) {
            String i18nValue = I18n.get(value.replace("@@", "@").substring(1));
            if (isMarked) i18nValue = "§a[REPLACE MARKED]§f" + i18nValue;
            if (isSimilarity) i18nValue = "§b[SIMILAR MARKED]§f" + i18nValue;
            return text.replace(key, i18nValue);
        } else {
            String i18nValue = I18n.get(value);
            if (isMarked) i18nValue = "§a[REPLACE MARKED]§f" + i18nValue;
            if (isSimilarity) i18nValue = "§b[SIMILAR MARKED]§f" + i18nValue;
            return i18nValue;
        }
    }


    @Override
    public String toString() {
        return "VaultPatcherPatch{" +
                "patchFile=" + patchFile +
                ", map=" + map +
                ", info=" + info +
                '}';
    }

    public PatchInfo getInfo() {
        return info;
    }

    public String getPname() {
        return pname;
    }
}
