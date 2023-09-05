package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
