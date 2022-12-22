package io.github.olivoz.snowballing;

import io.github.olivoz.snowballing.registry.SnowballingActivities;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingEffects;
import io.github.olivoz.snowballing.registry.SnowballingGameRules;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import io.github.olivoz.snowballing.registry.SnowballingLootContextParamSets;
import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import io.github.olivoz.snowballing.registry.SnowballingNumberProviders;
import io.github.olivoz.snowballing.registry.SnowballingPOI;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SnowballingMod {

    public static final String MOD_ID = "snowballing";

    public static void init() {
        SnowballingBlocks.init();
        SnowballingItems.init();
        SnowballingGameRules.init();
        SnowballingMemoryModules.init();
        SnowballingActivities.init();
        SnowballingPOI.init();
        SnowballingEffects.init();
        SnowballingLootContextParamSets.init();
        SnowballingNumberProviders.init();
    }
}
