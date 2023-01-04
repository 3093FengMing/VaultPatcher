package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.CommandDispatcher;
import me.fengming.vaultpatcher.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerClientCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal(Utils.MOD_ID).then(
                        Commands.literal("export")
                                .requires((commandSource) -> commandSource.hasPermission(2))
                                .executes(ExportCommand.instance)
                )
        );
    }
}
