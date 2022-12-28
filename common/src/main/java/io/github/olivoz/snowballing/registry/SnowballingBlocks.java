package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public final class SnowballingBlocks {

    private SnowballingBlocks() {
    }

    public static final Supplier<Block> SNOWBALL_PILE = RegistryManager.registerBlock("snowball_pile", SnowballPileBlock::new);
    public static final Supplier<Block> SNOWBRICKS = RegistryManager.registerBlock("snow_bricks", () ->
            new Block(
                    BlockBehaviour.Properties.of(Material.SNOW)
                            .requiresCorrectToolForDrops()
                            .dropsLike(Blocks.SNOW_BLOCK)
                            .strength(0.2f)
            ));

    public static void init() {

    }
}
