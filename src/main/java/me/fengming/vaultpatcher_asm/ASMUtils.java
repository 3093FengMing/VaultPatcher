package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.node.handlers.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
                    case 0: if (className.equals(stackTrace.getClassName()) && (ignoredMethod || methodName.equals(stackTrace.getMethodName()))) return v;
                    case 1: if (stackTrace.getClassName().startsWith(className) && (ignoredMethod || methodName.equals(stackTrace.getMethodName()))) return v;
                    case 2: if (stackTrace.getClassName().endsWith(className) && (ignoredMethod || methodName.equals(stackTrace.getMethodName()))) return v;
                }
            }

        }
        return s;
    }

    public static NodeHandler<? extends AbstractInsnNode> getHandlerByNode(AbstractInsnNode node, NodeHandlerParameters params) {
        switch (node.getType()) {
            case 9:
                return new LdcNodeHandler((LdcInsnNode) node, params);
            case 6:
                return new InvokeDynamicNodeHandler((InvokeDynamicInsnNode) node, params);
            case 5:
                return new MethodNodeHandler((MethodInsnNode) node, params);
            case 2:
                return new VarNodeHandler((VarInsnNode) node, params);
            case 0:
        }
        return null;
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        if (Utils.isBlank(i.getLocal())) return false;
        if (i.getLocalMode() == 2) return i.getLocal().equals(name);
        if ((i.getLocalMode() == 0 && isMethod) || (i.getLocalMode() == 1 && !isMethod))
            return i.getLocal().equals(name);
        return false;
    }

    public static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.rawPackage(className), "__vp_replace", "(Ljava/lang/String;)Ljava/lang/String;", false));
    }

}
