package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SnowballingMod.MOD_ID)
public class SnowballingModForge {

    public static final CreativeModeTab SNOWBALLING_MOD_TAB = new CreativeModeTab(SnowballingMod.MOD_ID + ".items") {
        @Override
        public ItemStack makeIcon() {
            return Items.SNOWBALL.getDefaultInstance();
        }
    };

    public SnowballingModForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();

        SnowballingMod.init();
        SnowballingBlocks.init(modEventBus);
        SnowballingItems.init(modEventBus);
    }
}
