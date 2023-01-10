package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherPatch;

public class ThePatcher {
    public ThePatcher() {
    }

    public static String patch(String s) {
        if (s == null || s.equals("")) {
            return s;
        }
        VaultPatcher.exportList.add(s);
        // VaultPatcher.LOGGER.info(Arrays.toString(Thread.currentThread().getStackTrace()));
        String ret = s;
        for (VaultPatcherPatch vpp : VaultPatcher.vpps) {
            VaultPatcher.LOGGER.info("s = " + s);
            VaultPatcher.LOGGER.info("ret = " + ret);
            ret = vpp.patch(s, Thread.currentThread().getStackTrace());
            if (ret != null && !ret.equals(s)) {
                return ret;
            }
        }
        return s;
    }
}
