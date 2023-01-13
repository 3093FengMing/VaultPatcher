package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;

@Mixin(value = TextComponent.class, priority = Integer.MAX_VALUE)
public abstract class TextComponentMixin {
    @Accessor("text")
    abstract String getText1();

    @Inject(method = "getContents", at = @At("HEAD"), cancellable = true)
    private void proxy_getContents(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText1());
        if (c != null) cir.setReturnValue(c);
    }
    @Inject(method = "getText", at = @At("HEAD"), cancellable = true)
    private void proxy_getText(CallbackInfoReturnable<String> cir) {
        String c = ThePatcher.patch(this.getText1());
        if (c != null) cir.setReturnValue(c);
    }

}

