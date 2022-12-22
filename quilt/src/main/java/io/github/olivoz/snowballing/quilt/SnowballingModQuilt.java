package io.github.olivoz.snowballing.quilt;

import io.github.olivoz.snowballing.fabriclike.SnowballingModFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class SnowballingModQuilt implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        SnowballingModFabricLike.init();
    }
}
