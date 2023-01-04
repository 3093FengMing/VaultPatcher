package me.fengming.vaultpatcher.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;


public class ExportCommand implements Command<CommandSource> {
    public static ExportCommand instance = new ExportCommand();

    @Override
    public int run(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.export.warning.wip"), true);
        Gson gson = new Gson();
        String json = gson.toJson(exportList, new TypeToken<ArrayList<String>>() {
        }.getType());
        //Export Patch
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(
                            Minecraft.getInstance().gameDirectory.toPath().
                            resolve("langpacther.json").toFile()));
            bw.write(json);
            bw.flush();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.export.tips.success"), true);
        return 0;
    }
}
