package io.github.olivoz.snowballing.listeners;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@UtilityClass
public final class SnowballingInteractionListener {

    public static boolean listen(Player player, Level level, BlockPos blockPos, ItemStack itemInHand, SnowPileBlock snowballPileBlock) {
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(Blocks.SNOW) && !blockState.is(snowballPileBlock)) return false;

        Item itemInHandType = itemInHand.getItem();
        if(!(itemInHandType == Items.SNOWBALL && player.isCrouching()) && !(itemInHandType instanceof ShovelItem))
            return false;

        if(itemInHandType == Items.SNOWBALL) {
            if(!player.isCrouching()) return false;
            if(!player.getAbilities().instabuild) itemInHand.shrink(1);

            if(blockState.is(snowballPileBlock)) {
                SnowPileBlock.addSnowball(level, blockPos, blockState);
                return true;
            }
        } else if(blockState.is(Blocks.SNOW)) {
            itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        level.setBlockAndUpdate(blockPos, snowballPileBlock.defaultBlockState());
        level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);

        return true;
    }

}
