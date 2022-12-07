package io.github.olivoz.snowballing.listener;

import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Projectile;

@UtilityClass
public final class SnowballingHitListener {

    public static void listen(Projectile projectile, LivingEntity livingEntity) {
        if(livingEntity.canFreeze() && !(livingEntity instanceof Villager) && (livingEntity.getTicksFrozen()<200))
            livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + 60);
    }

}
