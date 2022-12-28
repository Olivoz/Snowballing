package io.github.olivoz.snowballing.mixin;

import io.github.olivoz.snowballing.registry.SnowballingCriterionTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends LivingEntity {

    protected MixinServerPlayer(final EntityType<? extends LivingEntity> entityType, final Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/KilledTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"), method = "awardKillScore")
    private void snowballingMixinDie(final Entity entity, final int i, final DamageSource damageSource, final CallbackInfo ci) {
        if(damageSource != DamageSource.FALL) return;
        SnowballingCriterionTriggers.KILLED_BY_FALL_DAMAGE_TRIGGER.trigger(((ServerPlayer) (Object) this), entity);
    }

}
