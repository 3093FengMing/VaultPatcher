package me.fengming.vaultpatcher;

import com.mojang.logging.LogUtils;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(Utils.MOD_ID)
public class VaultPatcher {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ArrayList<String> exportList = new ArrayList<>();
    public static List<VaultPatcherPatch> vpps = new ArrayList<>();

    @Mod.EventBusSubscriber(modid = Utils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void loadConfig(FMLConstructModEvent event) {
            event.enqueueWork(() -> {
                try {
                    VaultPatcherConfig.readConfig();
                    List<String> mods = VaultPatcherConfig.getMods();
                    System.out.println("mods = " + mods);
                    for (String mod : mods) {
                        System.out.println("mod = " + mod);
                        VaultPatcherPatch vpp = new VaultPatcherPatch(mod + ".json");
                        System.out.println("vpp = " + vpp);
                        try {
                            vpp.readConfig();
                            vpps.add(vpp);
                            System.out.println("vpps = " + vpps);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("mods = " + mods);
                } catch (IOException e) {
                    LOGGER.error("Failed to load config: ", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
