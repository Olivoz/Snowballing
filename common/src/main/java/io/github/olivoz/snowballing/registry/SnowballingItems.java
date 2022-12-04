package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.item.SnowSling;
import io.github.olivoz.snowballing.manager.RegistryManager;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

@UtilityClass
public final class SnowballingItems {

    public static final CreativeModeTab SNOWBALLING_MOD_TAB = RegistryManager.registerTab("items");

    public static final Supplier<Item> SNOWBALL_PILE = RegistryManager.registerItem("snowball_pile", () -> new BlockItem(SnowballingBlocks.SNOWBALL_PILE.get(), new Item.Properties().tab(SNOWBALLING_MOD_TAB)));
    public static final Supplier<Item> SNOW_SLING = RegistryManager.registerItem("snow_sling", SnowSling::new);

    public static void init() {
    }
}
