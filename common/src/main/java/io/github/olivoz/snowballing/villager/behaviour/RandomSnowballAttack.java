package io.github.olivoz.snowballing.villager.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RandomSnowballAttack extends Behavior<Villager> {

    private static final Random RANDOM = new Random();

    private int cooldown;

    public RandomSnowballAttack() {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        cooldown = RANDOM.nextInt(EndSnowballFight.REWARD_COOLDOWN);
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        if(--cooldown > 0) return;
        NearestVisibleLivingEntities nearestVisibleLivingEntities = villager.getBrain()
            .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .orElseThrow();

        Optional<LivingEntity> closest = nearestVisibleLivingEntities.findClosest(livingEntity -> livingEntity instanceof Villager || livingEntity instanceof Player);
        if(closest.isEmpty()) return;
        LivingEntity target = closest.orElseThrow();

        if(villager.distanceToSqr(target) > 8 * 8) {
            BehaviorUtils.setWalkAndLookTargetMemories(villager, target, 0.75F, 8);
            return;
        }

        SnowballAttack.throwSnowball(villager, target);
        cooldown = EndSnowballFight.REWARD_COOLDOWN;
    }
}
