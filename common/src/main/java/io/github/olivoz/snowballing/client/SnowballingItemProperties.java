package io.github.olivoz.snowballing.client;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.item.SnowSling;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class SnowballingItemProperties {

    public static void register() {
        ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "full"), (itemStack, clientLevel, livingEntity, id) -> SnowSling.isFilled(itemStack) ? 1 : 0);
        ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "charged"), (itemStack, clientLevel, livingEntity, id) -> SnowSling.isCharged(itemStack) ? 1 : 0);
    }

}
