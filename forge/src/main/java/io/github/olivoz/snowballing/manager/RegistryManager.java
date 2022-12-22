package io.github.olivoz.snowballing.manager;

import com.google.common.collect.ImmutableSet;
import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public final class RegistryManager {

    private RegistryManager() {
    }

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SnowballingMod.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SnowballingMod.MOD_ID);
    private static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, SnowballingMod.MOD_ID);
    private static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, SnowballingMod.MOD_ID);
    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, SnowballingMod.MOD_ID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SnowballingMod.MOD_ID);
    private static final DeferredRegister<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPES = DeferredRegister.create(Registry.LOOT_NUMBER_PROVIDER_REGISTRY, SnowballingMod.MOD_ID);

    public static Supplier<Block> registerBlock(final String id, final Supplier<Block> blockSupplier) {
        return BLOCKS.register(id, blockSupplier);
    }

    public static CreativeModeTab registerTab(final String id) {
        return new CreativeModeTab("snowballing." + id) {
            @Override
            public ItemStack makeIcon() {
                return Items.SNOWBALL.getDefaultInstance();
            }
        };
    }

    public static Supplier<Item> registerItem(final String id, final Supplier<Item> itemSupplier) {
        return ITEMS.register(id, itemSupplier);
    }

    public static Supplier<Activity> registerActivity(final String id) {
        return ACTIVITIES.register(id, () -> new Activity(id));
    }

    public static <T> Supplier<MemoryModuleType<T>> registerMemoryModuleType(final String id) {
        return MEMORY_MODULE_TYPES.register(id, () -> new MemoryModuleType<>(Optional.empty()));
    }

    public static Supplier<PoiType> registerPOI(final String id, int ticketCount, int searchDistance, final Supplier<Block[]> blocks) {
        return POI_TYPES.register(id, () -> {
            final ImmutableSet.Builder<BlockState> blockStateBuilder = ImmutableSet.builder();

            for(Block block : blocks.get()) {
                blockStateBuilder.addAll(block.getStateDefinition()
                    .getPossibleStates());
            }

            return new PoiType(blockStateBuilder.build(), ticketCount, searchDistance);
        });
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(final String id, GameRules.Category category, GameRules.Type<T> type) {
        return GameRules.register(id, category, type);
    }

    public static Supplier<MobEffect> registerEffect(final String id, final Supplier<MobEffect> mobEffectSupplier) {
        return MOB_EFFECTS.register(id, mobEffectSupplier);
    }

    public static Supplier<LootNumberProviderType> registerLootNumberProviderType(final String id, final Supplier<LootNumberProviderType> type) {
        return LOOT_NUMBER_PROVIDER_TYPES.register(id, type);
    }

    public static LootContextParamSet registerLootContextParamSet(final String id, LootContextParamSet set) {
        LootContextParamSets.REGISTRY.put(new ResourceLocation(SnowballingMod.MOD_ID, id), set);
        return set;
    }

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        ACTIVITIES.register(eventBus);
        MEMORY_MODULE_TYPES.register(eventBus);
        POI_TYPES.register(eventBus);
        MOB_EFFECTS.register(eventBus);
        LOOT_NUMBER_PROVIDER_TYPES.register(eventBus);
    }

}
