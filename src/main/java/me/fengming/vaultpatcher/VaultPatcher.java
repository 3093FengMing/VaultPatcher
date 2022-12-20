package me.fengming.vaultpatcher;

import com.mojang.logging.LogUtils;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import java.io.IOException;

@Mod("vaultpatcher")
public class VaultPatcher
{
    public static final Logger LOGGER = LogUtils.getLogger();

    @Mod.EventBusSubscriber(modid = "vaultpatcher", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Events {
        @SubscribeEvent
        public static void loadConfig(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                try {
                    VaultPatcherConfig.getInstance().readConfig();
                } catch (IOException e) {
                    LOGGER.error("Failed to load config!", e);
                    //throw new RuntimeException(e);
                }
            });
        }
    }
}
