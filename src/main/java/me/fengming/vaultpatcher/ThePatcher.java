package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.DebugMode;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.Arrays;

public class ThePatcher {
    public ThePatcher() {
    }

    public static String patch(String string, String method) {
        if (string == null || string.equals("") || string.isBlank()) {
            return string;
        }
        if (!VaultPatcherConfig.getOptimize().isDisableExport()) {
            Utils.addToExportList(string);
        }

        String ret;
        for (VaultPatcherPatch vpp : Utils.vpps) {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            ret = vpp.patch(string, stacks);

            DebugMode debug = VaultPatcherConfig.getDebugMode();

            if (debug.isEnable()) {
                return outputDebugIndo(string, method, ret, stacks, debug);
            } else return ret;

        }
        return string;
    }

    private static String outputDebugIndo(String s, String m, String ret, StackTraceElement[] stacks, DebugMode debug) {
        String format = debug.getOutputFormat();
        if (ret != null && !ret.equals(s)) {
            if (debug.getOutputMode() == 1 || debug.getOutputMode() == 0) {
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", ret)
                                .replace("<method>", m)
                                .replace("<stack>", Arrays.toString(stacks))
                );
            }
        } else {
            if (debug.getOutputMode() == 1) {
                VaultPatcher.LOGGER.info(
                        format.replace("<source>", s)
                                .replace("<target>", s)
                                .replace("<method>", m)
                                .replace("<stack>", Arrays.toString(stacks))
                );
            }
        }
        return ret;
    }
}
