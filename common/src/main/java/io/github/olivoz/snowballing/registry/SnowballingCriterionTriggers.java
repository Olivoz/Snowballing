package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.advancement.criterion.KilledByFallDamageTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public final class SnowballingCriterionTriggers {

    public static final KilledByFallDamageTrigger KILLED_BY_FALL_DAMAGE_TRIGGER = CriteriaTriggers.register(new KilledByFallDamageTrigger());

    private SnowballingCriterionTriggers() {
    }

    public static void init() {

    }

}
