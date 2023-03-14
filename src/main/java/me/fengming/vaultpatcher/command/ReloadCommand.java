package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;

public class ReloadCommand implements Command<CommandSourceStack> {
    public static ReloadCommand instance = new ReloadCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        Minecraft.getInstance().reloadResourcePacks();
        return 0;
    }
}
