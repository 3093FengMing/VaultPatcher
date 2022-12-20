package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandEventHandler {
    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("vaultpatcher").then(
                        Commands.literal("export")
                                .requires((commandSource) -> commandSource.hasPermission(2))
                                .executes(ExportCommand.instance)
                )
        );
    }
}
