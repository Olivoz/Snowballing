package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.SnowballingMod;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

@UtilityClass
public final class SnowballingItems {

    public static final Item SNOWBALL_PILE = new BlockItem(SnowballingBlocks.SNOWBALL_PILE, new FabricItemSettings().tab(SnowballingModFabricLike.SNOWBALLING_MOD_TAB));

    private static void register(String id, Item item) {
        Registry.register(Registry.ITEM, new ResourceLocation(SnowballingMod.MOD_ID, id), item);
    }

    public static void init() {
        register("snowball_pile", SNOWBALL_PILE);
    }

}
