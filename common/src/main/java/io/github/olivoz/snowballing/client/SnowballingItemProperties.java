package io.github.olivoz.snowballing.client;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.item.SnowSling;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class SnowballingItemProperties {

    public static void register() {
        ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "full"), (itemStack, clientLevel, livingEntity, id) -> SnowSling.isFilled(itemStack) ? 1 : 0);
        ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "pull"), (itemStack, clientLevel, livingEntity, id) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks() / 20.0F : 0.0F);
        ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "pulling"), (itemStack, clientLevel, livingEntity, id) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
    }

}
