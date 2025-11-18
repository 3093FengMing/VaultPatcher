package me.fengming.vaultpatcher_asm.loader.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.transformers.VPMinecraftTransformer;
import me.fengming.vaultpatcher_asm.core.utils.ModClassDiscovery;
import me.fengming.vaultpatcher_asm.core.utils.Platform;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.nio.file.Path;
import java.util.Locale;
import java.util.NoSuchElementException;

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

    private enum FabricEnvironment implements Comparable<FabricEnvironment> {
        PRE_1_20_3("net.minecraft.class_327", "net.minecraft.class_2585"),
        POST_1_20_3("net.minecraft.class_327", "net.minecraft.class_8828$class_2585"),
        MOJ_MAPPED("net.minecraft.client.gui.Font", "net.minecraft.chat.contents.PlainTextContents$LiteralContents"),
        ;
        final String nameFont;
        final String nameLiteralContents;

        FabricEnvironment(String nameFont, String nameLiteralContents) {
            this.nameFont = nameFont;
            this.nameLiteralContents = nameLiteralContents;
        }

        static final net.fabricmc.loader.api.Version MC_VERSION;
        static final FabricEnvironment CURRENT;

        static {
            MC_VERSION = FabricLoader.getInstance().getModContainer("minecraft")
                    .orElseThrow(() -> new NoSuchElementException("Mod container 'minecraft' is absent!"))
                    .getMetadata()
                    .getVersion();

            FabricEnvironment current = null;
            if (MC_VERSION instanceof net.fabricmc.loader.api.SemanticVersion) {
                if (((SemanticVersion) MC_VERSION).getBuildKey().orElse("").toLowerCase(Locale.ROOT).contains("unobfuscated")) {
                    current = MOJ_MAPPED;
                }
            }
            if (current == null) {
                if (versionPredicate(">=1.21.12-").test(MC_VERSION)) {
                    // Mojang claims to remove obfuscation right after Mounts of Mayhem
                    current = MOJ_MAPPED;
                } else if (versionPredicate(">=1.20.3-").test(MC_VERSION)) {
                    current = POST_1_20_3;
                } else {
                    current = PRE_1_20_3;
                }
            }
            CURRENT = current;
        }
    }

    private static VersionPredicate versionPredicate(String p) {
        try {
            return VersionPredicate.parse(p);
        } catch (VersionParsingException e) {
            throw new IllegalStateException("Your Fabric Loader sucks", e);
        }
    }

    private static void addMinecraftClasses() {
        // net/minecraft/client/gui/Font
        ClassTinkerers.addTransformation(FabricEnvironment.CURRENT.nameFont, new VPMinecraftTransformer());
        // net/minecraft/chat/contents/LiteralContents
        // In 1.20.3+: PlainTextContents$LiteralContents 8828$2585
        ClassTinkerers.addTransformation(FabricEnvironment.CURRENT.nameLiteralContents, new VPMinecraftTransformer());
    }

    private static void addExpandClasses() {
        ModClassDiscovery.getApplyClassNames().forEach(cn -> ClassTinkerers.addTransformation(cn, new VPClassTransformer(null)));
        VaultPatcherConfig.getClasses().forEach(s -> ClassTinkerers.addTransformation(s, new VPClassTransformer(null)));
    }

    private static String getMinecraftVersion() {
        return FabricEnvironment.MC_VERSION.getFriendlyString();
    }
}
