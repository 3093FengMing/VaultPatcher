package me.fengming.vaultpatcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import me.fengming.vaultpatcher.config.translationObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

@Mod("vaultpatcher")
public class VaultPatcher
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Path config =
            FMLPaths.CONFIGDIR.get().resolve("vaultpatcher");
    public static final File configFile = config.resolve("config.json").toFile();
    public static ArrayList<translationObject> translationObjects = new ArrayList<>();
    public static boolean[] iteratedArray = {};

    public VaultPatcher() {
        Gson gson = new Gson();
        String cfjson = "";
        try {
            if (Files.notExists(config)) {
                Files.createDirectories(config);
            }
            //Read Config
            if (!configFile.exists()) { //No exists
                configFile.createNewFile();
            }
            if (configFile.canRead()) {
                BufferedReader br = new BufferedReader(new FileReader(configFile, StandardCharsets.UTF_8));
                String s;
                while ((s = br.readLine()) != null) {
                    cfjson += s + "\n";
                }
                br.close();
            } else {
                LOGGER.warn("Cannot Read " + configFile + " !");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Patch Json: " + cfjson);
        //Config
        translationObjects = new ArrayList<>();
        translationObjects = gson.fromJson(cfjson, new TypeToken<ArrayList<translationObject>>() {}.getType());
        iteratedArray = new boolean[translationObjects.size() + 2];
        Arrays.fill(iteratedArray, false);
    }
}
