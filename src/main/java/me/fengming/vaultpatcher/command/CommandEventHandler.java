package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
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
        LiteralCommandNode<CommandSource> cmd = dispatcher.register(
                Commands.literal(Utils.MOD_ID
        ).then(
                Commands.literal("export")
                        .executes(ExportCommand.instance)
        ).then(
                Commands.literal("list")
                        .executes(ListCommand.instance)
        ).then(
                Commands.literal("reload")
                        .executes(ReloadCommand.instance)
        ));
        dispatcher.register(Commands.literal("vp").redirect(cmd));
    }
}
