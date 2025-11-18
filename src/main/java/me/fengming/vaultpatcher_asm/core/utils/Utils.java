package me.fengming.vaultpatcher_asm.core.utils;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.misc.CommonSuperClassWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Utils {
    public static Map<String, Set<TranslationInfo>> translationInfoMap = new Object2ReferenceOpenHashMap<>();
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

    public static void deepCopyClass(ClassNode target, ClassNode source) {
        clearClassNode(target);
        source.accept(target);
    }

    private static void clearClassNode(ClassNode node) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (Field field : ClassNode.class.getFields()) {
            // bypass final and static members
            if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0) continue;

            try {
                MethodHandle mh = lookup.unreflectSetter(field).bindTo(node);
                mh.invoke(zero(field.getType()));
            } catch (Throwable t) {
                VaultPatcher.LOGGER.error("Can't clear field {1} of ClassNode {0} reflectively", node, field, t);
            }
        }
    }

    private static Object zero(Class<?> type) {
        if (!type.isPrimitive()) return null;
        switch (type.getName()) {
            case "int": return 0;
            case "float": return 0f;
            case "double": return 0d;
            case "short": return (short) 0;
            case "byte": return (byte) 0;
            case "long": return 0L;
            case "boolean": return false;
            case "char": return '\0';
            default: throw new IncompatibleClassChangeError("Unknown primitive type: " + type);
        }
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

    public static Path exportClass(ClassNode node, Path root) {
        ClassWriter w = new ClassWriter(0);
        node.accept(w);
        byte[] b = w.toByteArray();
        Path path = root.resolve(node.name + ".class");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, b);

            return path;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export class: ", e);
        }
    }
}
