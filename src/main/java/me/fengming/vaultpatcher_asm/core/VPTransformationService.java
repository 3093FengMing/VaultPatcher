package me.fengming.vaultpatcher_asm.core;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VPTransformationService implements ITransformationService {
    @Override
    public @NotNull String name() {
        return "vaultpatcher";
    }

    @Override
    public void initialize(IEnvironment environment) {
        VaultPatcher.LOGGER.warn("[VaultPatcher] Warning! You are in ASM mode!");
        VaultPatcher.LOGGER.warn("[VaultPatcher] In this mode, the configuration files will be stored in \"config/vaultpatcher_asm\"!");
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformationService!");
        Optional<Path> minecraftPath = environment.getProperty(IEnvironment.Keys.GAMEDIR.get());
        if (minecraftPath.isEmpty()) {
            VaultPatcher.LOGGER.warn("Minecraft path not found");
            return;
        }
        VaultPatcher.init(minecraftPath.get());
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {

    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of(new VPClassTransformer());
    }
}
