package me.fengming.vaultpatcher_asm.core.transformers;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TransformChecker {

    public static Map<TranslationInfo, Boolean> transformed = new HashMap<>();

    public static boolean isTransformed(String className) {
        for (Map.Entry<TranslationInfo, Boolean> entry : transformed.entrySet()) {
            TranslationInfo info = entry.getKey();
            String rClassName = StringUtils.rawPackage(info.getTargetClassInfo().getName());
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
}
