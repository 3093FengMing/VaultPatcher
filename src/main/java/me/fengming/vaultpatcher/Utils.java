package me.fengming.vaultpatcher;

import me.fengming.vaultpatcher.config.VaultPatcherPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static final String MOD_ID = "vaultpatcher";
    public static final String MOD_NAME = "Vault Patcher";

    public static List<VaultPatcherPatch> vpps = new ArrayList<>();
    public static ArrayList<String> exportList = new ArrayList<>();
    public static Map<String, Boolean> exported = new HashMap<>();

    public static void addToExportList(String text) {
        if (!exported.getOrDefault(text, false) || !isInExportList(text)) {
            exported.put(text, true);
            exportList.add(text);
        }
    }

    public static boolean isInExportList(String text) {
        return exportList.lastIndexOf(text) != -1;
    }
}
