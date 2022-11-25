package io.github.olivoz.snowballing.listener;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

@UtilityClass
public final class SnowballingInteractionListener {

    public static InteractionResult listen(Player player, Level level, BlockPos blockPos, ItemStack itemInHand, Block snowballPileBlock) {
        BlockState blockState = level.getBlockState(blockPos);
        if(!(blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) && !blockState.is(snowballPileBlock)) return InteractionResult.PASS;

        Item itemInHandType = itemInHand.getItem();
        if(itemInHandType != Items.SNOWBALL && !(itemInHandType instanceof ShovelItem)) return InteractionResult.PASS;

        if(itemInHandType == Items.SNOWBALL) {
            if(blockState.is(snowballPileBlock)) {
                if(player.isCrouching()) {
                    int size = blockState.getValue(SnowPileBlock.SNOWBALLS);
                    if(size == SnowPileBlock.MAX_SIZE) return InteractionResult.FAIL;

                    CompoundTag tag = itemInHand.getTag();
                    int amount = tag == null ? 1 : tag.getInt("size");

                    SnowPileBlock.addSnowball(level, blockPos, blockState, amount);
                    if(!player.getAbilities().instabuild) itemInHand.shrink(1);
                    level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    if(player.getAbilities().instabuild) {
                        if(!level.isClientSide) SnowPileBlock.removeSnowball(level, blockPos, blockState, 1);

                        return InteractionResult.sidedSuccess(level.isClientSide);
                    } else if(itemInHand.getMaxStackSize() > itemInHand.getCount()) {
                        if(!level.isClientSide) {
                            SnowPileBlock.removeSnowball(level, blockPos, blockState, 1);
                            itemInHand.grow(1);
                        }

                        return InteractionResult.sidedSuccess(level.isClientSide);
                    } else {
                        return InteractionResult.FAIL;
                    }

                }
            } else if(player.isCrouching()) {
                level.setBlockAndUpdate(blockPos, snowballPileBlock.defaultBlockState());
                level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if(!player.getAbilities().instabuild) itemInHand.shrink(1);

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }

        if(blockState.is(Blocks.SNOW)) {
            itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            level.setBlockAndUpdate(blockPos, snowballPileBlock.defaultBlockState());
            level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        int size = blockState.getValue(SnowPileBlock.SNOWBALLS);
        if(size == SnowPileBlock.MAX_SIZE) return InteractionResult.FAIL;

        itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        SnowPileBlock.addSnowball(level, blockPos, blockState, 1);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}
