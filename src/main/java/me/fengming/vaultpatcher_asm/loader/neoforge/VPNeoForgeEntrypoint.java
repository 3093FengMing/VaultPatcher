package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import net.neoforged.fml.common.Mod;

@Mod("vaultpatcher")
public class VPNeoForgeEntrypoint {
    public VPNeoForgeEntrypoint() {
        NeoForgeRuntimeInfo.bootstrap("NeoForge");
        VaultPatcher.debugInfo("[VaultPatcher] NF DONE!");
    }
}
