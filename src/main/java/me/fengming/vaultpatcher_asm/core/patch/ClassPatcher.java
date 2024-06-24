package me.fengming.vaultpatcher_asm.core.patch;

import me.fengming.vaultpatcher_asm.core.cache.ClassCache;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ClassPatcher {

    private static final Map<String, ClassNode> patchMap = new HashMap<>();
    public static void init(Path path) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        traverse(file, path);
    }

    private static void traverse(File file, Path root) throws IOException {
        if (file == null) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) return;
            for (File child : children) {
                traverse(child, root);
            }
        } else if (file.getName().endsWith(".class")) {
            String className = Utils.filePathToClassName(file.toPath(), root);
            ClassNode node = new ClassNode();
            ClassReader cr = new ClassReader(Files.newInputStream(file.toPath()));
            cr.accept(node, 0);
            patchMap.putIfAbsent(className, node);
        }
    }

    public static ClassNode patch(ClassNode original) {
        ClassNode patched = patch(original.name);
        return patched == null ? original : patched;
    }

    public static ClassNode patch(String className) {
        return patchMap.getOrDefault(className, null);
    }
}
