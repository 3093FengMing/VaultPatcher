package me.fengming.vaultpatcher_asm.loader;

import me.fengming.vaultpatcher_asm.core.utils.Platform;

import java.nio.file.Path;

public interface LoaderBootstrapContext {
    String loaderName();

    Platform platform();

    Path gameDir();

    boolean isClient();

    String resolveMinecraftVersion();
}
