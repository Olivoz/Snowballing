package io.github.olivoz.snowballing.manager;

import io.github.olivoz.snowballing.SnowballingMod;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@UtilityClass
public final class RegistryManager {

    public static Supplier<Block> registerBlock(final String id, final Supplier<Block> blockSupplier) {
        Block registeredBlock = Registry.register(Registry.BLOCK, new ResourceLocation(SnowballingMod.MOD_ID, id), blockSupplier.get());
        return () -> registeredBlock;
    }

    public static CreativeModeTab registerTab(final String id) {
        return FabricItemGroupBuilder.build(new ResourceLocation(SnowballingMod.MOD_ID, id), Items.SNOWBALL::getDefaultInstance);
    }

    public static Supplier<Item> registerItem(final String id, final Supplier<Item> itemSupplier) {
        Item registeredItem = Registry.register(Registry.ITEM, new ResourceLocation(SnowballingMod.MOD_ID, id), itemSupplier.get());
        return () -> registeredItem;
    }

    public static Supplier<Activity> registerActivity(final String id) {
        Activity registeredActivity = Activity.register(SnowballingMod.MOD_ID + ":" + id);
        return () -> registeredActivity;
    }

    public static <T> Supplier<MemoryModuleType<T>> registerMemoryModuleType(final String id) {
        MemoryModuleType<T> registeredMemoryModuleType = MemoryModuleType.register(SnowballingMod.MOD_ID + ":" + id);
        return () -> registeredMemoryModuleType;
    }

    public static Supplier<PoiType> registerPOI(final String id, int ticketCount, int searchDistance, final Supplier<Block[]> blocks) {
        PoiType registeredPoiType = PointOfInterestHelper.register(new ResourceLocation(SnowballingMod.MOD_ID, id), ticketCount, searchDistance, blocks.get());
        return () -> registeredPoiType;
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(final String id, GameRules.Category category, GameRules.Type<T> type) {
        return GameRuleRegistry.register(id, category, type);
    }

}