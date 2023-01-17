package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class ListCommand implements Command<CommandSourceStack> {
    public static ListCommand instance = new ListCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.list.warning.wip"), false);
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.list.tips.modslist"), false);
        List<String> mods = VaultPatcherConfig.getMods();
        StringBuilder smods = new StringBuilder();
        for (String mod : mods) {
            smods.append(mod).append(", ");
        }
        context.getSource().sendSuccess(new TextComponent(new String(smods)), false);
        return 0;
    }
}
