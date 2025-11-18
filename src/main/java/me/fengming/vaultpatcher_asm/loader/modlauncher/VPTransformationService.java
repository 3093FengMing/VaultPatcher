package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VPTransformationService implements ITransformationService {

    private boolean disableDynamic = false;

    @Override
    public @NotNull String name() {
        return "vaultpatcher";
    }

    @Override
    public void initialize(IEnvironment environment) {
        // VaultPatcher.LOGGER.warn("[VaultPatcher] Warning! You are in ASM mode!");
        // VaultPatcher.LOGGER.warn("[VaultPatcher] In this mode, the configuration files will be stored in \"config/vaultpatcher_asm\"!");

        VaultPatcher.debugInfo("[VaultPatcher] Loading VPTransformationService!");

        Optional<Path> minecraftPathOptional = environment.getProperty(IEnvironment.Keys.GAMEDIR.get());
        if (!minecraftPathOptional.isPresent()) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Minecraft path not found!");
            return;
        }

        if (environment.getProperty(IEnvironment.Keys.ASSETSDIR.get()).isPresent()) {
            VaultPatcher.isClient = true;
        }

        VaultPatcher.platform = Platform.Forge1_13;

        String minecraftVersion = GameVersionHolder.VERSION;
        if (StringUtils.isBlank(minecraftVersion)) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Failed to get minecraft version!");
        }
        // VaultPatcher.debugInfo("[VaultPatcher] Get minecraft version: " + minecraftVersion);
        if (isLegacyVersion(minecraftVersion)) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Disable dynamic replace because the game version is 1.16.5 or below (your version: {})", minecraftVersion);
            disableDynamic = true;
        }

        Path mcPath = minecraftPathOptional.get();
        VaultPatcher.init(mcPath, minecraftVersion);

        VaultPatcher.debugInfo("[VaultPatcher] TS DONE!");
    }

    private static final int VERSION_DYNAMIC_REPLACE_INTRODUCTION = 17;

    private static boolean isLegacyVersion(final String version) {
        int indexOfDot1 = version.indexOf('.') + 1;
        int indexOfDot2 = version.indexOf('.', indexOfDot1 + 1);
        try {
            String segment2 = indexOfDot2 < 0 ? version.substring(indexOfDot1) : version.substring(indexOfDot1, indexOfDot2);
            return Integer.parseUnsignedInt(segment2) < VERSION_DYNAMIC_REPLACE_INTRODUCTION;
        } catch (Exception e) {
            VaultPatcher.LOGGER.error("[VaultPatcher] unrecognizable version {}, disabling dynamic replace.", version, e);
            return true;
        }
    }

    @Override
    public void beginScanning(@NotNull IEnvironment environment) {}

    @Override
    public void onLoad(@NotNull IEnvironment env, @NotNull Set<String> otherServices) {}

    @Override
    @SuppressWarnings("rawtypes")   // as defined
    public @NotNull List<ITransformer> transformers() {
        List<ITransformer> list = new ArrayList<>();
        if (VaultPatcherConfig.isEnableClassPatch()) {
            list.add(new PatchClassTransformer(ClassPatcher.getPatches().keySet()));
        }

        list.addAll(Utils.translationInfoMap.values().stream()
                .map(ForgeClassTransformer::new)
                .collect(Collectors.toList()));

        list.add(new ForgeClassTransformer(null));
        if (!disableDynamic) list.add(new ForgeMinecraftTransformer());
        return list;
    }

}
