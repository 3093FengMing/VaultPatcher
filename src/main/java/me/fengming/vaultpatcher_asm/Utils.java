package me.fengming.vaultpatcher_asm;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
    public static List<VaultPatcherPatch> vpps = new ArrayList<>();
    public static List<TranslationInfo> translationInfos = new ArrayList<>();
    public static Path mcPath = null;

    public static Iterator<TranslationInfo> getIterator() {
        return translationInfos.iterator();
    }

    public static List<ITransformer.Target> addTargetClasses() {
        List<ITransformer.Target> list = new ArrayList<>();
        VaultPatcherConfig.getClasses().forEach(s -> list.add(ITransformer.Target.targetClass(s.replace(".", "/"))));
        for (VaultPatcherPatch vpp : vpps) {
            for (TranslationInfo translationInfo : vpp.getTranslationInfoList()) {
                String name = translationInfo.getTargetClassInfo().getName();
                if (name.isEmpty()) continue;
                list.add(ITransformer.Target.targetClass(translationInfo.getTargetClassInfo().getName().replace(".", "/")));
            }
        }
        return list;
    }

    public static List<String> getClassNameByJar(String jarPath) {
        List<String> retClassName = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry entry = entrys.nextElement();
                String name = entry.getName();
                if (name.isEmpty()) continue;
                if (name.endsWith(".class")) {
                    retClassName.add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retClassName;
    }

    public static void outputDebugIndo(String s, String m, String ret, String c, DebugMode debug) {
        String format = debug.getOutputFormat();
        if (ret != null && !ret.equals(s)) {
            if (debug.getOutputMode() == 1 || debug.getOutputMode() == 0) {
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", ret)
                                .replace("<method>", m)
                                .replace("<class>", c)
                );
            }
        } else {
            if (debug.getOutputMode() == 1) {
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", s)
                                .replace("<method>", m)
                                .replace("<class>", c)
                );
            }
        }
    }


}