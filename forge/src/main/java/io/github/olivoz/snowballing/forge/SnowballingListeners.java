package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import io.github.olivoz.snowballing.listener.SnowballingHitListener;
import io.github.olivoz.snowballing.listener.SnowballingInteractionListener;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import lombok.experimental.UtilityClass;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@UtilityClass
public final class SnowballingListeners {

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

    @SubscribeEvent
    public static void onHit(ProjectileImpactEvent event) {
        if(!(event.getProjectile() instanceof Snowball snowball)) return;
        if(!(event.getRayTraceResult() instanceof EntityHitResult hitResult)) return;
        if(!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        SnowballingHitListener.listen(snowball, livingEntity);
    }

}
