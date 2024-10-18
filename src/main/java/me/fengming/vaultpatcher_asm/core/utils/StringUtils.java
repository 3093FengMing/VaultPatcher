package me.fengming.vaultpatcher_asm.core.utils;

import java.nio.charset.StandardCharsets;

public class StringUtils
{
    public static String rawPackage(String s) {
        return s.replace('.', '/');
    }

    public static boolean isBlank(String s) {
        if (s == null) return false;
        if (s.isEmpty()) return true; // there is a short in most cases
        for (int i = 0; i < s.getBytes(StandardCharsets.UTF_8).length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }
}
