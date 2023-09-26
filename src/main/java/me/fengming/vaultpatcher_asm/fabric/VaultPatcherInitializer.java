package me.fengming.vaultpatcher_asm.fabric;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import net.fabricmc.api.ModInitializer;

public class VaultPatcherInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        VaultPatcher.LOGGER.info("Fabric Done!");
    }
}
