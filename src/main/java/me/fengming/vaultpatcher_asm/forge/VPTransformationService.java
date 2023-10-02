package me.fengming.vaultpatcher_asm.forge;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VPTransformationService implements ITransformationService {

    private boolean oldVersion = false;

    @Override
    public @NotNull String name() {
        return "vaultpatcher";
    }

    @Override
    public void initialize(IEnvironment environment) {
        // VaultPatcher.LOGGER.warn("[VaultPatcher] Warning! You are in ASM mode!");
        // VaultPatcher.LOGGER.warn("[VaultPatcher] In this mode, the configuration files will be stored in \"config/vaultpatcher_asm\"!");

        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformationService!");

        Optional<Path> minecraftPathOptional = environment.getProperty(IEnvironment.Keys.GAMEDIR.get());
        if (!minecraftPathOptional.isPresent()) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Minecraft path not found!");
            return;
        }

        String minecraftVersion = getMinecraftVersion();
        if (Utils.isBlank(minecraftVersion)) VaultPatcher.LOGGER.error("[VaultPatcher] Failed to get minecraft version!");
        // VaultPatcher.LOGGER.info("[VaultPatcher] Get minecraft version: " + minecraftVersion);
        if (isOldVersion(minecraftVersion)) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Disable dynamic replace because you are in old version (" + minecraftVersion + "<=1.16.5)");
            oldVersion = true;
        }

        VaultPatcher.init(minecraftPathOptional.get());
    }

    public static boolean isOldVersion(String version) {
        String _116 = "1.16.5";
        if (_116.equals(version)) return true;
        for (int i = 0; i < 6; i++) {
            if (version.charAt(i) < _116.charAt(i)) return true;
        }
        return false;
    }

    private static String getMinecraftVersion() {
        try {
            Object Instance_Launcher = Launcher.INSTANCE;
            Field Field_argumentHandler = Instance_Launcher.getClass().getDeclaredField("argumentHandler");
            Field_argumentHandler.setAccessible(true);

            Object Instance_argumentHandler = Field_argumentHandler.get(Instance_Launcher);
            Field Field_args = Instance_argumentHandler.getClass().getDeclaredField("args");
            Field_args.setAccessible(true);

            String[] Instance_args = (String[]) Field_args.get(Instance_argumentHandler);
            for (int i = 0; i < Instance_args.length; i++) {
                if (Instance_args[i].equals("--fml.mcVersion")) return Instance_args[i + 1];
            }

        } catch (Exception e) {
            throw new IllegalStateException("WHY ARE YOU HERE!!??");
        }
        return "";
    }

    @Override
    public void beginScanning(IEnvironment environment) {}

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {}

    @Override
    public @NotNull List<ITransformer> transformers() {
        List<ITransformer> list = new ArrayList<>();
        list.addAll(Utils.translationInfos.stream().collect(ArrayList::new, (l1, info) -> l1.add(new ForgeClassTransformer(info)), ArrayList::addAll));
        list.add(new ForgeClassTransformer(null));
        if (!oldVersion) list.add(new ForgeMinecraftTransformer());
        return list;
    }
}
