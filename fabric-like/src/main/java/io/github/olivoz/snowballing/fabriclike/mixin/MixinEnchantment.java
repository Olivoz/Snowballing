package io.github.olivoz.snowballing.fabriclike.mixin;

import io.github.olivoz.snowballing.item.SnowSling;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public final class MixinEnchantment {

    @Inject(method = "canEnchant", at = @At(value = "RETURN"), cancellable = true)
    private void snowballingMixinGetAvailableEnchantmentResults(final ItemStack itemStack, final CallbackInfoReturnable<Boolean> cir) {
        if(!(itemStack.getItem() instanceof SnowSling snowSling)) return;
        if(cir.getReturnValue()) return;

        Enchantment enchantment = (Enchantment) (Object) this;
        if(!snowSling.canApplyAtEnchantingTable(itemStack, enchantment)) return;

        cir.setReturnValue(true);
    }

}
