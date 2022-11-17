package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@UtilityClass
public final class SnowballingItems {

    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SnowballingMod.MOD_ID);
    public static final RegistryObject<BlockItem> SNOWBALL_PILE = REGISTRY.register("snowball_pile", () -> new BlockItem(SnowballingBlocks.SNOWBALL_PILE.get(), new Item.Properties().tab(SnowballingModForge.SNOWBALLING_MOD_TAB)));

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }

}
