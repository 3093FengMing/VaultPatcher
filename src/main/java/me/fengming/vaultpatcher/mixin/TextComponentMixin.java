package me.fengming.vaultpatcher.mixin;

import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.fengming.vaultpatcher.util.util.translationString;

@Mixin(TextComponent.class)
public class TextComponentMixin {
    @Mutable
    @Final
    @Shadow
    private String text;


    /** @author teddyxlandlee, FengMing */

    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void modifyContents(CallbackInfoReturnable<String> cir) {
        var c = translationString(this.text);
        cir.setReturnValue(c);
    }


}

