package me.fengming.vaultpatcher_asm.core.cache;

import me.fengming.vaultpatcher_asm.core.utils.Utils;
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

public class Caches {
    private static final Map<String, ClassCache> cacheMap = new HashMap<>();

    public static void init(Path path) throws IOException {
        if (!Utils.debug.isUseCache()) return;
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
                if (!file.getFileName().endsWith(".class")) return super.visitFile(file, attrs);

                String className = Utils.filePathToClassName(file, root);
                cacheMap.putIfAbsent(className, new ClassCache(file.getParent().resolve(file.getFileName() + ".sha256"), file));
                return super.visitFile(file, attrs);
            }
        });
    }

    public static ClassCache getClassCache(String className) {
        return cacheMap.getOrDefault(className, null);
    }

    public static void addClassCache(String className, ClassNode node, byte[] hash) {
        File classFile = Utils.exportClass(node, Utils.getVpPath().resolve("cache"));
        try {
            ClassCache cache = new ClassCache(classFile.getParentFile().toPath().resolve(classFile.getName() + ".sha256"), classFile.toPath());
            cache.create(hash);
            cacheMap.put(className, cache);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add cache", e);
        }
    }
}
