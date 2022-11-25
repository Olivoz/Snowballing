package io.github.olivoz.snowballing.manager;

import com.google.common.collect.ImmutableSet;
import io.github.olivoz.snowballing.SnowballingMod;
import lombok.experimental.UtilityClass;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public final class RegistryManager {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SnowballingMod.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SnowballingMod.MOD_ID);
    private static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, SnowballingMod.MOD_ID);
    private static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, SnowballingMod.MOD_ID);
    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, SnowballingMod.MOD_ID);

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

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        ACTIVITIES.register(eventBus);
        MEMORY_MODULE_TYPES.register(eventBus);
        POI_TYPES.register(eventBus);
    }

}
