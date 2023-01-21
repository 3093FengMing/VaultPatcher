package me.fengming.vaultpatcher.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;


public class ExportCommand implements Command<CommandSourceStack> {
    public static ExportCommand instance = new ExportCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.export.warning.wip"), true);
        Gson gson = new Gson();
        String json = gson.toJson(exportList, new TypeToken<ArrayList<String>>() {
        }.getType());
        //Export langs
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(
                            FMLPaths.GAMEDIR.get().resolve("langpacther.json").toFile(),
                            StandardCharsets.UTF_8));
            bw.write(json);
            bw.flush();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.getSource().sendSuccess(new TranslatableComponent("commands.vaultpatcher.export.tips.success"), true);
        return 0;
    }
}
