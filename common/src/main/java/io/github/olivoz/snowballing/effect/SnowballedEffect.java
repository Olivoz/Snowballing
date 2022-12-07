package io.github.olivoz.snowballing.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SnowballedEffect extends MobEffect {

    public SnowballedEffect() {
        super(MobEffectCategory.HARMFUL, 0xffffff);
    }

    @Override
    public void applyEffectTick(final LivingEntity livingEntity, final int amplifier) {
        if(!livingEntity.canFreeze()) return;
        int minFreeze = 20 << amplifier;
        if(livingEntity.getTicksFrozen() < minFreeze) livingEntity.setTicksFrozen(minFreeze);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return true;
    }
}
