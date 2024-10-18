package me.fengming.vaultpatcher_asm.core.cache;

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
    private final Path hashFile;
    private boolean updated;

    public ClassCache(Path hashFile, Path classFile) throws IOException {
        this.hashFile = hashFile;
        this.cr = new ClassReader(Files.newInputStream(classFile));
    }

    private static String bytes2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getSha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return bytes2String(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash", e);
        }
    }

    public boolean updated(ClassNode clazz) {
        ClassWriter cw1 = new ClassWriter(0);
        clazz.accept(cw1);
        String currentHash = getSha256(cw1.toByteArray());

        byte[] cacheHashBytes = new byte[64];
        try (InputStream fis = Files.newInputStream(this.hashFile)) {
            fis.read(cacheHashBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read hash file", e);
        }

        String cacheHash = new String(cacheHashBytes, StandardCharsets.UTF_8);

        this.updated = currentHash.equals(cacheHash);

        if (!this.updated) {
            this.clazz = clazz;
            writeHash(currentHash, this.hashFile);
        } else {
            this.clazz = new ClassNode();
            this.cr.accept(this.clazz, 0);
        }

        return this.updated;
    }

    public void create(byte[] bytesToHash) throws IOException {
        this.updated = true;
        String currentHash = getSha256(bytesToHash);
        writeHash(currentHash, this.hashFile);

        // update(node);
    }

    public ClassNode take() {
        return this.clazz;
    }

    public void put(ClassNode node, byte[] bytesToHash) {
        if (this.updated) return;
        this.clazz = node;
        Utils.exportClass(node, Utils.getVpPath().resolve("cache"));
        String hash = getSha256(bytesToHash);
        writeHash(hash, this.hashFile);
    }

    private static void writeHash(String hash, Path hashFile) {
        try {
            if (Files.notExists(hashFile)) {
                Files.createDirectories(hashFile.getParent());
                Files.createFile(hashFile);
            }
            try (OutputStream fos = Files.newOutputStream(hashFile)) {
                fos.write(hash.getBytes(StandardCharsets.UTF_8));
                fos.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write hash file", e);
        }
    }

}
