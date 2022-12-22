package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.effect.SnowballedEffect;
import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public final class SnowballingEffects {

    private SnowballingEffects() {
    }

    public static final Supplier<MobEffect> SNOWBALLED = RegistryManager.registerEffect("snowballed", SnowballedEffect::new);

    public static void init() {

    }

}
