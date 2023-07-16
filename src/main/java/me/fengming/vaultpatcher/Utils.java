package me.fengming.vaultpatcher;

import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher.config.DebugMode;
import me.fengming.vaultpatcher.config.TranslationInfo;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static List<VaultPatcherPatch> vpps = new ArrayList<>();
    public static List<TranslationInfo> translationInfos = new ArrayList<>();

    public static Iterator<TranslationInfo> getIterator() {
        return translationInfos.iterator();
    }

    public static List<ITransformer.Target> addTargetClasses() {
        List<ITransformer.Target> list = new ArrayList<>();
        VaultPatcherConfig.getClasses().forEach(s -> list.add(ITransformer.Target.targetClass(s.replace(".", "/"))));
        for (VaultPatcherPatch vpp : vpps) {
            for (TranslationInfo translationInfo : vpp.getTranslationInfoList()) {
                list.add(ITransformer.Target.targetClass(translationInfo.getTargetClassInfo().getName().replace(".", "/")));
            }
        }
        return list;
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
