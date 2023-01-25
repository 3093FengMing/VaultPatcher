package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StringTextComponent.class, priority = -Integer.MAX_VALUE)
public abstract class StringTextComponentMixin {
    @Accessor("text")
    abstract String getText_();

    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void proxy_getContents(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText_());
        if (c != null) cir.setReturnValue(c);
    }

    @Inject(method = "getText", at = @At("HEAD"), cancellable = true)
    private void proxy_getText(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText_());
        if (c != null) cir.setReturnValue(c);
    }


}

