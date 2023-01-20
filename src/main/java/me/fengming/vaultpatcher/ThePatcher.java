package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.DebugMode;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.Arrays;

public class ThePatcher {
    public ThePatcher() {
    }

    public static String patch(String s) {
        if (s == null || s.trim().equals("")) {
            return s;
        }
        VaultPatcher.exportList.add(s);
        // VaultPatcher.LOGGER.info(Arrays.toString(Thread.currentThread().getStackTrace()));
        String ret;
        for (VaultPatcherPatch vpp : VaultPatcher.vpps) {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            ret = vpp.patch(s, stacks);
            DebugMode debug = VaultPatcherConfig.getDebugMode();
            if (ret != null && !ret.equals(s)) {
                if (debug.isEnable() && debug.getOutputMode() == 0) {
                    VaultPatcher.LOGGER.info(String.format(debug.getOutputFormat(), s, ret, Arrays.toString(stacks)));
                }
                return ret;
            } else {
                if (debug.isEnable() && debug.getOutputMode() == 1) {
                    VaultPatcher.LOGGER.info(String.format(debug.getOutputFormat(), s, ret, Arrays.toString(stacks)));
                }
                return s;
            }
        }
        return s;
    }
}
