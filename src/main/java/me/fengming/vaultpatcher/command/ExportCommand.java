package me.fengming.vaultpatcher.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fengming.vaultpatcher.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportCommand implements Command<CommandSource> {
    public static ExportCommand instance = new ExportCommand();

    @Override
    public int run(CommandContext<CommandSource> context) {
        context.getSource().sendSuccess(new TranslationTextComponent("commands.vaultpatcher.export.warning.wip"), true);
        Gson gson = new Gson();
        String json = gson.toJson(Utils.exportList, new TypeToken<ArrayList<String>>() {
        }.getType());
        // Export
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(
                            FMLPaths.GAMEDIR.get().resolve("langpacther.json").toFile()
                    ));
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
