package me.fengming.vaultpatcher_asm;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.config.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
    public static List<VaultPatcherPatch> vpps = new ArrayList<>();
    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static Path mcPath = null;

    public static Iterator<TranslationInfo> getIterator() {
        return translationInfos.iterator();
    }

    // debug

    public static void printDebugIndo(String s, String m, String ret, String c, DebugMode debug) {
        String format = debug.getOutputFormat();
        if (!debug.isEnable()) return;
        if (ret != null && !ret.equals(s)) {
            if (debug.getOutputMode() == 1 || debug.getOutputMode() == 0) {
                VaultPatcher.LOGGER.info("[VaultPatcher] Trying replacing!");
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", ret)
                                .replace("<method>", m)
                                .replace("<class>", c)
                );
            }
        } else {
            if (debug.getOutputMode() == 1) {
                VaultPatcher.LOGGER.info("[VaultPatcher] Trying replacing!");
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", s)
                                .replace("<method>", m)
                                .replace("<class>", c)
                );
            }
        }
    }

    // transformer

    public static List<String> getClassesNameByJar(String jarPath) {
        List<String> retClassName = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.isEmpty()) continue;
                if (name.endsWith(".class")) {
                    retClassName.add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retClassName;
    }

    public static ITransformer.Target addTargetClasses(TranslationInfo translationInfo) {
        String name = translationInfo.getTargetClassInfo().getName();
        if (name.isEmpty()) return null;
        return getTargetClassByString(rawPackage(translationInfo.getTargetClassInfo().getName()));
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
                (list, s) -> getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(s + ".jar").toString()).forEach(s1 -> list.add(getTargetClassByString(s1.substring(0, s1.length() - 6)))),
                ArrayList::addAll);
    }

    public static String rawPackage(String s) {
        return s.replace('.', '/');
    }

    public static ITransformer.Target getTargetClassByString(String s) {
        return ITransformer.Target.targetClass(s);
    }

    public static String matchPairs(Pairs p, String key) {
        if (key.isEmpty() || isBlank(key)) return key;
        String v = p.getValue(key);
        return v == null ? key : v;
    }

    public static boolean isBlank(String s) {
        if (s.isEmpty()) return true;
        for (int i = 0; i < s.getBytes(StandardCharsets.UTF_8).length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

    public static String __getClassName(Class<?> c) {
        return c.getName().replace(".", "/");
    }

    public static String __makeMethodDesc(Class<?> ret, Class<?>... param) {
        StringBuilder r = new StringBuilder("(");
        for (Class<?> p1 : param) {
            r.append("L").append(__getClassName(p1)).append(";");
        }
        r.append(")L").append(__getClassName(ret)).append(";");
        return r.toString();
    }
}
