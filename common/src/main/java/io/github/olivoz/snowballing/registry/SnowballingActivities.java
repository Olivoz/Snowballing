package io.github.olivoz.snowballing.registry;

import io.github.olivoz.snowballing.manager.RegistryManager;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.schedule.Activity;

import java.util.function.Supplier;

@UtilityClass
public final class SnowballingActivities {

    public static final Supplier<Activity> SNOWBALL_FIGHT = RegistryManager.registerActivity("snowball_fight");

    public static void init() {
    }

}
