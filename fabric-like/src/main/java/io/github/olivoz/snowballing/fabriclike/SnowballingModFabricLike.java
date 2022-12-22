package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.client.SnowballingItemProperties;

public final class SnowballingModFabricLike {

    private SnowballingModFabricLike() {
    }

    public static void init() {
        SnowballingMod.init();
        SnowballingListeners.init();
        SnowballingItemProperties.register();
    }
}
