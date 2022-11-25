package io.github.olivoz.snowballing.datagen;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class SnowballingBlockLootProvider extends SimpleFabricLootTableProvider {

    private static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
            .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))
        .invert();

    public SnowballingBlockLootProvider(final FabricDataGenerator dataGenerator) {
        super(dataGenerator, LootContextParamSets.BLOCK);
    }

    @Override
    public void accept(final BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {

        LootTable.Builder snowballPileLootTable = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                .add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(SnowPileBlock.SNOWBALLS.getPossibleValues(), size -> LootItem.lootTableItem(Items.SNOWBALL)
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SnowballingBlocks.SNOWBALL_PILE.get())
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(SnowPileBlock.SNOWBALLS, size)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(size + 1))))
                    .when(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(SnowPileBlock.SNOWBALLS.getPossibleValues(), size -> size == 1 ? snowballPileDropForSize(size) : snowballPileDropForSize(size).apply(SetNbtFunction.setTag(snowballPileNBT(size)))))));

        biConsumer.accept(SnowballingBlocks.SNOWBALL_PILE.get().getLootTable(), snowballPileLootTable);
    }

    private LootPoolSingletonContainer.Builder<?> snowballPileDropForSize(int size) {
        return LootItem.lootTableItem(SnowballingBlocks.SNOWBALL_PILE.get())
            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SnowballingBlocks.SNOWBALL_PILE.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                    .hasProperty(SnowPileBlock.SNOWBALLS, size)));
    }

    private CompoundTag snowballPileNBT(int size) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", size);
        return tag;
    }
}
