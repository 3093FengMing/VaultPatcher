package me.fengming.vaultpatcher_asm;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VaultPatcherInitializer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("vaultpatcher");

    @Override
    public void onInitialize() {
        LOGGER.info("VP loaded, please check!");
    }
}
