package io.github.olivoz.snowballing.datagen;

import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class SnowballingTagProvider extends FabricTagProvider<Block> {

    public SnowballingTagProvider(final FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.BLOCK);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(SnowballingBlocks.SNOWBALL_PILE.get());
    }
}
