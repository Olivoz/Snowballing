package io.github.olivoz.snowballing.manager;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

import java.util.function.Supplier;

public final class RegistryManager {

    private RegistryManager() {
    }

    public static Supplier<Block> registerBlock(final String id, final Supplier<Block> blockSupplier) {
        throw new AssertionError();
    }

    public static CreativeModeTab registerTab(final String id) {
        throw new AssertionError();
    }

    public static Supplier<Item> registerItem(final String id, final Supplier<Item> itemSupplier) {
        throw new AssertionError();
    }

    public static Supplier<Activity> registerActivity(final String id) {
        throw new AssertionError();
    }

    public static <T> Supplier<MemoryModuleType<T>> registerMemoryModuleType(final String id) {
        throw new AssertionError();
    }

    public static Supplier<PoiType> registerPOI(final String id, int ticketCount, int searchDistance, final Supplier<Block[]> blocks) {
        throw new AssertionError();
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(final String id, GameRules.Category category, GameRules.Type<T> type) {
        throw new AssertionError();
    }

    public static Supplier<MobEffect> registerEffect(final String id, final Supplier<MobEffect> mobEffectSupplier) {
        throw new AssertionError();
    }

    public static Supplier<LootNumberProviderType> registerLootNumberProviderType(final String id, final Supplier<LootNumberProviderType> type) {
        throw new AssertionError();
    }

    public static LootContextParamSet registerLootContextParamSet(final String id, LootContextParamSet set) {
        throw new AssertionError();
    }
}
