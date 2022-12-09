package io.github.olivoz.snowballing.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.olivoz.snowballing.extend.EvilSnowballReferenceHack;
import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import io.github.olivoz.snowballing.villager.behaviour.EndSnowballFight;
import io.github.olivoz.snowballing.villager.behaviour.SetSnowAsWalkTarget;
import io.github.olivoz.snowballing.villager.behaviour.SnowballAttack;
import io.github.olivoz.snowballing.villager.behaviour.TakeOrMakeSnowballs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Villager.class)
public final class MixinVillager implements EvilSnowballReferenceHack {

    private @Nullable Snowball lastHitBySnowball = null;

    @Accessor("MEMORY_TYPES")
    private static ImmutableList<MemoryModuleType<?>> getMemoryTypes() {
        throw new AssertionError();
    }

    @Accessor("MEMORY_TYPES")
    private static void setMemoryTypes(ImmutableList<MemoryModuleType<?>> memoryTypes) {
        throw new AssertionError();
    }

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void snowballingMixinStaticInit(final CallbackInfo ci) {
        ImmutableList<MemoryModuleType<?>> newMemoryTypes = ImmutableList.<MemoryModuleType<?>>builder()
            .addAll(getMemoryTypes())
            .add(SnowballingMemoryModules.SNOW_AT.get())
            .add(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get())
            .add(SnowballingMemoryModules.LAST_ATTACKED_BY_SNOWBALL.get())
            .build();

        setMemoryTypes(newMemoryTypes);
    }

    @Inject(at = @At("HEAD"), method = "registerBrainGoals")
    public void snowballingMixinRegisterBrainGoals(final Brain<Villager> brain, final CallbackInfo ci) {
        float speed = 0.5F;
        brain.addActivity(SnowballingActivities.SNOWBALL_FIGHT.get(), ImmutableList.of(Pair.of(0, new GateBehavior<>(Map.of(), Set.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, List.of(Pair.of(new EndSnowballFight(), 0), Pair.of(new SnowballAttack(speed), 1), Pair.of(new TakeOrMakeSnowballs(), 1), Pair.of(new SetSnowAsWalkTarget(speed), 1)))), VillagerGoalPackages.getMinimalLookBehavior()));
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    public void snowballingMixinMobInteract(final Player player, final InteractionHand interactionHand, final CallbackInfoReturnable<InteractionResult> cir) {
        if(player.isHolding(Items.SNOWBALL) || player.isHolding(SnowballingItems.SNOW_SLING.get())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "setLastHurtByMob(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
    private void snowballingMixinSetLastHurtByMob(final LivingEntity livingEntity, final CallbackInfo ci) {
        if(lastHitBySnowball == null) return;
        lastHitBySnowball = null;
        ci.cancel();
    }

    @Override
    public @Nullable Snowball getLastHitBySnowball() {
        return lastHitBySnowball;
    }

    @Override
    public void setLastHitBySnowball(@Nullable final Snowball snowball) {
        this.lastHitBySnowball = snowball;
    }
}
