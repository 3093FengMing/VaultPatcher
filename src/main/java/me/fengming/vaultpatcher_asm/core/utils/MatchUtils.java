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
        if (name == null || info == null) return false;
        
        TargetClassInfo targetClass = info.getTargetClassInfo();
        String local = targetClass.getLocal();
        if (StringUtils.isBlank(local)) return false;
        
        switch (targetClass.getLocalMode()) {
            case INVOKE_RETURN:
            case METHOD_RETURN:
                return isMethod && local.equals(name);
            case LOCAL_VARIABLE:
            case GLOBAL_VARIABLE:
                return !isMethod && local.equals(name);
            case NONE:
                return local.equals(name);
            default:
                return false;
        }
    }

    public static boolean matchOrdinal(TranslationInfo info, int ordinal) {
        if (info == null) return false;
        int targetOrdinal = info.getTargetClassInfo().getOrdinal();
        return targetOrdinal == -1 || targetOrdinal == ordinal;
    }
}
