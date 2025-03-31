package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.Pair;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

public class MatchUtils {

    public static String matchPairs(Pairs p, String source, boolean dyn) {
        if (source.isEmpty()) return source; // FIX replace whitespace with "" -> source
        String v = p.getValue(source); // Go to return if its full match
        if (dyn && p.isNonFullMatch()) {
            for (Pair<String, String> pair : p.getSet()) {
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
        if (StringUtils.isBlank(i.getLocal())) return false;
        if (i.getLocalMode() == TargetClassInfo.LocalMode.NONE
                || (i.getLocalMode() == TargetClassInfo.LocalMode.INVOKE_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.METHOD_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.LOCAL_VARIABLE && !isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.GLOBAL_VARIABLE && !isMethod))
            return i.getLocal().equals(name);
        return false;
    }

    public static boolean matchOrdinal(TranslationInfo info, int ordinal) {
        return info.getTargetClassInfo().getOrdinal() == -1 || info.getTargetClassInfo().getOrdinal() == ordinal;
    }
}
