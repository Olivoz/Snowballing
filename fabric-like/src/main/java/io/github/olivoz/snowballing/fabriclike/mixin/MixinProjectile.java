package io.github.olivoz.snowballing.fabriclike.mixin;

import io.github.olivoz.snowballing.listeners.SnowballingHitListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public final class MixinProjectile {

    @Inject(at = @At("HEAD"), method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V")
    private void snowballingOnEntityHit(final EntityHitResult hitResult, final CallbackInfo ci) {
        if(!((Object) this instanceof Snowball snowball)) return;
        if(!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        SnowballingHitListener.listen(snowball, livingEntity);
    }

}
