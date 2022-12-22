package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public final class SnowballingLootContextParams {

    private SnowballingLootContextParams() {
    }

    public static final LootContextParam<LivingEntity> SNOWBALL_FIGHT_ENEMY = of("snowball_fight_enemy");
    public static final LootContextParam<Snowball> LAST_HIT_BY_SNOWBALL = of("last_hit_by_snowball");
    public static final LootContextParam<Integer> SNOWBALL_FIGHT_POINTS = of("snowball_fight_points");

    private static <T> LootContextParam<T> of(String id) {
        return new LootContextParam<>(new ResourceLocation(SnowballingMod.MOD_ID, id));
    }

}
