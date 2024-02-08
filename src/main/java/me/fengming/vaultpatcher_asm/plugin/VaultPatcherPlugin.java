package me.fengming.vaultpatcher_asm.plugin;

import java.nio.file.Path;

public interface VaultPatcherPlugin {
    public void start(Path mcPath);
    public void end();
}
