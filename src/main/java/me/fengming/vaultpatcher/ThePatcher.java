package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.Arrays;

public class ThePatcher {
    public ThePatcher() {
    }

    public static String patch(String s) {
        if (s == null || s.equals("")) {
            return s;
        }
        VaultPatcher.exportList.add(s);
        // VaultPatcher.LOGGER.info(Arrays.toString(Thread.currentThread().getStackTrace()));
        String ret;
        for (VaultPatcherPatch vpp : VaultPatcher.vpps) {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            ret = vpp.patch(s, stacks);
            if (ret != null && !ret.equals(s)) {
                if (VaultPatcherConfig.getDebugMode().isEnable()) {
                    VaultPatcher.LOGGER.debug(VaultPatcherConfig.getDebugMode().getOutputFormat()
                            .replace("<source>", s)
                            .replace("<target>", ret)
                            .replace("<stack>", Arrays.toString(stacks))
                    );
                }
                return ret;
            }
        }
        return s;
    }
}
