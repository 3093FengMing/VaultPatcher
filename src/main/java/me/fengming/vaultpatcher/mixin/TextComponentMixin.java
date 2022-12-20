package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextComponent.class)
public abstract class TextComponentMixin {
    @Accessor("text") abstract String getText();
    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void modifyContents(CallbackInfoReturnable<String> cir) {
        VaultPatcher.LOGGER.info(this.getText());
        String c = ThePatcher.patch(this.getText());
        if (c != null) cir.setReturnValue(c);
    }


}

