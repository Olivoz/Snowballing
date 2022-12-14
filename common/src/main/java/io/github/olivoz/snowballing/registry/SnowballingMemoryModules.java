package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.function.Supplier;

public final class SnowballingMemoryModules {

    private SnowballingMemoryModules() {
    }

    public static final Supplier<MemoryModuleType<BlockPos>> SNOW_AT = RegistryManager.registerMemoryModuleType("snow_at");
    public static final Supplier<MemoryModuleType<LivingEntity>> SNOWBALL_FIGHT_ENEMY = RegistryManager.registerMemoryModuleType("snowball_fight_enemy");
    public static final Supplier<MemoryModuleType<Long>> LAST_ATTACKED_BY_SNOWBALL = RegistryManager.registerMemoryModuleType("last_attacked_by_snowball");

    public static void init() {
    }
}
