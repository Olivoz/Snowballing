package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class SnowballingBlocks {

    private SnowballingBlocks() {
    }

    public static final Supplier<Block> SNOWBALL_PILE = RegistryManager.registerBlock("snowball_pile", SnowballPileBlock::new);

    public static void init() {

    }
}
