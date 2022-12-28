package io.github.olivoz.snowballing.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.VillagerPanicTrigger;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerPanicTrigger.class)
public final class MixinVillagerPanicTrigger {

    private MixinVillagerPanicTrigger() {
    }

    @Inject(at = @At("HEAD"), method = "isHurt(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    private static void isHurt(final LivingEntity livingEntity, final CallbackInfoReturnable<Boolean> cir) {
        DamageSource lastDamageSource = livingEntity.lastDamageSource;
        if(lastDamageSource == null) return;
        if(!lastDamageSource.isProjectile()) return;
        if(lastDamageSource.getDirectEntity() instanceof Snowball) cir.setReturnValue(false);
    }

}