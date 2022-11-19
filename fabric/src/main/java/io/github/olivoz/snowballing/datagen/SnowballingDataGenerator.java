package io.github.olivoz.snowballing.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SnowballingDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(final FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(SnowballingBlockLootProvider::new);
        fabricDataGenerator.addProvider(SnowballingTagProvider::new);
    }
}
