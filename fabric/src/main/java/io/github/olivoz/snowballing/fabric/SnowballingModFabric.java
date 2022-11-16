package io.github.olivoz.snowballing.fabric;

import io.github.olivoz.snowballing.fabriclike.SnowballingModFabricLike;
import net.fabricmc.api.ModInitializer;

public class SnowballingModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SnowballingModFabricLike.init();
    }
}
