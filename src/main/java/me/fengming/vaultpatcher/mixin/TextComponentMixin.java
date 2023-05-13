package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
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

    @Mutable
    @Shadow
    @Final
    private String text;

    // So, just this

    @Inject(method = "<init>", at = @At("RETURN"))
    private void proxy_init(String pText, CallbackInfo ci) {
        String c = ThePatcher.patch(this.text, "TextComponent#init");
        if (c == null) {
            this.text = pText;
        } else this.text = c;
    }

}

