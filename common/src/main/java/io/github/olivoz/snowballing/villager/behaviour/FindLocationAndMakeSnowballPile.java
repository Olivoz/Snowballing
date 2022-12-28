package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingPOI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;

public class FindLocationAndMakeSnowballPile extends Behavior<Villager> {

    private static final float REACH = 1.73F * 1.73F;
    private final float speed;

    public FindLocationAndMakeSnowballPile(final float speed) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speed = speed;
    }

    private static boolean validSnowballPileLocation(final ServerLevel serverLevel, final BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        return (blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) || serverLevel.getBlockState(blockPos.above())
            .isAir();
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        return !villager.getBrain()
            .isActive(SnowballingActivities.SNOWBALL_FIGHT.get()) && serverLevel.getPoiManager()
            .getInRange(holder -> holder.value() == SnowballingPOI.SNOWBALL_PILE.get(), villager.blockPosition(), 32, PoiManager.Occupancy.ANY)
            .count() < 5;
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        Optional<BlockPos> optionalClosestSnow = serverLevel.getPoiManager()
            .findClosest(holder -> holder.value() == SnowballingPOI.SNOW.get(), blockPos -> validSnowballPileLocation(serverLevel, blockPos), villager.blockPosition(), 32, PoiManager.Occupancy.HAS_SPACE);

        if(optionalClosestSnow.isEmpty()) return;
        BlockPos blockPos = optionalClosestSnow.orElseThrow();

        if(blockPos.distSqr(villager.blockPosition()) < REACH) {
            BlockState blockState = serverLevel.getBlockState(blockPos);
            BlockPos targetBlockPos = blockState.is(Blocks.SNOW) ? blockPos : blockPos.above();

            BlockState newState = SnowballingBlocks.SNOWBALL_PILE.get()
                .defaultBlockState()
                .setValue(SnowballPileBlock.SNOWBALLS, SnowballPileBlock.MAX_SIZE);

            serverLevel.setBlockAndUpdate(targetBlockPos, newState);
            return;
        }

        BehaviorUtils.setWalkAndLookTargetMemories(villager, blockPos, speed, 1);
    }
}
