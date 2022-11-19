package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import io.github.olivoz.snowballing.listeners.SnowballingInteractionListener;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@UtilityClass
public final class SnowballingListeners {

    public static InteractionResult onInteract(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        return SnowballingInteractionListener.listen(player, level, hitResult.getBlockPos(), player.getItemInHand(hand), (SnowPileBlock) SnowballingBlocks.SNOWBALL_PILE);
    }

    public static void init() {
        UseBlockCallback.EVENT.register(SnowballingListeners::onInteract);
    }

}
