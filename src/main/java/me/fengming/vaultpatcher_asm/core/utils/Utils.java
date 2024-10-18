package me.fengming.vaultpatcher_asm.core.utils;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Utils {
    public static final List<TranslationInfo> EMPTY_LIST = new ArrayList<>();

    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static List<TranslationInfo> dynTranslationInfos = new ArrayList<>();
    public static boolean needStacktrace = false;

    public static DebugMode debug = new DebugMode();

    // plugins

    public static Path getVpPath() {
        return VaultPatcher.mcPath.resolve("vaultpatcher");
    }

    // debug

    public static void printDebugInfo(int ordinal, String source,
                                      String method, String target, String clazz,
                                      TranslationInfo info) {
        if (!debug.isEnable()) return;
        String format = debug.getOutputFormat();
        VaultPatcher.LOGGER.info("[VaultPatcher] Trying replacing!\n{}",
                format.replace("<source>", source)
                .replace("<target>", target)
                .replace("<method>", method)
                .replace("<info>", info.toString())
                .replace("<class>", clazz)
                .replace("<ordinal>", ordinal == -1 ? "Unknown" : String.valueOf(ordinal))
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

    public static byte[] nodeToBytes(ClassNode node) {
        ClassWriter wr = new ClassWriter(0); // flag = 0, not compute frames to prevent class loading
        node.accept(wr);
        return wr.toByteArray();
    }

    public static Set<ITransformer.Target> getExpandTargets() {
        Set<ITransformer.Target> targets = new HashSet<>();
        // Apply mods
        targets.addAll(VaultPatcherConfig.getApplyMods().stream().collect(
                ArrayList::new,
                (list, s) -> Utils.getClassesNameByJar(VaultPatcher.mcPath.resolve("mods").resolve(s + ".jar").toString())
                        .forEach(s1 -> list.add(ITransformer.Target.targetClass(s1.substring(0, s1.length() - 6)))),
                ArrayList::addAll)); // May cause unnecessary resource waste

        // Classes
        targets.addAll(VaultPatcherConfig.getClasses().stream().collect(
                ArrayList::new,
                (list, s) -> list.add(ITransformer.Target.targetClass(StringUtils.rawPackage(s))),
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

    // other
    public static String getI18n(String key) {
        return I18n.getValue(key);
    }

    public static String filePathToClassName(Path path, Path root) {
        String s = root.relativize(path).toString();
        return s.substring(0, s.length() - 6).replace(File.separatorChar, '/');
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
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export class: ", e);
        }
    }
}
