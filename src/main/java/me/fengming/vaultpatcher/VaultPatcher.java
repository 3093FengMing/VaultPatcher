package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

@Mod(Utils.MOD_ID)
public class VaultPatcher {
    public static final Logger LOGGER =  LogManager.getLogger();
    public static ArrayList<String> exportList = new ArrayList<>();

    @Mod.EventBusSubscriber(modid = Utils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void loadConfig(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                try {
                    VaultPatcherConfig.getInstance().readConfig();
                } catch (IOException e) {
                    LOGGER.error("Failed to load config: ", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
