package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.manager.RegistryManager;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@UtilityClass
public final class SnowballingBlocks {

    public static final Supplier<Block> SNOWBALL_PILE = RegistryManager.registerBlock("snowball_pile", SnowballPileBlock::new);

    public static void init() {

    }
}
