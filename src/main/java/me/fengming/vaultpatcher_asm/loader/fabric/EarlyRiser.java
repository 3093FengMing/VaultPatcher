package me.fengming.vaultpatcher_asm.loader.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        VaultPatcher.debugInfo("[VaultPatcher] Loading VPEarlyRiser");

        VaultPatcher.platform = Platform.Fabric;

        Path mcPath = FabricLoader.getInstance().getGameDir();
        VaultPatcher.isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        VaultPatcher.init(mcPath, getMinecraftVersion());

        // do patches
        if (VaultPatcherConfig.isEnableClassPatch()) {
            ClassPatcher.getPatches().forEach((k, v) -> ClassTinkerers.addReplacement(k, n -> Utils.deepCopyClass(n, v)));
        }

        Utils.translationInfoMap.forEach((k, v) -> ClassTinkerers.addTransformation(k, new VPClassTransformer(v)));

        addExpandClasses();
        addMinecraftClasses();

        VaultPatcher.debugInfo("[VaultPatcher] ER DONE!");
    }

    private static void addMinecraftClasses() {
        ClassTinkerers.addTransformation("net.minecraft.class_327", new VPMinecraftTransformer());
        ClassTinkerers.addTransformation("net.minecraft.class_2585", new VPMinecraftTransformer());
    }

    private static void addExpandClasses() {
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            Utils.getClassesNameByJar(VaultPatcher.mcPath.resolve("mods").resolve(targetMod + ".jar").toString())
                    .forEach(s -> ClassTinkerers.addTransformation(s.substring(0, s.length() - 6), new VPClassTransformer(null)));
        }

        VaultPatcherConfig.getClasses().forEach(s -> ClassTinkerers.addTransformation(s, new VPClassTransformer(null)));
    }

    private static String getMinecraftVersion() {
        try {
            // Fabric
            return getNormalizedGameVersion(Class.forName("net.fabricmc.loader.impl.FabricLoaderImpl"));
        } catch (Exception ignored) {}
        try {
            // Quilt
            return getNormalizedGameVersion(Class.forName("org.quiltmc.loader.impl.QuiltLoaderImpl"));
        } catch (Exception ignored) {}
        return null;
    }

    private static String getNormalizedGameVersion(Class<?> clazz) throws Exception {
        Field field = clazz.getDeclaredField("INSTANCE");
        field.setAccessible(true);
        Object instance = field.get(null);
        Object gameProvider = instance.getClass().getDeclaredMethod("getGameProvider").invoke(instance);
        return (String) gameProvider.getClass().getDeclaredMethod("getNormalizedGameVersion").invoke(gameProvider);
    }
}
