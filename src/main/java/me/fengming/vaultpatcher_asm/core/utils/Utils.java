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
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

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
