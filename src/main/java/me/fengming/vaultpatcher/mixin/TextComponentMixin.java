package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextComponent.class)
public class TextComponentMixin {
    @Accessor("text") private native String getText();


    /** @author teddyxlandlee, FengMing */

    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void modifyContents(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText());
        if (c != null) cir.setReturnValue(c);
    }


}

