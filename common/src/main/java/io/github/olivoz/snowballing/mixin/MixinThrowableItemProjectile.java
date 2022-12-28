package io.github.olivoz.snowballing.mixin;

import io.github.olivoz.snowballing.extend.SlingShotSnowball;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableItemProjectile.class)
public final class MixinThrowableItemProjectile {

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void snowballingMixinAddAdditionalSaveData(final CompoundTag compoundTag, final CallbackInfo ci) {
        if(!((Object) this instanceof SlingShotSnowball slingShotSnowball)) return;
        compoundTag.putFloat("Charge", slingShotSnowball.getCharge());
        compoundTag.putBoolean("IsSlingShot", slingShotSnowball.isSlingShot());
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void snowballingMixinReadAdditionalSaveData(final CompoundTag compoundTag, final CallbackInfo ci) {
        if(!((Object) this instanceof SlingShotSnowball slingShotSnowball)) return;
        slingShotSnowball.setCharge(compoundTag.getFloat("Charge"));
        slingShotSnowball.setSlingShot(compoundTag.getBoolean("IsSlingShot"));
    }

}
