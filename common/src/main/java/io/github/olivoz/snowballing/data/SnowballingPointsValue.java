package io.github.olivoz.snowballing.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.olivoz.snowballing.registry.SnowballingLootContextParams;
import io.github.olivoz.snowballing.registry.SnowballingNumberProviders;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SnowballingPointsValue implements NumberProvider {

    public static final SnowballingPointsValue INSTANCE = new SnowballingPointsValue();

    private SnowballingPointsValue() {
    }

    @Override
    public float getFloat(final LootContext lootContext) {
        Integer lootContextValue = lootContext.getParamOrNull(SnowballingLootContextParams.SNOWBALL_FIGHT_POINTS);
        return lootContextValue == null ? 0.0F : lootContextValue.floatValue();
    }

    @Override
    public LootNumberProviderType getType() {
        return SnowballingNumberProviders.SNOWBALL_FIGHT_POINTS.get();
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SnowballingPointsValue> {

        @Override
        public void serialize(final JsonObject jsonObject, final SnowballingPointsValue value, final JsonSerializationContext jsonSerializationContext) {
            // Nothing to serialize
        }

        @Override
        public SnowballingPointsValue deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }
    }
}
