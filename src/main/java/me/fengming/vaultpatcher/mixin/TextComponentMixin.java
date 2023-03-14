package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import me.fengming.vaultpatcher.Utils;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextComponent.class)
public abstract class TextComponentMixin {

    @Mutable @Shadow @Final private String text;

    @Accessor("text")
    abstract String getText_();

    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void proxy_getContents(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText_(), "TextComponent#getContents");
        if (c != null) {
            this.text = c;
            cir.setReturnValue(c);
        }
    }

    @Inject(method = "getText", at = @At("HEAD"), cancellable = true)
    private void proxy_getText(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText_(), "TextComponent#getText");
        if (c != null) {
            this.text = c;
            cir.setReturnValue(c);
        }
    }

}

