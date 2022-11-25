package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Map;

public class EndSnowballFight extends Behavior<Villager> {

    public static final int SNOWBALL_FIGHT_TIMEOUT = 30 * 20;

    public static final int MAX_DISTANCE_FROM_ENEMY = 36;

    public EndSnowballFight() {
        super(Map.of());
    }

    private static boolean hasFreeTime(Villager villager) {
        Activity activityAt = villager.getBrain()
            .getSchedule()
            .getActivityAt((int) (villager.level.getDayTime() % 24000L));

        return activityAt == Activity.IDLE;
    }

    private static boolean isEnemyCloseEnoughForFight(Villager villager) {
        return villager.getBrain()
            .getMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get())
            .filter(livingEntity -> livingEntity.distanceToSqr(villager) <= MAX_DISTANCE_FROM_ENEMY * MAX_DISTANCE_FROM_ENEMY)
            .isPresent();
    }

    private static boolean isFightTimedOut(Villager villager) {
        return villager.getBrain()
            .getMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get())
            .filter(timestamp -> timestamp + SNOWBALL_FIGHT_TIMEOUT < villager.level.getGameTime())
            .isPresent();
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        return villager.isTrading() || !hasFreeTime(villager) || isFightTimedOut(villager) || !isEnemyCloseEnoughForFight(villager);
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTime) {
        Brain<Villager> brain = villager.getBrain();

        brain.eraseMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get());
        brain.eraseMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get());
        brain.updateActivityFromSchedule(serverLevel.getDayTime(), currentTime);
    }

}
