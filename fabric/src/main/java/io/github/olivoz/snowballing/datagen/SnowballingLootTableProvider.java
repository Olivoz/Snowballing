package io.github.olivoz.snowballing.datagen;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.data.SnowballingPointsValue;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingLootContextParamSets;
import io.github.olivoz.snowballing.registry.SnowballingLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class SnowballingLootTableProvider extends SimpleFabricLootTableProvider {

    private static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
            .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))
        .invert();

    public SnowballingLootTableProvider(final FabricDataGenerator dataGenerator) {
        super(dataGenerator, LootContextParamSets.BLOCK);
    }

    @Override
    public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> consumer) {

        consumer.accept(SnowballingBlocks.SNOWBALL_PILE.get()
            .getLootTable(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                .add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(SnowballPileBlock.SNOWBALLS.getPossibleValues(), size -> LootItem.lootTableItem(Items.SNOWBALL)
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SnowballingBlocks.SNOWBALL_PILE.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(SnowballPileBlock.SNOWBALLS, size)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(size + 1))))
                    .when(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(SnowballPileBlock.SNOWBALLS.getPossibleValues(), size -> size == 1 ? snowballPileDropForSize(size) : snowballPileDropForSize(size).apply(SetNbtFunction.setTag(snowballPileNBT(size))))))));

        consumer.accept(SnowballingLootTables.SNOWBALL_FIGHT_END, LootTable.lootTable()
            .setParamSet(SnowballingLootContextParamSets.SNOWBALL_FIGHT)
            .withPool(LootPool.lootPool()
                .when(ValueCheckCondition.hasValue(SnowballingPointsValue.INSTANCE, IntRange.lowerBound(-5)))
                .add(LootTableReference.lootTableReference(SnowballingLootTables.SNOWBALL_FIGHT_LOWBALLER)))
            .withPool(LootPool.lootPool()
                .when(ValueCheckCondition.hasValue(SnowballingPointsValue.INSTANCE, IntRange.range(6, 18)))
                .add(LootTableReference.lootTableReference(SnowballingLootTables.SNOWBALL_FIGHT_SNOVICE)))
            .withPool(LootPool.lootPool()
                .when(ValueCheckCondition.hasValue(SnowballingPointsValue.INSTANCE, IntRange.lowerBound(19)))
                .add(LootTableReference.lootTableReference(SnowballingLootTables.SNOWBALL_FIGHT_SNOWBALLER))));

        consumer.accept(SnowballingLootTables.SNOWBALL_FIGHT_LOWBALLER, LootTable.lootTable()
            .setParamSet(SnowballingLootContextParamSets.SNOWBALL_FIGHT)
            .withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.EMERALD)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F))))
                .add(LootItem.lootTableItem(Items.WOODEN_SHOVEL))));

        consumer.accept(SnowballingLootTables.SNOWBALL_FIGHT_SNOVICE, LootTable.lootTable()
            .setParamSet(SnowballingLootContextParamSets.SNOWBALL_FIGHT)
            .withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.EMERALD)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 10.0F))))
                .add(LootItem.lootTableItem(Items.IRON_SHOVEL))
                .add(LootItem.lootTableItem(Items.DIAMOND_SHOVEL)
                    .setWeight(-5))));

        consumer.accept(SnowballingLootTables.SNOWBALL_FIGHT_SNOWBALLER, LootTable.lootTable()
            .setParamSet(SnowballingLootContextParamSets.SNOWBALL_FIGHT)
            .withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.EMERALD)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(10.0F, 15.0F))))
                .add(LootItem.lootTableItem(Items.IRON_SHOVEL)
                    .setWeight(-5))
                .add(LootItem.lootTableItem(Items.DIAMOND_SHOVEL))));
    }

    private LootPoolSingletonContainer.Builder<?> snowballPileDropForSize(int size) {
        return LootItem.lootTableItem(SnowballingBlocks.SNOWBALL_PILE.get())
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SnowballingBlocks.SNOWBALL_PILE.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                    .hasProperty(SnowballPileBlock.SNOWBALLS, size)));
    }

    private CompoundTag snowballPileNBT(int size) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", size);
        return tag;
    }
}
