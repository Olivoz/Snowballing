package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.listener.SnowballingInteractionListener;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class SnowballingListeners {

    private SnowballingListeners() {
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if(event.getUseItem() == Event.Result.DENY) return;
        Level level = event.getLevel();
        InteractionResult result = SnowballingInteractionListener.listen(event.getEntity(), level, event.getPos(), event.getItemStack(), SnowballingBlocks.SNOWBALL_PILE.get());
        if(result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

}
