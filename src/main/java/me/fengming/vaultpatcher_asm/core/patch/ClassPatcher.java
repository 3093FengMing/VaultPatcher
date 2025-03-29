package me.fengming.vaultpatcher_asm.core.patch;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class ClassPatcher {

    private static final Map<String, ClassNode> patchMap = new HashMap<>();

    public static Map<String, ClassNode> getPatchMap() {
        return patchMap;
    }

    public static void init(Path path) throws IOException {
        if (!VaultPatcherConfig.isEnableClassPatch()) return;
        File file = path.toFile();
        if (Files.notExists(path)) {
            file.mkdirs();
        }
        traverse(path);
    }

    private static void traverse(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toString().endsWith(".class")) return super.visitFile(file, attrs);
                VaultPatcher.debugInfo("[VaultPatcher] Found patch: {}", file);
                String className = Utils.filePathToClassName(file, root);
                ClassNode node = new ClassNode();
                ClassReader cr = new ClassReader(Files.newInputStream(file));
                cr.accept(node, 0);
                patchMap.putIfAbsent(className, node);
                return super.visitFile(file, attrs);
            }
        });
    }

    public static ClassNode patch(String className) {
        return patchMap.getOrDefault(className, null);
    }
}
