package io.github.olivoz.snowballing.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.olivoz.snowballing.villager.behaviour.FindLocationAndMakeSnowballPile;
import io.github.olivoz.snowballing.villager.behaviour.RandomSnowballAttack;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerGoalPackages.class)
public final class MixinVillagerGoalPackages {

    private MixinVillagerGoalPackages() {
    }

    @Inject(at = @At("RETURN"), method = "getIdlePackage(Lnet/minecraft/world/entity/npc/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;", cancellable = true)
    private static void snowballingMixinGetIdlePackage(final VillagerProfession villagerProfession, final float f, final CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>> cir) {
        cir.setReturnValue(ImmutableList.<Pair<Integer, ? extends Behavior<? super Villager>>>builder()
            .addAll(cir.getReturnValue())
            .add(Pair.of(2, new FindLocationAndMakeSnowballPile(0.5F)))
            .add(Pair.of(0, new RandomSnowballAttack()))
            .build());
    }

    @Inject(at = @At("RETURN"), method = "getWorkPackage(Lnet/minecraft/world/entity/npc/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;", cancellable = true)
    private static void snowballingMixinGetWorkPackage(final VillagerProfession villagerProfession, final float f, final CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>>> cir) {
        if(villagerProfession != VillagerProfession.NONE) return;

        cir.setReturnValue(ImmutableList.<Pair<Integer, ? extends Behavior<? super Villager>>>builder()
            .addAll(cir.getReturnValue())
            .add(Pair.of(2, new FindLocationAndMakeSnowballPile(0.5F)))
            .build());
    }

}
