package io.github.olivoz.snowballing.datagen;

import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class SnowballingRecipeProvider extends FabricRecipeProvider {

    public SnowballingRecipeProvider(final FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(final Consumer<FinishedRecipe> exporter) {
        ShapedRecipeBuilder.shaped(SnowballingItems.SNOW_BRICKS.get(), 4)
            .define('$', Items.SNOW_BLOCK)
            .pattern("$$")
            .pattern("$$")
            .unlockedBy("has_snow_block", has(Items.SNOW_BLOCK))
            .save(exporter, "building_blocks/snow_bricks");

        ShapedRecipeBuilder.shaped(SnowballingItems.SNOWBALL_BRICKS.get(), 4)
            .define('$', SnowballingItems.SNOW_BRICKS.get())
            .pattern("$$$")
            .pattern("$$$")
            .pattern("$$$")
            .unlockedBy("has_snow_bricks", has(SnowballingItems.SNOW_BRICKS.get()))
            .save(exporter, "building_blocks/snowball_bricks_from_snow_bricks");

        ShapedRecipeBuilder.shaped(SnowballingItems.SNOWBALL_BRICKS.get())
            .define('$', Items.SNOWBALL)
            .pattern("$$$")
            .pattern("$$$")
            .pattern("$$$")
            .unlockedBy("has_snowball", has(Items.SNOWBALL))
            .save(exporter, "building_blocks/snowball_bricks_from_snowballs");
    }
}
