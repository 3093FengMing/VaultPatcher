package me.fengming.vaultpatcher.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fengming.vaultpatcher.Utils;
import me.fengming.vaultpatcher.config.PatchInfo;
import me.fengming.vaultpatcher.config.VaultPatcherPatch;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;

public class ListCommand implements Command<CommandSourceStack> {
    public static ListCommand instance = new ListCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.list.tips.modslist"), false);
        for (VaultPatcherPatch vpp : Utils.vpps) {
            PatchInfo info = vpp.getInfo();
            context.getSource().sendSuccess(constructText(info, vpp.getPname()), false);
        }
        return 0;
    }

    private MutableComponent constructText(PatchInfo info, String pname) {
        return new TextComponent(pname).setStyle(
                Style.EMPTY.withColor(TextColor.fromRgb(0x55FF55))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new TextComponent("")
                                        .append(new TranslatableComponent("commands.vaultpatcher.list.tips.name", info.getName()).append("\n")
                                                .append(new TranslatableComponent("commands.vaultpatcher.list.tips.desc", info.getDesc()).append("\n")
                                                        .append(new TranslatableComponent("commands.vaultpatcher.list.tips.authors", info.getAuthors()).append("\n")
                                                                .append(new TranslatableComponent("commands.vaultpatcher.list.tips.mods", info.getMods()
                                                                )))))
                        ))
        );
    }

}
