package me.fengming.vaultpatcher_asm.core.utils;

public class StringUtils {
    public static String dotPackage(String s) {
        return s.replace('/', '.');
    }

    public static String rawPackage(String s) {
        return s.replace('.', '/');
    }

    public static boolean isBlank(String s) {
        if (s == null) return false;
        if (s.isEmpty()) return true; // there is a short in most cases
        return s.codePoints().allMatch(Character::isWhitespace);
    }
}
