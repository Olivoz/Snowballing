package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.client.SnowballingItemProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SnowballingModFabricLike {

    public static void init() {
        SnowballingMod.init();
        SnowballingListeners.init();
        SnowballingItemProperties.register();
    }
}
