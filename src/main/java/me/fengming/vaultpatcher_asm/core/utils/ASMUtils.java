package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.VaultPatcher;
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

    // for dynamic replace
    public static String __mappingString(String orignal, String method) {
        if (orignal == null) return null;
        if (Utils.isBlank(orignal)) return orignal;
        // There will be no need to get the stack traces if classname is not needed
        StackTraceElement[] stackTraces = new StackTraceElement[0];
        if (Utils.needStacktrace) stackTraces = Thread.currentThread().getStackTrace();
        for (TranslationInfo info : Utils.dynTranslationInfos) {
            String replaced = Utils.matchPairs(info.getPairs(), orignal, true);
            if (Utils.isBlank(replaced) || replaced.equals(orignal)) continue;

            Utils.printDebugInfo(-1, orignal, replaced, method, stackTraces2String(stackTraces), info);

            TargetClassInfo targetClass = info.getTargetClassInfo();
            String className = targetClass.getName();
            if (Utils.isBlank(className)) return replaced;

            String methodName = targetClass.getMethod();
            boolean ignoredMethod = Utils.isBlank(methodName);

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
            throw new IllegalStateException("Failed to export class: ", e);
        }
    }
}
