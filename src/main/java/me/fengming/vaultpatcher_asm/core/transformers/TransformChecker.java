package me.fengming.vaultpatcher_asm.core.transformers;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

public class TransformChecker {
    public static Object2BooleanMap<TranslationInfo> transformed = new Object2BooleanOpenHashMap<>();

    public static boolean isTransformed(String className) {
        for (Object2BooleanMap.Entry<TranslationInfo> entry : transformed.object2BooleanEntrySet()) {
            String rClassName = StringUtils.rawPackage(entry.getKey().getTargetClassInfo().getName());
            if (className.equals(rClassName) && !entry.getBooleanValue()) {
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
