package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;

public class TakeOrMakeSnowballs extends Behavior<Villager> {

    private static final int TIME_PER_SNOWBALL = 20;
    private static final float REACH = 1.73F;
    private static final int COOLDOWN = 20;

    public TakeOrMakeSnowballs() {
        super(Map.of(SnowballingMemoryModules.SNOW_AT.get(), MemoryStatus.VALUE_PRESENT), TIME_PER_SNOWBALL * Items.SNOWBALL.getMaxStackSize());
    }

    public static Optional<BlockPos> getKnownSnowPos(Villager villager) {
        return villager.getBrain()
            .getMemory(SnowballingMemoryModules.SNOW_AT.get());
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        if(villager.getInventory()
            .countItem(Items.SNOWBALL) >= Items.SNOWBALL.getMaxStackSize()) return false;

        Optional<BlockPos> knownSnowPos = getKnownSnowPos(villager);

        return knownSnowPos.isPresent() && knownSnowPos.get()
            .distSqr(villager.blockPosition()) <= REACH;
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        Optional<BlockPos> knownSnowPos = getKnownSnowPos(villager);

        villager.getBrain()
            .setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(knownSnowPos.orElseThrow()));

        SimpleContainer inventory = villager.getInventory();
        ItemStack snowballItemStack = Items.SNOWBALL.getDefaultInstance();
        int added = snowballItemStack.getCount() - inventory.addItem(snowballItemStack)
            .getCount();

        BlockState blockState = serverLevel.getBlockState(knownSnowPos.orElseThrow());
        if(!blockState.is(SnowballingBlocks.SNOWBALL_PILE.get())) return;
        SnowballPileBlock.removeSnowball(serverLevel, knownSnowPos.orElseThrow(), blockState, added);
    }

    @Override
    protected void stop(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        Brain<Villager> brain = villager.getBrain();

        Optional<LivingEntity> optionalEnemy = brain.getMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get());

        if(optionalEnemy.isPresent()) {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(optionalEnemy.orElseThrow(), true));
        } else {
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        }

        if(villager.getInventory()
            .countItem(Items.SNOWBALL) >= Items.SNOWBALL.getMaxStackSize()) brain.eraseMemory(SnowballingMemoryModules.SNOW_AT.get());
    }
}
