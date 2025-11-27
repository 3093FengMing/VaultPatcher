package me.fengming.vaultpatcher_asm.core.utils;


import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.misc.CommonSuperClassWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class Utils {
    public static Map<String, Set<TranslationInfo>> translationInfoMap = new HashMap<>();
    public static List<TranslationInfo> dynTranslationInfos = new ArrayList<>();
    public static boolean needStacktrace = false;

    public static DebugMode debug = new DebugMode();

    public static void forEachInfos(Consumer<TranslationInfo> consumer) {
        translationInfoMap.values().forEach(set -> set.forEach(consumer));
    }

    public static void addTranslationInfo(String targetClass, TranslationInfo info) {
        translationInfoMap.computeIfAbsent(targetClass, k -> new HashSet<>()).add(info);
    }

    // plugins

    public static Path getVpPath() {
        return VaultPatcher.mcPath.resolve("vaultpatcher");
    }

    // debug
    public static void printDebugInfo(int ordinal, String source,
                                      String method, String target, String clazz,
                                      TranslationInfo info, String detail) {
        if (!debug.isEnable()) return;
        int showSame = debug.getOutputMode();
        if (showSame == 0 && source.equals(target)) return;
        TargetClassInfo ci = info.getTargetClassInfo();
        VaultPatcher.LOGGER.info("[VaultPatcher] Trying replacing!\n{}",
                String.format(
                        "'%s' -> '%s' in %s | TranslationInfo{name=%s, method=%s, localMode=%s, local=%s, ordinal=%s, matchMode=%s, ASM/DynMethod=%s, %s}",
                        source, target, StringUtils.dotPackage(clazz),
                        ci.getDynamicName(), ci.getMethod(), ci.getLocalMode(),
                        ci.getLocal(), (ordinal == -1 ? "Unknown" : String.valueOf(ordinal)), ci.getMatchMode(),
                        method, detail
                )
        );
    }

    // transformer

    public static cpw.mods.modlauncher.api.TargetType neoforgeGetTargetType(String type) {
        try {
            Class<?> clazz = Class.forName("cpw.mods.modlauncher.api.TargetType");
            return (cpw.mods.modlauncher.api.TargetType) clazz.getDeclaredField(type).get(null);
        } catch (Exception e) {
            throw new RuntimeException("Error getting target type: ", e);
        }
    }

    public static void deepCopyClass(ClassNode a, ClassNode b) {
        a.interfaces = b.interfaces;
        a.name = b.name;
        a.methods = b.methods;
        a.fields = b.fields;
        a.innerClasses = b.innerClasses;
        a.superName = b.superName;
        a.access = b.access;
        a.attrs = b.attrs;
        a.visibleAnnotations = b.visibleAnnotations;
        a.visibleTypeAnnotations = b.visibleTypeAnnotations;
        a.invisibleAnnotations = b.invisibleAnnotations;
        a.invisibleTypeAnnotations = b.invisibleTypeAnnotations;
    }

    public static byte[] nodeToBytes(ClassNode node) {
        ClassWriter wr = new CommonSuperClassWriter();
        node.accept(wr);
        return wr.toByteArray();
    }

    // other
    public static String filePathToClassName(Path path, Path root) {
        String s = root.relativize(path).toString();
        return s.substring(0, s.length() - 6).replace(File.separatorChar, '/');
    }

    private static Map<String, Object> withoutName(Map<String, Object> source) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (source == null) return out;
        for (Map.Entry<String, Object> e : source.entrySet()) {
            if (!"name".equals(e.getKey())) {
                out.put(e.getKey(), e.getValue());
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static void convert(List<Map<String, Object>> data) {
        if (data == null) return;

        List<Map<String, Object>> newData = new ArrayList<>();

        for (Map<String, Object> entry : data) {
            if (entry.containsKey("key") && entry.containsKey("value")) {
                Object k = entry.get("key");
                Object v = entry.get("value");
                Map<String, Object> pair = new LinkedHashMap<>();
                pair.put("key", k);
                pair.put("value", v);
                entry.put("pairs", Collections.singletonList(pair));
                entry.remove("key");
                entry.remove("value");
            }

            if (entry.containsKey("target_class")) {
                Object tc = entry.get("target_class");
                if (tc instanceof Map) {
                    Map<String, Object> tcMap = new LinkedHashMap<>((Map<String, Object>) tc);
                    Map<String, Object> info = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> e : tcMap.entrySet()) {
                        if (!"name".equals(e.getKey())) info.put(e.getKey(), e.getValue());
                    }
                    if (!info.isEmpty()) {
                        entry.put("info", info);
                    }
                    List<Object> names = new ArrayList<>();
                    Object name = tcMap.get("name");
                    if (name != null) names.add(name);
                    entry.put("target_class", names);
                }
                newData.add(entry);
                continue;
            }

            if (entry.containsKey("target_classes")) {
                Object tcsObj = entry.get("target_classes");
                List<?> tcsList;
                if (tcsObj instanceof List) {
                    tcsList = (List<?>) tcsObj;
                } else {
                    newData.add(entry);
                    continue;
                }
                Map<String, Object> base = new LinkedHashMap<>();
                for (Map.Entry<String, Object> kv : entry.entrySet()) {
                    String key = kv.getKey();
                    if ("target_classes".equals(key)) continue;
                    base.put(key, kv.getValue());
                }

                for (Object each : tcsList) {
                    Map<String, Object> newEntry = new LinkedHashMap<>(base);
                    if (each instanceof Map) {
                        Map<String, Object> eachMap = new LinkedHashMap<>((Map<String, Object>) each);
                        Object name = eachMap.get("name");
                        List<Object> names = new ArrayList<>();
                        if (name != null) names.add(name);
                        newEntry.put("target_class", names);
                        Map<String, Object> info = new LinkedHashMap<>();
                        for (Map.Entry<String, Object> e : eachMap.entrySet()) {
                            if (!"name".equals(e.getKey())) {
                                info.put(e.getKey(), e.getValue());
                            }
                        }
                        if (!info.isEmpty()) {
                            newEntry.put("info", info);
                        }
                    }
                    newData.add(newEntry);
                }
                continue;
            }
            newData.add(entry);
        }
        data.clear();
        data.addAll(newData);
    }


    public static boolean needsConversion(List<Map<String, Object>> data) {
        if (data == null) return false;
        for (Map<String, Object> entry : data) {
            if (entry == null) continue;

            // case 1: target_class exists and is a map
            if (entry.containsKey("target_class")) {
                Object tc = entry.get("target_class");
                if (tc instanceof Map) {
                    return true;
                }
            }

            // case 2: target_classes exists and contains at least one map element
            if (entry.containsKey("target_classes")) {
                Object tcsObj = entry.get("target_classes");
                if (tcsObj instanceof List) {
                    List<?> tcs = (List<?>) tcsObj;
                    for (Object each : tcs) {
                        if (each instanceof Map) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static File exportClass(ClassNode node, Path root) {
        ClassWriter w = new ClassWriter(0);
        node.accept(w);
        byte[] b = w.toByteArray();
        File file = root.resolve(node.name + ".class").toFile();
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            file.setWritable(true);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(b);
                fos.flush();
            }
            return file;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export class: ", e);
        }
    }
}
