package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import io.github.olivoz.snowballing.registry.SnowballingPOI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public class SetSnowAsWalkTarget extends Behavior<Villager> {

    private static final int COOLDOWN = 20;
    private final float speedModifier;
    private long lastUpdate;

    public SetSnowAsWalkTarget(float speedModifier) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        if(serverLevel.getGameTime() - lastUpdate < COOLDOWN) return false;

        return villager.getInventory()
            .countItem(Items.SNOWBALL) < Items.SNOWBALL.getMaxStackSize();
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        this.lastUpdate = currentTick;

        Optional<BlockPos> optionalSnowPos = serverLevel.getPoiManager()
            .findClosest((holder -> holder.value() == SnowballingPOI.SNOWBALL_PILE.get()), villager.blockPosition(), 32, PoiManager.Occupancy.HAS_SPACE);

        if(optionalSnowPos.isEmpty()) return;

        Brain<Villager> brain = villager.getBrain();
        brain.setMemory(SnowballingMemoryModules.SNOW_AT.get(), optionalSnowPos.orElseThrow());
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(optionalSnowPos.orElseThrow(), speedModifier, 1));
    }
}
