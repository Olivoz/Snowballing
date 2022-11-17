package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.block.SnowPileBlock;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@UtilityClass
public final class SnowballingBlocks {

    public static final Block SNOWBALL_PILE = new SnowPileBlock();

    private static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, new ResourceLocation(SnowballingMod.MOD_ID, id), block);
    }

    public static void init() {
        register("snowball_pile", SNOWBALL_PILE);
    }

}
