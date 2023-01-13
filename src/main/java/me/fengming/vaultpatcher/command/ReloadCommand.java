package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class ReloadCommand implements Command<CommandSourceStack> {
    public static ReloadCommand instance = new ReloadCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.list.warning.wip"), false);
        Minecraft.getInstance().reloadResourcePacks();
        return 0;
    }
}
