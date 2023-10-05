package me.fengming.vaultpatcher_asm;

import cpw.mods.modlauncher.api.ITransformer;
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

    public static ITransformer.Target addTargetClasses(TranslationInfo translationInfo) {
        String name = translationInfo.getTargetClassInfo().getName();
        if (isBlank(name)) return null;
        return ITransformer.Target.targetClass(rawPackage(name));
    }

    public static List<ITransformer.Target> addConfigClasses() {
        return VaultPatcherConfig.getClasses().stream().collect(
                ArrayList::new,
                (list, s) -> list.add(ITransformer.Target.targetClass(rawPackage(s))),
                ArrayList::addAll);
    }

    public static List<ITransformer.Target> addConfigApplyMods() {
        return VaultPatcherConfig.getApplyMods().stream().collect(
                ArrayList::new,
                (list, s) -> getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(s + ".jar").toString()).forEach(s1 -> list.add(ITransformer.Target.targetClass(s1.substring(0, s1.length() - 6)))),
                ArrayList::addAll);
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

    // for debug

    public static String stackTraces2String(StackTraceElement[] stackTraces) {
        StringBuilder sb = new StringBuilder("[");
        for (StackTraceElement stackTrace : stackTraces) {
            sb.append(stackTrace.getClassName()).append("#").append(stackTrace.getMethodName()).append(", ");
        }
        return sb.delete(sb.length() - 2, sb.length()).append("]").toString();
    }

}
