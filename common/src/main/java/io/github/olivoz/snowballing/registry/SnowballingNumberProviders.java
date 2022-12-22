package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.data.SnowballingPointsValue;
import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

import java.util.function.Supplier;

public class SnowballingNumberProviders {

    public static final Supplier<LootNumberProviderType> SNOWBALL_FIGHT_POINTS = RegistryManager.registerLootNumberProviderType("snowball_fight_points", () -> new LootNumberProviderType(new SnowballingPointsValue.Serializer()));

    public static void init() {

    }

}
