package me.fengming.vaultpatcher.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ExportCommand implements Command<CommandSourceStack> {
    public static ExportCommand instance = new ExportCommand();
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TextComponent("Warning: This is *WIP*"), true);
//        Gson gson = new Gson();
//        String json = gson.toJson(VaultPatcher.patchMap, new TypeToken<HashMap<String, String>>(){}.getType());
//        //Export Patch
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(VaultPatcher.configFile, StandardCharsets.UTF_8));
//            bw.write(json);
//            bw.flush();
//
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        context.getSource().sendSuccess(new TextComponent("Export langpatcher.json"), true);
        return 0;
    }
}
