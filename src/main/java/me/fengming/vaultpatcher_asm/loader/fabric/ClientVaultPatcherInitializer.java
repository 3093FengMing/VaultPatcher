package me.fengming.vaultpatcher_asm.loader.fabric;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import net.fabricmc.api.ClientModInitializer;

public class ClientVaultPatcherInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VaultPatcher.isClient = true;
    }
}
