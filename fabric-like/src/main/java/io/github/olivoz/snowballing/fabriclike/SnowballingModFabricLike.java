package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.SnowballingMod;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

@UtilityClass
public final class SnowballingModFabricLike {

    public static final CreativeModeTab SNOWBALLING_MOD_TAB = FabricItemGroupBuilder.build(new ResourceLocation(SnowballingMod.MOD_ID, "items"), Items.SNOWBALL::getDefaultInstance);

    public static void init() {
        SnowballingMod.init();
        SnowballingBlocks.init();
        SnowballingItems.init();
        SnowballingListeners.init();
    }
}
