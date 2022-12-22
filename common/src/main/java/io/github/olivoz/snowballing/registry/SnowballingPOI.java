package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public final class SnowballingPOI {

    private SnowballingPOI() {
    }

    public static final Supplier<PoiType> SNOWBALL_PILE = RegistryManager.registerPOI("snowball_pile", 1, 5, () -> new Block[] { SnowballingBlocks.SNOWBALL_PILE.get(), Blocks.SNOW, Blocks.SNOW_BLOCK });

    public static void init() {

    }

}
