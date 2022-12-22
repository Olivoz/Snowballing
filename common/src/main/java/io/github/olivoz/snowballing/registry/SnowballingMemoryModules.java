package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.function.Supplier;

@UtilityClass
public final class SnowballingMemoryModules {

    public static final Supplier<MemoryModuleType<BlockPos>> SNOW_AT = RegistryManager.registerMemoryModuleType("snow_at");
    public static final Supplier<MemoryModuleType<LivingEntity>> SNOWBALL_FIGHT_ENEMY = RegistryManager.registerMemoryModuleType("snowball_fight_enemy");
    public static final Supplier<MemoryModuleType<Long>> LAST_ATTACKED_BY_SNOWBALL = RegistryManager.registerMemoryModuleType("last_attacked_by_snowball");
    public static final Supplier<MemoryModuleType<Long>> LAST_PRIZE_DROP = RegistryManager.registerMemoryModuleType("last_prize_drop");

    public static void init() {
    }
}
