package io.github.olivoz.snowballing.mixin;

import io.github.olivoz.snowballing.extend.EvilSnowballReferenceHack;
import io.github.olivoz.snowballing.extend.PointTracker;
import io.github.olivoz.snowballing.extend.SlingShotSnowball;
import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingEffects;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import io.github.olivoz.snowballing.villager.behaviour.EndSnowballFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class MixinSnowball extends ThrowableItemProjectile implements SlingShotSnowball {

    private boolean isSlingShot = false;
    private float charge = 0.0F;

    private MixinSnowball(final EntityType<? extends ThrowableItemProjectile> entityType, final Level level) {
        super(entityType, level);
    }

    private static void snowballingHandleSnowballHit(Snowball snowball, LivingEntity livingEntity) {
        if(!(snowball.getOwner() instanceof LivingEntity shooter)) return;

        if(livingEntity instanceof PointTracker pointTracker) {
            if(pointTracker.getEnemy() != shooter) pointTracker.setPoints(0);
            else pointTracker.setPoints(pointTracker.getPoints() + 1);
        }

        if(shooter instanceof PointTracker pointTracker && livingEntity instanceof Player) {
            pointTracker.setPoints(pointTracker.getPoints() - 1);
        }

        if(livingEntity instanceof Villager villager) {
            ((EvilSnowballReferenceHack) villager).setLastHitBySnowball(snowball);

            Brain<Villager> brain = villager.getBrain();

            brain.setMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get(), villager.level.getGameTime());
            brain.setMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get(), shooter);

            if(!brain.isActive(SnowballingActivities.SNOWBALL_FIGHT.get()) && !EndSnowballFight.shouldEnd(villager)) {
                brain.eraseMemory(MemoryModuleType.PATH);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
                brain.eraseMemory(MemoryModuleType.BREED_TARGET);
                brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);

                brain.setActiveActivityIfPossible(SnowballingActivities.SNOWBALL_FIGHT.get());
            }
        }

        if(livingEntity instanceof IronGolem && shooter instanceof Villager) {
            livingEntity.setLastHurtByMob(null);
        }

        if(livingEntity instanceof Player player && player.canFreeze()) {
            MobEffectInstance activeSnowballedEffect = player.getEffect(SnowballingEffects.SNOWBALLED.get());
            int activeAmplifier = activeSnowballedEffect == null ? 0 : activeSnowballedEffect.getAmplifier();

            RandomSource random = player.getRandom();
            if(activeAmplifier < 5 && random.nextInt(100) < 25) activeAmplifier++;
            player.addEffect(new MobEffectInstance(SnowballingEffects.SNOWBALLED.get(), 10 * 20 + random.nextInt(20 << activeAmplifier), activeAmplifier));
        }

        if((snowball instanceof SlingShotSnowball slingShotSnowball && slingShotSnowball.isSlingShot()) && slingShotSnowball.getCharge() > 0.1F) {
            double knockbackResistance = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

            Vec3 vec3 = snowball.getDeltaMovement()
                .multiply(1.0, 0.0, 1.0)
                .normalize()
                .scale(((SlingShotSnowball) snowball).getCharge() * knockbackResistance);

            if(vec3.lengthSqr() > 0.0) {
                livingEntity.push(vec3.x, 0.1, vec3.z);
            }
        }

    }

    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.charge = compoundTag.getFloat("Charge");
        this.isSlingShot = compoundTag.getBoolean("isSlingShot");
    }

    @Inject(at = @At(value = "HEAD"), method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V")
    private void snowballingMixinOnEntityHitHead(final EntityHitResult hitResult, final CallbackInfo ci) {
        if(!(hitResult.getEntity() instanceof Villager villager)) return;
        ((EvilSnowballReferenceHack) villager).setLastHitBySnowball((Snowball) ((Object) this));
    }

    @Inject(at = @At(value = "TAIL"), method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V")
    private void snowballingMixinOnEntityHitTail(final EntityHitResult hitResult, final CallbackInfo ci) {
        if(!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        snowballingHandleSnowballHit((Snowball) ((Object) this), livingEntity);
    }

    @Override
    public float getCharge() {
        return charge;
    }

    @Override
    public void setCharge(final float charge) {
        this.charge = charge;
    }

    @Override
    public boolean isSlingShot() {
        return isSlingShot;
    }

    @Override
    public void setSlingShot(final boolean slingShot) {
        this.isSlingShot = slingShot;
    }
}
