package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(Utils.MOD_ID)
public class VaultPatcher {
    public static final Logger LOGGER = LogManager.getLogger();
    public static ArrayList<String> exportList = new ArrayList<>();
    public static List<VaultPatcherPatch> vpps = new ArrayList<>();

    @Mod.EventBusSubscriber(modid = Utils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void loadConfig(FMLConstructModEvent event) {
            event.enqueueWork(() -> {
                try {
                    VaultPatcherConfig.readConfig();
                    List<String> mods = VaultPatcherConfig.getMods();
                    for (String mod : mods) {
                        VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                        try {
                            vpp.readConfig();
                            vpps.add(vpp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to load config: ", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
