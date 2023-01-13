package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class ReloadCommand implements Command<CommandSource> {
    public static ReloadCommand instance = new ReloadCommand();

    @Override
    public int run(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.list.warning.wip"), false);
        Minecraft.getInstance().reloadResourcePacks();
        return 0;
    }
}
