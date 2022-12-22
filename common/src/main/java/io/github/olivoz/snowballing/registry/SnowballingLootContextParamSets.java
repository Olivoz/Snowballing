package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class SnowballingLootContextParamSets {

    private SnowballingLootContextParamSets() {
    }

    public static final LootContextParamSet SNOWBALL_FIGHT = RegistryManager.registerLootContextParamSet("snowball_fight", new LootContextParamSet.Builder().required(LootContextParams.THIS_ENTITY)
        .required(LootContextParams.ORIGIN)
        .required(SnowballingLootContextParams.LAST_HIT_BY_SNOWBALL)
        .required(SnowballingLootContextParams.SNOWBALL_FIGHT_POINTS)
        .optional(SnowballingLootContextParams.SNOWBALL_FIGHT_ENEMY)
        .build());

    public static void init() {

    }

}
