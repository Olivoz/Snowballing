package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.item.SnowSling;
import io.github.olivoz.snowballing.manager.RegistryManager;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SnowballingMod.MOD_ID)
public class SnowballingModForge {

    public SnowballingModForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();

        RegistryManager.init(modEventBus);
        SnowballingMod.init();
        MinecraftForge.EVENT_BUS.register(SnowballingListeners.class);
        modEventBus.addListener(this::setupClient);
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "full"), (itemStack, clientLevel, livingEntity, id) -> SnowSling.isFilled(itemStack) ? 1 : 0);
            ItemProperties.register(SnowballingItems.SNOW_SLING.get(), new ResourceLocation(SnowballingMod.MOD_ID, "charged"), (itemStack, clientLevel, livingEntity, id) -> SnowSling.isCharged(itemStack) ? 1 : 0);
        });
    }

}
