package me.fengming.vaultpatcher_asm.core.utils;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;

import java.util.stream.Stream;

public final class ModClassDiscovery {
    public static Stream<String> getApplyClassNames() {
        return VaultPatcherConfig.getApplyMods().stream()
                .map(mod -> VaultPatcher.mcPath.resolve("mods").resolve(mod + ".jar").toString())
                .flatMap(jarPath -> Utils.getClassesNameByJar(jarPath).stream())
                .map(className -> className.substring(0, className.length() - 6))   // trim ".class"
                .map(StringUtils::dotPackage);
    }
}
