package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;

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

            String methodName = info.getTargetClassInfo().getMethod();
            boolean ignoreMethod = StringUtils.isBlank(methodName);

            String dynName = info.getTargetClassInfo().getDynamicName();
            TargetClassInfo.MatchMode mode = info.getTargetClassInfo().getMatchMode();

            // Global replace when no classes are set
            if (StringUtils.isBlank(dynName)) {
                Utils.printDebugInfo(-1, original, method, replaced, stackTraces2String(stackTraces), info, null);
                return replaced;
            }

            // match classes that are set
            for (StackTraceElement ste : stackTraces) {
                if (!ignoreMethod && !methodName.equals(ste.getMethodName())) continue;

                boolean ok = false;
                if (!StringUtils.isBlank(dynName)) {
                    String cn = ste.getClassName();
                    switch (mode) {
                        case FULL:
                            ok = cn.equals(dynName);
                            break;
                        case STARTS:
                            ok = cn.startsWith(dynName);
                            break;
                        case ENDS:
                            ok = cn.endsWith(dynName);
                            break;
                    }
                }
                if (ok) {
                    Utils.printDebugInfo(-1, original, method, replaced, stackTraces2String(stackTraces), info, null);
                    return replaced;
                }
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
