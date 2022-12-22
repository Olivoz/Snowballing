package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import net.minecraft.world.level.GameRules;

public final class SnowballingGameRules {

    private SnowballingGameRules() {
    }

    public static final GameRules.Key<GameRules.BooleanValue> SNOWBALL_PILES_MELT = RegistryManager.registerGameRule("snowballPilesMelt", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));

    public static void init() {

    }

}
