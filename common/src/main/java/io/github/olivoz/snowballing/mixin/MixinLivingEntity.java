package io.github.olivoz.snowballing.mixin;

import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setLastHurtByMob(Lnet/minecraft/world/entity/LivingEntity;)V"), method = "hurt", cancellable = true)
    public void snowballingMixinHurt(final DamageSource damageSource, final float amount, final CallbackInfoReturnable<Boolean> cir) {
        if(((Object) this) instanceof Villager villager && damageSource.getDirectEntity() instanceof Snowball snowball && damageSource.getEntity() instanceof LivingEntity shooter) {
            Brain<Villager> brain = villager.getBrain();
            Activity activityAt = villager.getBrain()
                .getSchedule()
                .getActivityAt((int) (villager.level.getDayTime() % 24000L));

            VillagerProfession profession = villager.getVillagerData()
                .getProfession();

            if((profession.heldJobSite() == PoiType.NONE || activityAt == Activity.IDLE) && !brain.isActive(SnowballingActivities.SNOWBALL_FIGHT.get())) {
                brain.eraseMemory(MemoryModuleType.PATH);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
                brain.eraseMemory(MemoryModuleType.BREED_TARGET);
                brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);

                brain.setActiveActivityIfPossible(SnowballingActivities.SNOWBALL_FIGHT.get());
            }

            brain.setMemory(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get(), villager.level.getGameTime());
            brain.setMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get(), shooter);

            cir.setReturnValue(true);
        }

    }

}
