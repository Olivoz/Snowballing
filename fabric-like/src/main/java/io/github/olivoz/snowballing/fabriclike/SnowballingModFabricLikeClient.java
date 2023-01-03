package io.github.olivoz.snowballing.fabriclike;

import io.github.olivoz.snowballing.client.SnowballingItemProperties;
import net.fabricmc.api.ClientModInitializer;

public class SnowballingModFabricLikeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SnowballingItemProperties.register();
    }
}
