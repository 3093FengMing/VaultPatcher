package me.fengming.vaultpatcher_asm.core.utils;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Utils {
    public static List<TranslationInfo> emptyList = new ArrayList<>();
    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static List<TranslationInfo> dynTranslationInfos = new ArrayList<>();
    public static Path mcPath = null;

    // debug

    public static void printDebugInfo(int o, String s, String m, String ret, String c, TranslationInfo info) {
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
                        .replace("<ordinal>", String.valueOf(o))
        );
    }

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
                        .replace("<ordinal>", "Unknown")
        );
    }

    // transformer

    public static byte[] nodeToBytes(ClassNode node) {
        ClassWriter wr = new ClassWriter(0);
        node.accept(wr);
        return wr.toByteArray();
    }

    public static Set<ITransformer.Target> getTarget(TranslationInfo info) {
        Set<ITransformer.Target> targets = new HashSet<>();

        if (info == null) {
            targets.addAll(getExpandTargets());
        } else {
            String name = info.getTargetClassInfo().getName();
            if (!Utils.isBlank(name)) targets.add(ITransformer.Target.targetClass(Utils.rawPackage(name)));
        }

        targets.iterator().forEachRemaining(t -> VaultPatcher.debugInfo(String.format("[VaultPatcher] VPClassTransformer Target = %s", t.getClassName())));

        return targets;
    }

    public static Set<ITransformer.Target> getExpandTargets() {
        Set<ITransformer.Target> targets = new HashSet<>();
        // Apply mods
        targets.addAll(VaultPatcherConfig.getApplyMods().stream().collect(
                ArrayList::new,
                (list, s) -> Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(s + ".jar").toString())
                        .forEach(s1 -> list.add(ITransformer.Target.targetClass(s1.substring(0, s1.length() - 6)))),
                ArrayList::addAll)); // May cause unnecessary resource waste

        // Classes
        targets.addAll(VaultPatcherConfig.getClasses().stream().collect(
                ArrayList::new,
                (list, s) -> list.add(ITransformer.Target.targetClass(Utils.rawPackage(s))),
                ArrayList::addAll));

        return targets;
    }

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
        if (key.isEmpty()) return key; // FIX replace whitespace with "" -> original
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
        if (s == null) return false;
        if (s.isEmpty()) return true;
        for (int i = 0; i < s.getBytes(StandardCharsets.UTF_8).length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

}
