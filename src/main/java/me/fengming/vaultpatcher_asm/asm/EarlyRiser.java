package me.fengming.vaultpatcher_asm.asm;

import com.chocohead.mm.api.ClassTinkerers;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        VaultPatcher.init(FabricLoader.getInstance());
        // Patch
        for (TranslationInfo info : Utils.translationInfos) {
            String cn = info.getTargetClassInfo().getName();
            if (!cn.isEmpty()) {
                ClassTinkerers.addTransformation(cn, new ClassTransformer(info));
            }
        }
        // Apply Mods
        List<String> targetMods = VaultPatcherConfig.getApplyMods();
        for (String targetMod : targetMods) {
            Utils.getClassesNameByJar(Utils.mcPath.resolve("mods").resolve(targetMod + ".jar").toString()).forEach((s) -> ClassTinkerers.addTransformation(s, new ClassTransformer(null)));
        }
        // Classes
        VaultPatcherConfig.getClasses().forEach((s) -> ClassTinkerers.addTransformation(s, new ClassTransformer(null)));
    }
}
