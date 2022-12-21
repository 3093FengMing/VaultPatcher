package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherConfig;

import java.util.Arrays;

/**
 * For config writers: here is the patcher method,
 * indexed ${UNKNOWN} in the stacktrace
 */
public final class ThePatcher {
    private ThePatcher() {}

    public static String patch(String s) {
        // VaultPatcher.LOGGER.info(Arrays.toString(Thread.currentThread().getStackTrace()));
        return VaultPatcherConfig.getInstance().patch(s, Thread.currentThread().getStackTrace());
    }
}
