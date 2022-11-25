package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SnowballingMod.MOD_ID)
public class SnowballingModForge {

    public SnowballingModForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();

        RegistryManager.init(modEventBus);
        SnowballingMod.init();
        MinecraftForge.EVENT_BUS.register(SnowballingListeners.class);
    }
}
