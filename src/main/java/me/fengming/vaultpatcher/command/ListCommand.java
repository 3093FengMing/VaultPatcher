package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fengming.vaultpatcher.config.VaultPatcherConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class ListCommand implements Command<CommandSource> {
    public static ListCommand instance = new ListCommand();

    @Override
    public int run(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.list.warning.wip"), false);
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.list.tips.modslist"), false);
        List<String> mods = VaultPatcherConfig.getMods();
        for (String mod : mods) {
            context.getSource().sendSuccess(new StringTextComponent("§a" + mod), false);
        }
        return 0;
    }
}
