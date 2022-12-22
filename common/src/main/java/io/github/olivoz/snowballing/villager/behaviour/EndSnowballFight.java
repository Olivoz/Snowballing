package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.extend.EvilSnowballReferenceHack;
import io.github.olivoz.snowballing.extend.PointTracker;
import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingLootContextParamSets;
import io.github.olivoz.snowballing.registry.SnowballingLootContextParams;
import io.github.olivoz.snowballing.registry.SnowballingLootTables;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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

        return activityAt == Activity.IDLE || activityAt == Activity.MEET || (villager.getVillagerData()
            .getProfession()
            .heldJobSite() == PoiType.NONE && activityAt == Activity.WORK);
    }

    private static boolean isEnemyCloseEnoughForFight(Villager villager) {
        return villager.getBrain()
            .getMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get())
            .filter(livingEntity -> !livingEntity.isSpectator() && livingEntity.distanceToSqr(villager) <= MAX_DISTANCE_FROM_ENEMY * MAX_DISTANCE_FROM_ENEMY)
            .isPresent();
    }

    private static boolean isFightTimedOut(Villager villager) {
        return villager.getBrain()
            .getMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get())
            .filter(timestamp -> timestamp + SNOWBALL_FIGHT_TIMEOUT < villager.level.getGameTime())
            .isPresent();
    }

    private static boolean shouldSurrender(Villager villager) {
        if(!(villager instanceof PointTracker pointTracker)) return false;
        return pointTracker.getPoints() > 20;
    }

    private static boolean shouldWin(Villager villager) {
        if(!(villager instanceof PointTracker pointTracker)) return false;
        return pointTracker.getPoints() < -10;
    }

    public static boolean shouldEnd(final Villager villager) {
        return villager.isTrading() || !hasFreeTime(villager) || isFightTimedOut(villager) || !isEnemyCloseEnoughForFight(villager) || shouldSurrender(villager) || shouldWin(villager);
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        return shouldEnd(villager) && villager.getBrain()
            .isActive(SnowballingActivities.SNOWBALL_FIGHT.get());
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTime) {
        Brain<Villager> brain = villager.getBrain();

        LivingEntity enemy = brain.getMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get())
            .orElse(null);

        if(enemy instanceof Player player) {
            if(!brain.hasMemoryValue(SnowballingMemoryModules.LAST_PRIZE_DROP.get()) || brain.getMemory(SnowballingMemoryModules.LAST_PRIZE_DROP.get())
                .orElseThrow() + 20 * 60 * 20 < currentTime) {

                brain.setMemory(SnowballingMemoryModules.LAST_PRIZE_DROP.get(), currentTime);

                LootContext.Builder builder = new LootContext.Builder(serverLevel).withOptionalParameter(SnowballingLootContextParams.SNOWBALL_FIGHT_ENEMY, enemy)
                    .withRandom(villager.getRandom())
                    .withLuck(player.getLuck())
                    .withOptionalParameter(SnowballingLootContextParams.LAST_HIT_BY_SNOWBALL, ((EvilSnowballReferenceHack) villager).getLastHitBySnowball())
                    .withParameter(SnowballingLootContextParams.SNOWBALL_FIGHT_POINTS, ((PointTracker) villager).getPoints())
                    .withParameter(LootContextParams.THIS_ENTITY, villager)
                    .withParameter(LootContextParams.ORIGIN, villager.position());

                Vec3 pos = !enemy.isSpectator() && enemy.distanceToSqr(villager) < 5 * 5 ? enemy.position() : villager.position();

                serverLevel.getServer()
                    .getLootTables()
                    .get(SnowballingLootTables.SNOWBALL_FIGHT_END)
                    .getRandomItems(builder.create(SnowballingLootContextParamSets.SNOWBALL_FIGHT), itemStack -> BehaviorUtils.throwItem(villager, itemStack, pos));
            }
        }

        ((PointTracker) villager).setPoints(0);
        brain.eraseMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get());
        brain.eraseMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get());
        brain.updateActivityFromSchedule(serverLevel.getDayTime(), currentTime);
    }

}
