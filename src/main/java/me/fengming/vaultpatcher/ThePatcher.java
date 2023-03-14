package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.DebugMode;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.Arrays;

public class ThePatcher {
    public ThePatcher() {
    }

    public static String patch(String s, String m) {
        if (s == null || s.isBlank()) {
            return s;
        }
        Utils.addToExportList(s);
        // VaultPatcher.LOGGER.info(Arrays.toString(Thread.currentThread().getStackTrace()));
        String ret;
        for (VaultPatcherPatch vpp : Utils.vpps) {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            ret = vpp.patch(s, stacks);
            DebugMode debug = VaultPatcherConfig.getDebugMode();
            String format = debug.getOutputFormat();
            if (ret != null && !ret.equals(s)) {
                if (debug.isEnable() && (debug.getOutputMode() == 1 || debug.getOutputMode() == 0)) {
                    VaultPatcher.LOGGER.info(
                            format.replace("<source>", s)
                                    .replace("<target>", ret)
                                    .replace("<method>", m)
                                    .replace("<stack>", Arrays.toString(stacks))
                    );
                }
                return ret;
            } else {
                if (debug.isEnable() && debug.getOutputMode() == 1) {
                    VaultPatcher.LOGGER.info(
                            format.replace("<source>", s)
                                    .replace("<target>", s)
                                    .replace("<method>", m)
                                    .replace("<stack>", Arrays.toString(stacks))
                    );
                }
                return s;
            }
        }
        return s;
    }
}
