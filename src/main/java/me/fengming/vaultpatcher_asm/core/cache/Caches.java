package me.fengming.vaultpatcher_asm.core.cache;

import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Caches {
    private static final Map<String, ClassCache> cacheMap = new HashMap<>();
    public static void initCache(Path path) throws IOException {
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
            String className = filePathToClassName(file.toPath(), root);
            cacheMap.putIfAbsent(className, new ClassCache(file.getParentFile().toPath().resolve(file.getName() + ".sha256"), file.toPath()));
        }
    }

    private static String filePathToClassName(Path path, Path root) {
        String s = root.relativize(path).toString();
        return s.substring(0, s.length() - 6).replace(File.separatorChar, '/');
    }

    public static ClassCache getClassCache(String className) {
        return cacheMap.getOrDefault(className, null);
    }

    public static void addClassCache(String className, ClassNode node, byte[] hashes) {
        File classFile = ASMUtils.exportClass(node, Utils.mcPath.resolve("vaultpatcher").resolve("cache"));
        try {
            ClassCache cache = new ClassCache(classFile.getParentFile().toPath().resolve(classFile.getName() + ".sha256"), classFile.toPath());
            cache.create(node, hashes);
            cacheMap.putIfAbsent(className, cache);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add cache", e);
        }
    }
}
