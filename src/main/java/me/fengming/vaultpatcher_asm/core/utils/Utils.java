package me.fengming.vaultpatcher_asm.core.utils;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.*;
import me.fengming.vaultpatcher_asm.plugin.VaultPatcherPlugin;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Utils {
    public static final List<TranslationInfo> EMPTY_LIST = new ArrayList<>();

    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static List<TranslationInfo> dynTranslationInfos = new ArrayList<>();
    public static Map<TranslationInfo, Boolean> transformed = new HashMap<>();
    public static boolean needStacktrace = false;

    public static Path mcPath = null;
    public static String mcVersion = null;
    public static Platform platform = Platform.UNDEFINED;
    public static boolean isClient = false;

    public static DebugMode debug = new DebugMode();

    // plugins

    public static String getGameVersion() {
        return mcVersion;
    }

    public static Path getPluginConfigPath(VaultPatcherPlugin plugin) {
        return mcPath.resolve("config").resolve("vaultpatcher_asm").resolve("plugins").resolve(plugin.getName());
    }

    public static Path getVpPath() {
        return mcPath.resolve("vaultpatcher");
    }

    public static Platform getPlatform() {
        return platform;
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
            VaultPatcher.LOGGER.error("Error getting target type: ", e);
        }
        return null;
    }

    public static boolean isTransformed(String className) {
        for (Map.Entry<TranslationInfo, Boolean> entry : transformed.entrySet()) {
            TranslationInfo info = entry.getKey();
            String rClassName = rawPackage(info.getTargetClassInfo().getName());
            if (className.equals(rClassName) && !entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean setTransformed(TranslationInfo info) {
        if (!transformed.containsKey(info)) return false;
        transformed.put(info, true);
        return true;
    }

    public static byte[] nodeToBytes(ClassNode node) {
        ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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

        targets.iterator().forEachRemaining(t -> VaultPatcher.debugInfo("[VaultPatcher] VPClassTransformer Target = {}", t.getClassName()));

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

    public static String matchPairs(Pairs p, String source, boolean dyn) {
        if (source.isEmpty()) return source; // FIX replace whitespace with "" -> source
        String v = p.getValue(source); // Go to return if its full match
        if (dyn && p.isNonFullMatch()) {
            for (Pair<String, String> pair : p.getList()) {
                if (pair.second.charAt(0) == '@' && source.contains(pair.first)) {
                    v = source.replace(pair.first, pair.second.substring(1));
                }
            }
        }
        return v == null ? source : v;
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        if (Utils.isBlank(i.getLocal())) return false;
        if (i.getLocalMode() == TargetClassInfo.LocalMode.NONE
                || (i.getLocalMode() == TargetClassInfo.LocalMode.CALL_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.METHOD_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.LOCAL_VARIABLE && !isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.GLOBAL_VARIABLE && !isMethod))
            return i.getLocal().equals(name);
        return false;
    }

    public static boolean matchOrdinal(TranslationInfo info, int ordinal) {
        return info.getTargetClassInfo().getOrdinal() == -1 || info.getTargetClassInfo().getOrdinal() == ordinal;
    }

    public static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition, boolean isString) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.rawPackage(className), "__vp_replace", isString ? "(Ljava/lang/String;)Ljava/lang/String;" : "(Ljava/lang/Object;)Ljava/lang/String;", false));
    }

    // other
    public static String getI18n(String key) {
        return I18n.getValue(key);
    }

    public static String filePathToClassName(Path path, Path root) {
        String s = root.relativize(path).toString();
        return s.substring(0, s.length() - 6).replace(File.separatorChar, '/');
    }

    public static boolean isBlank(String s) {
        if (s == null) return false;
        if (s.isEmpty()) return true; // there is a short in most cases
        for (int i = 0; i < s.getBytes(StandardCharsets.UTF_8).length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

    public enum Platform {
        UNDEFINED, Fabric, Forge1_6, Forge1_13
    }
}
