package me.fengming.vaultpatcher_asm.plugin;

import me.fengming.vaultpatcher_asm.config.VaultPatcherPatch;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Path;

public interface VaultPatcherPlugin {
    String getName();
    void start(Path mcPath);
    default void onLoadCaches(Phase phase) {}
    default void onLoadConfig(Phase phase) {}
    default void onLoadPatch(VaultPatcherPatch patch, Phase phase) {}
    default void onTransformClass(ClassNode classNode, Phase phase) {}
    void end();

    enum Phase {
        BEFORE, AFTER
    }
}
