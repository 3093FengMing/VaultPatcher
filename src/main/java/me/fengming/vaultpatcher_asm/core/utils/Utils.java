package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Utils {
    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static List<TranslationInfo> dynTranslationInfos = new ArrayList<>();
    public static Path mcPath = null;

    // debug

    public static void printDebugInfo(String s, String m, String ret, String c, TranslationInfo info) {
        DebugMode debug = VaultPatcherConfig.getDebugMode();
        if (!debug.isEnable()) return;
        String format = debug.getOutputFormat();
        VaultPatcher.LOGGER.info("[VaultPatcher] Trying replacing!");
        VaultPatcher.LOGGER.info(
                format.replace("<source>", s)
                        .replace("<target>", ret)
                        .replace("<method>", m)
                        .replace("<info>", info.toString())
                        .replace("<class>", c)
        );
    }

    // transformer

    public static List<String> getClassesNameByJar(String jarPath) {
        try (JarFile jarFile = new JarFile(jarPath)) {
            return jarFile.stream()
                    .map(ZipEntry::getName)
                    .filter(name -> name.endsWith(".class"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed loading jar: " + jarPath, e);
        }
    }

    public static String rawPackage(String s) {
        return s.replace('.', '/');
    }

    public static String matchPairs(Pairs p, String key, boolean dyn) {
        if (isBlank(key)) return key;
        String v = key;
        if (dyn) {
            for (Map.Entry<String, String> entry : p.getMap().entrySet()) {
                String k1 = entry.getKey();
                String v1 = entry.getValue();
                if (key.equals(k1) && v1.charAt(0) != '@') {
                    v = v1;
                }
                if (v1.charAt(0) == '@' && key.contains(k1)) { // non-full match
                    v = v.replace(k1, v1.substring(1));
                }
            }
        } else {
            v = p.getValue(key);
        }
        return v == null ? key : v;
    }

    public static boolean isBlank(String s) {
        if (s.isEmpty()) return true;
        for (int i = 0; i < s.getBytes(StandardCharsets.UTF_8).length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

}
