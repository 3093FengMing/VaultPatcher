package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

public class ASMUtils {

    public static String __mappingString(String s, String method) {
        if (Utils.isBlank(s)) return s;
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (TranslationInfo info : Utils.dynTranslationInfos) {
            String v = Utils.matchPairs(info.getPairs(), s, true);
            if (Utils.isBlank(v) || v.equals(s)) continue;

            Utils.printDebugInfo(s, method, v, Utils.stackTraces2String(stackTraces), info);

            TargetClassInfo targetClass = info.getTargetClassInfo();
            String className = targetClass.getName();
            if (Utils.isBlank(className)) return v;

            String methodName = targetClass.getMethod();
            boolean ignoredMethod = Utils.isBlank(methodName);

            for (StackTraceElement stackTrace : stackTraces) {
                switch (targetClass.getMatchMode()) {
                    case 0: {
                        if (className.equals(stackTrace.getClassName()) && (ignoredMethod || methodName.equals(stackTrace.getMethodName())))
                            return v;
                    }
                    case 1: {
                        if (stackTrace.getClassName().startsWith(className) && (ignoredMethod || methodName.equals(stackTrace.getMethodName())))
                            return v;
                    }
                    case 2: {
                        if (stackTrace.getClassName().endsWith(className) && (ignoredMethod || methodName.equals(stackTrace.getMethodName())))
                            return v;
                    }
                }
            }

        }
        return s;
    }

}
