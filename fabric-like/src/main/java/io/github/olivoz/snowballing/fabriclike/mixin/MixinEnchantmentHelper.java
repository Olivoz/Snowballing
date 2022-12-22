package io.github.olivoz.snowballing.fabriclike.mixin;

import io.github.olivoz.snowballing.item.SnowSling;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public final class MixinEnchantmentHelper {

    private MixinEnchantmentHelper() {
    }

    @Inject(method = "getAvailableEnchantmentResults", at = @At(value = "RETURN"))
    private static void snowballingMixinGetAvailableEnchantmentResults(final int enchantmentCost, final ItemStack itemStack, final boolean isTreasure, final CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if(!(itemStack.getItem() instanceof SnowSling)) return;

        List<EnchantmentInstance> availableEnchantments = cir.getReturnValue();

        for(Enchantment enchantment : SnowSling.ALLOWED_ENCHANTMENTS) {
            for(int maxLevel = enchantment.getMaxLevel(); maxLevel > enchantment.getMinLevel() - 1; --maxLevel) {
                if(enchantmentCost >= enchantment.getMinCost(maxLevel) && enchantmentCost <= enchantment.getMaxCost(maxLevel)) {
                    availableEnchantments.add(new EnchantmentInstance(enchantment, maxLevel));
                    break;
                }
            }
        }

    }

}
