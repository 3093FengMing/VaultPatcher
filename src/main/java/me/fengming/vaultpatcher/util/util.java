package me.fengming.vaultpatcher.util;

import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.resources.language.I18n;

public class util {

    public static String translationString(String s) {
        var c = s;
        for (int i = 0; i < VaultPatcher.translationObjects.size(); i++) {
            if (VaultPatcher.iteratedArray[i]) {
                continue;
            }
            var translationObject = VaultPatcher.translationObjects.get(i);
            if (s.equals(translationObject.key)) {
                StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
                VaultPatcher.LOGGER.info(translationObject.toString());
                if (stElements[translationObject.target_class.stack_depth].
                        getClassName().equals(translationObject.target_class.name) ||
                        (translationObject.target_class.name.equals("") &&
                        translationObject.target_class.stack_depth == 0)) {
                    c = I18n.get(translationObject.value);
                    VaultPatcher.iteratedArray[i] = true;
                }
            }
        }
        return c;
    }
}
