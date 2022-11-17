package io.github.olivoz.snowballing.listeners;

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

import java.util.function.Supplier;

@UtilityClass
public final class SnowballingInteractionListener {

    public static boolean listen(Player player, Level level, BlockPos blockPos, ItemStack itemInHand, Supplier<BlockState> newBlockState) {
        Item itemInHandType = itemInHand.getItem();
        if(itemInHandType != Items.SNOWBALL && !(itemInHandType instanceof ShovelItem)) return false;

        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(Blocks.SNOW)) return false;

        if(!level.isClientSide) {
            if(!player.getAbilities().instabuild) {
                if(itemInHand.getMaxDamage() > 0) {
                    itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                } else {
                    itemInHand.shrink(1);
                }
            }

            level.setBlockAndUpdate(blockPos, newBlockState.get());
            level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        return true;
    }

}
