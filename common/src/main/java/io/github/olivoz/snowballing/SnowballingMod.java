package io.github.olivoz.snowballing;

import io.github.olivoz.snowballing.gamerule.SnowballingGameRules;

public final class SnowballingMod {

    public static final String MOD_ID = "snowballing";

    public static void init() {
        SnowballingGameRules.init();
    }
}
