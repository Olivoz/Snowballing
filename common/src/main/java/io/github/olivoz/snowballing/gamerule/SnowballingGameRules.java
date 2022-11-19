package io.github.olivoz.snowballing.gamerule;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.GameRules;

@UtilityClass
public final class SnowballingGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> SNOWBALL_PILES_MELT = GameRules.register("snowballPilesMelt", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));

    public static void init() {

    }

}
