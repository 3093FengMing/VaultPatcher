package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.Pair;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

public class MatchUtils {

    public static String matchPairs(Pairs p, String source, boolean dyn) {
        if (source == null || source.isEmpty()) return source;

        String v = p.getValue(source);
        if (v != null) return v;

        if (dyn && p.isNonFullMatch()) {
            for (Pair<String, String> pair : p.getSet()) {
                if (pair.second.charAt(0) == '@' && source.contains(pair.first)) {
                    return source.replace(pair.first, pair.second.substring(1));
                }
            }
        }
        return source;
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        String local = i.getLocal();
        if (StringUtils.isBlank(local)) return false;

        switch (i.getLocalMode()) {
            case INVOKE_RETURN:
            case METHOD_RETURN:
                return isMethod && local.equals(name);
            case LOCAL_VARIABLE:
            case GLOBAL_VARIABLE:
                return !isMethod && local.equals(name);
            case ARRAY_ELEMENT:
            case NONE:
                return local.equals(name);
            default:
                return false;
        }
    }

    public static boolean matchLocalIndex(TranslationInfo info, int objectIndex) {
        try {
            TargetClassInfo i = info.getTargetClassInfo();
            int targetIndex = Integer.parseInt(i.getLocal());
            return targetIndex == objectIndex;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean matchOrdinal(TranslationInfo info, int ordinal) {
        Pair<Integer, Integer> pair = info.getTargetClassInfo().getOrdinal();
        return pair.first <= ordinal && (pair.second == -1 || ordinal <= pair.second);
    }
}
