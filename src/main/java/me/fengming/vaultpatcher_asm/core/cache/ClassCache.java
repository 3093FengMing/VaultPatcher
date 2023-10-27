package me.fengming.vaultpatcher_asm.core.cache;

import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class ClassCache {

    private final ClassReader cr;
    private ClassNode clazz;
    private Path hashFile;
    private Path classFile;
    private boolean updated;
    private String cacheHash;

    public ClassCache(Path hashFile, Path classFile) throws IOException {
        this.hashFile = hashFile;
        this.classFile = classFile;
        // get cache hash
        byte[] cacheHashBytes = new byte[1024];
        try (InputStream fis = Files.newInputStream(this.hashFile)) {
            fis.read(cacheHashBytes);
        }
        this.cacheHash = bytes2String(cacheHashBytes);
        this.cr = new ClassReader(Files.newInputStream(classFile));
    }

    private static String bytes2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String getSha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return bytes2String(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash");
        }
    }

    public boolean update(ClassNode clazz) {
        ClassWriter cw1 = new ClassWriter(0);
        clazz.accept(cw1);
        String currentHash = getSha256(cw1.toByteArray());
        this.updated = currentHash.equals(this.cacheHash);

        if (!this.updated) {
            this.cacheHash = currentHash;
            this.clazz = clazz;
            try (OutputStream fos = Files.newOutputStream(this.hashFile)) {
                fos.write(currentHash.getBytes(StandardCharsets.UTF_8));
                fos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            ClassWriter cw2 = new ClassWriter(this.cr, 0);
            this.clazz = new ClassNode();
            this.clazz.accept(cw2);
        }

        return this.updated;
    }

    public ClassNode take() {
        return this.clazz;
    }

    public void put(ClassNode node) {
        if (this.updated) return;
        this.clazz = node;
        ASMUtils.exportClass(node, Utils.mcPath.resolve("vaultpatcher").resolve("cache"));
    }
}
