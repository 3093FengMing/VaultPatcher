package me.fengming.vaultpatcher_asm.loader.modlauncher;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrap;
import me.fengming.vaultpatcher_asm.loader.LoaderBootstrapContext;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VPTransformationService implements ITransformationService {

    @Override
    public @NotNull String name() {
        return "vaultpatcher";
    }

    @Override
    public void initialize(IEnvironment environment) {
        // VaultPatcher.LOGGER.warn("[VaultPatcher] Warning! You are in ASM mode!");
        // VaultPatcher.LOGGER.warn("[VaultPatcher] In this mode, the configuration files will be stored in \"config/vaultpatcher_asm\"!");

        VaultPatcher.debugInfo("[VaultPatcher] Loading VPTransformationService!");

        final Optional<Path> minecraftPathOptional = environment.getProperty(IEnvironment.Keys.GAMEDIR.get());
        final boolean isClient = environment.getProperty(IEnvironment.Keys.ASSETSDIR.get()).isPresent();
        final String minecraftVersion = LoaderBootstrap.resolveMinecraftVersion(new LoaderBootstrapContext() {
            @Override
            public String loaderName() {
                return "ModLauncher";
            }

            @Override
            public Platform platform() {
                return Platform.Forge1_13;
            }

            @Override
            public Path gameDir() {
                return minecraftPathOptional.orElse(null);
            }

            @Override
            public boolean isClient() {
                return isClient;
            }

            @Override
            public String resolveMinecraftVersion() {
                return getMinecraftVersion();
            }
        });


        LoaderBootstrap.bootstrap(new LoaderBootstrapContext() {
            @Override
            public String loaderName() {
                return "ModLauncher";
            }

            @Override
            public Platform platform() {
                return Platform.Forge1_13;
            }

            @Override
            public Path gameDir() {
                return minecraftPathOptional.orElse(null);
            }

            @Override
            public boolean isClient() {
                return isClient;
            }

            @Override
            public String resolveMinecraftVersion() {
                return minecraftVersion;
            }
        });

        VaultPatcher.debugInfo("[VaultPatcher] TS DONE!");
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
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public void beginScanning(IEnvironment environment) {}

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {}

    @Override
    public List<ITransformer> transformers() {
        List<ITransformer> list = new ArrayList<>();
        if (VaultPatcherConfig.isEnableClassPatch()) {
            list.add(new PatchClassTransformer(ClassPatcher.getPatches().keySet()));
        }

        list.addAll(Utils.translationInfoMap.values().stream()
                .map(ForgeClassTransformer::new)
                .collect(Collectors.toList()));

        list.add(new ForgeClassTransformer(null));
        boolean hasDynamicRules = !Utils.dynTranslationInfos.isEmpty();
        VaultPatcher.debugInfo("[VaultPatcher] Dynamic hook transformer enabled: {} (dynamic rules: {})",
                hasDynamicRules, Utils.dynTranslationInfos.size());
        if (hasDynamicRules) {
            list.add(new ForgeMinecraftTransformer());
        }
        return list;
    }

}
