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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassPatcher {
    private static final Map<String, ClassNode> PATCHES = new HashMap<>();

    private static final Set<String> DELETES = new HashSet<>();

    public static Map<String, ClassNode> getPatches() {
        return PATCHES;
    }

    public static Set<String> getDeletes() { return DELETES; }

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
                VaultPatcher.debugInfo("Found patch: {}", file);
                String className = Utils.filePathToClassName(file, root);
                if (Files.size(file) == 0) {
                    DELETES.add(className);
                    return super.visitFile(file, attrs);
                }
                ClassNode node = new ClassNode();
                ClassReader cr = new ClassReader(Files.newInputStream(file));
                cr.accept(node, 0);
                PATCHES.putIfAbsent(className, node);
                return super.visitFile(file, attrs);
            }
        });
    }

    public static ClassNode makeEmptyClass(ClassNode inputNode) {
        inputNode.fields.clear();
        inputNode.methods.clear();
        if (inputNode.innerClasses != null) inputNode.innerClasses.clear();
        inputNode.outerClass=null;
        inputNode.outerMethod=null;
        inputNode.outerMethodDesc=null;
        inputNode.visibleAnnotations=null;
        inputNode.invisibleAnnotations=null;
        inputNode.attrs=null;
        try {
            inputNode.visibleTypeAnnotations=null;
            inputNode.invisibleTypeAnnotations=null;
            if (inputNode.nestMembers != null) inputNode.nestMembers.clear();
            inputNode.nestHostClass=null;
        } catch (Throwable ignored) {
            // Some properties are not supported in older ASM
        }
        return inputNode;
    }

    public static ClassNode patch(String className) {
        return PATCHES.getOrDefault(className, null);
    }

    public static ClassNode delete(ClassNode inputNode) {
        if (DELETES.contains(inputNode.name)) {
            return makeEmptyClass(inputNode);
        } else {
            return null;
        }
    }

}
