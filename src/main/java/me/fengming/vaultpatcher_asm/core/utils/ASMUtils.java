package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.config.TargetClassInfo;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

public class ASMUtils {

    public static String __mappingString(String s, String method) {
        if (s == null) return null;
        if (Utils.isBlank(s)) return s;
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (TranslationInfo info : Utils.dynTranslationInfos) {
            String v = Utils.matchPairs(info.getPairs(), s, true);
            if (Utils.isBlank(v) || v.equals(s)) continue;

            if (VaultPatcherConfig.getDebugMode().isEnable()) Utils.printDebugInfo(s, method, v, stackTraces2String(stackTraces), info);

            TargetClassInfo targetClass = info.getTargetClassInfo();
            String className = targetClass.getName();
            if (Utils.isBlank(className)) return v;

            String methodName = targetClass.getMethod();
            boolean ignoredMethod = Utils.isBlank(methodName);

            for (StackTraceElement stackTrace : stackTraces) {
                if (!ignoredMethod && !methodName.equals(stackTrace.getMethodName())) continue;
                switch (targetClass.getMatchMode()) {
                    case FULL: if (stackTrace.getClassName().equals(className)) return v;
                    case STARTS: if (stackTrace.getClassName().startsWith(className)) return v;
                    case ENDS: if (stackTrace.getClassName().endsWith(className)) return v;
                }
            }

        }
        return s;
    }

    private static String stackTraces2String(StackTraceElement[] stackTraces) {
        StringBuilder sb = new StringBuilder("[");
        for (StackTraceElement stackTrace : stackTraces) {
            sb.append(stackTrace.getClassName()).append("#").append(stackTrace.getMethodName()).append(", ");
        }
        return sb.delete(sb.length() - 2, sb.length()).append("]").toString();
    }

    public static File exportClass(ClassNode node, Path root) {
        ClassWriter w = new ClassWriter(0);
        node.accept(w);
        byte[] b = w.toByteArray();
        File file = root.resolve(node.name + ".class").toFile();
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            file.setWritable(true);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export class", e);
        }
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        TargetClassInfo i = info.getTargetClassInfo();
        if (Utils.isBlank(i.getLocal())) return false;
        if (i.getLocalMode() == TargetClassInfo.LocalMode.NONE
                || (i.getLocalMode() == TargetClassInfo.LocalMode.CALL_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.METHOD_RETURN && isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.LOCAL_VARIABLE && !isMethod)
                || (i.getLocalMode() == TargetClassInfo.LocalMode.GLOBAL_VARIABLE && !isMethod))
            return i.getLocal().equals(name);
        return false;
    }

    public static boolean matchOrdinal(TranslationInfo info, int ordinal) {
        return info.getTargetClassInfo().getOrdinal() == -1 || info.getTargetClassInfo().getOrdinal() == ordinal;
    }

    public static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition, boolean isString) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, Utils.rawPackage(className), "__vp_replace", isString ? "(Ljava/lang/String;)Ljava/lang/String;" : "(Ljava/lang/Object;)Ljava/lang/String;", false));
    }

}
