package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import me.fengming.vaultpatcher.Utils;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BaseComponent.class)
public abstract class BaseComponentMixin {
    /*
    @Accessor("siblings")
    abstract List<Component> getSiblings();

    @Inject(method = "getSiblings", at = @At("HEAD"), cancellable = true)
    private void proxy_getSiblings(CallbackInfoReturnable<List<Component>> cir) {
        List<Component> list = this.getSiblings();
        if (!list.isEmpty()) {
            list.replaceAll(component -> {
                if (component instanceof TextComponent) {
                    return new TextComponent(ThePatcher.patch(component.getContents()));
                } else return component;
            });
        }
        cir.setReturnValue(list);
    }
    */

    @Shadow
    @Final
    protected List<Component> siblings;

    @Shadow
    public abstract MutableComponent copy();

    @Shadow
    public abstract Style getStyle();

    @ModifyArg(
            method = "getVisualOrderText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/locale/Language;getVisualOrder(Lnet/minecraft/network/chat/FormattedText;)Lnet/minecraft/util/FormattedCharSequence;"
            )
    )
    private FormattedText proxy_getVisualOrder(FormattedText p_128116_) {
        if (p_128116_ instanceof TextComponent) {
            String c = ThePatcher.patch(p_128116_.getString());
            return new TextComponent(c).setStyle(this.getStyle());
        }
        return p_128116_;
    }

    @Inject(
            method = "append",
            at = @At("HEAD"),
            cancellable = true
    )
    private void proxy_append(Component p_130585_, CallbackInfoReturnable<MutableComponent> cir) {
        String c = ThePatcher.patch(p_130585_.getString());
        this.siblings.add(new TextComponent(c).setStyle(this.getStyle()));
        cir.setReturnValue(this.copy());
    }

}
