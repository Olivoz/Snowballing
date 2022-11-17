package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SnowballingMod.MOD_ID)
public class SnowballingModForge {

    public SnowballingModForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();

        SnowballingMod.init();
        SnowballingBlocks.init(modEventBus);
    }
}
