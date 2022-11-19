package io.github.olivoz.snowballing.listeners;

import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

@UtilityClass
public final class SnowballingHitListener {

    public static void listen(Projectile projectile, LivingEntity livingEntity) {
        if(livingEntity.canFreeze()) livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + 20);
    }

}
