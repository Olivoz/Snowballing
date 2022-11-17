package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.listeners.SnowballingInteractionListener;
import lombok.experimental.UtilityClass;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@UtilityClass
public final class SnowballingListeners {

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if(event.getUseItem() == Event.Result.DENY) return;
        Level level = event.getLevel();
        boolean handled = SnowballingInteractionListener.listen(event.getEntity(), level, event.getPos(), event.getItemStack(), SnowballingBlocks.SNOWBALL_PILE.get()::defaultBlockState);
        if(handled) event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }

}
