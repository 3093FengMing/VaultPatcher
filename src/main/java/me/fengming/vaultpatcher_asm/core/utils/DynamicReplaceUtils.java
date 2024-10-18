package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

public class DynamicReplaceUtils {

    // for dynamic replace
    public static String __mappingString(String orignal, String method) {
        if (orignal == null) return null;
        if (StringUtils.isBlank(orignal)) return orignal;
        // There will be no need to get the stack traces if classname is not needed
        StackTraceElement[] stackTraces = new StackTraceElement[0];
        if (Utils.needStacktrace) stackTraces = Thread.currentThread().getStackTrace();
        for (TranslationInfo info : Utils.dynTranslationInfos) {
            String replaced = MatchUtils.matchPairs(info.getPairs(), orignal, true);
            if (StringUtils.isBlank(replaced) || replaced.equals(orignal)) continue;

            Utils.printDebugInfo(-1, orignal, replaced, method, stackTraces2String(stackTraces), info);

            TargetClassInfo targetClass = info.getTargetClassInfo();
            String className = targetClass.getName();
            if (StringUtils.isBlank(className)) return replaced;

            String methodName = targetClass.getMethod();
            boolean ignoredMethod = StringUtils.isBlank(methodName);

            for (StackTraceElement stackTrace : stackTraces) {
                if (!ignoredMethod && !methodName.equals(stackTrace.getMethodName())) continue;
                switch (targetClass.getMatchMode()) {
                    case FULL: if (stackTrace.getClassName().equals(className)) return replaced;
                    case STARTS: if (stackTrace.getClassName().startsWith(className)) return replaced;
                    case ENDS: if (stackTrace.getClassName().endsWith(className)) return replaced;
                }
            }
        }
        return orignal;
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
