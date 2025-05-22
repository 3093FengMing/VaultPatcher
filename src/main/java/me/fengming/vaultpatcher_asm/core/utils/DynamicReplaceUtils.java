package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

import java.util.Arrays;

@SuppressWarnings("unused")
public class DynamicReplaceUtils {

    // for dynamic replace
    public static String __mappingString(String original, String method) {
        if (original == null) return null;
        if (StringUtils.isBlank(original)) return original;
        // There will be no need to get the stack traces if classname is not needed
        StackTraceElement[] stackTraces = Utils.needStacktrace ? Thread.currentThread().getStackTrace() : new StackTraceElement[0];
        for (TranslationInfo info : Utils.dynTranslationInfos) {
            String replaced = MatchUtils.matchPairs(info.getPairs(), original, true);
            if (StringUtils.isBlank(replaced) || replaced.equals(original)) continue;

            Utils.printDebugInfo(-1, original, method, replaced, stackTraces2String(stackTraces), info);

            String className = info.getTargetClass();
            if (StringUtils.isBlank(className)) return replaced;

            String methodName = info.getTargetClassInfo().getMethod();
            boolean ignoredMethod = StringUtils.isBlank(methodName);

            for (StackTraceElement stackTrace : stackTraces) {
                if (!ignoredMethod && !methodName.equals(stackTrace.getMethodName())) continue;
                if (stackTrace.getClassName().equals(className)) return replaced;
            }
        }
        return original;
    }

    private static String stackTraces2String(StackTraceElement[] stackTraces) {
        if (stackTraces.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (StackTraceElement stackTrace : stackTraces) {
            sb.append(stackTrace.getClassName()).append("#").append(stackTrace.getMethodName()).append(", ");
        }
        return sb.delete(sb.length() - 2, sb.length()).append("]").toString();
    }
}
