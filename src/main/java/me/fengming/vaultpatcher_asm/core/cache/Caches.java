package me.fengming.vaultpatcher_asm.core.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Caches {
    private static final Map<String, ClassCache> cacheMap = new HashMap<>();
    public static void initCache(Path path) throws IOException {
        File file = path.toFile();
        while (file != null) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children == null) break;
                for (File child : children) {
                    file = child;
                }
            } else if (file.getName().endsWith(".class")) {
                String className = filePathToClassName(file.toPath(), path);
                cacheMap.putIfAbsent(className, new ClassCache(file.getParentFile().toPath().resolve(file.getName() + ".sha256"), file.toPath()));
            }
        }
    }

    private static String filePathToClassName(Path path, Path root) {
        String s = path.relativize(root).toString();
        return s.substring(0, s.length() - 6).replace(File.pathSeparatorChar, '.');
    }

    public static ClassCache getClassCache(String className) {
        return cacheMap.getOrDefault(className, null);
    }
}
