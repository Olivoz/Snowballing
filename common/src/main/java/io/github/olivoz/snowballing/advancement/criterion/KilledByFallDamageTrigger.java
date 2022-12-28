package io.github.olivoz.snowballing.advancement.criterion;

import com.google.gson.JsonObject;
import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class KilledByFallDamageTrigger extends SimpleCriterionTrigger<KilledByFallDamageTrigger.TriggerInstance> {

    static final ResourceLocation ID = new ResourceLocation(SnowballingMod.MOD_ID, "killed_by_fall");

    @Override
    protected KilledByFallDamageTrigger.TriggerInstance createInstance(final JsonObject jsonObject, final EntityPredicate.Composite composite, final DeserializationContext deserializationContext) {
        return new TriggerInstance(composite, MinMaxBounds.Doubles.fromJson(jsonObject.get("height")), EntityPredicate.Composite.fromJson(jsonObject, "entity", deserializationContext), DamageSourcePredicate.fromJson(jsonObject.get("lastDamageSource")));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer serverPlayer, Entity entity) {
        LootContext lootContext = EntityPredicate.createContext(serverPlayer, entity);
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(serverPlayer, lootContext));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final MinMaxBounds.Doubles height;
        private final EntityPredicate.Composite entityPredicate;
        private final DamageSourcePredicate lastDamageSource;

        public TriggerInstance(final EntityPredicate.Composite composite, final MinMaxBounds.Doubles height, final EntityPredicate.Composite entityPredicate, final DamageSourcePredicate lastDamageSource) {
            super(KilledByFallDamageTrigger.ID, composite);
            this.height = height;
            this.entityPredicate = entityPredicate;
            this.lastDamageSource = lastDamageSource;
        }

        public static TriggerInstance fall() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Doubles.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
        }

        public static TriggerInstance fall(final EntityPredicate entityPredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Doubles.ANY, EntityPredicate.Composite.wrap(entityPredicate), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance fall(final DamageSourcePredicate lastDamageSource) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Doubles.ANY, EntityPredicate.Composite.ANY, lastDamageSource);
        }

        public static TriggerInstance fall(final EntityPredicate entityPredicate, final DamageSourcePredicate lastDamageSource) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Doubles.ANY, EntityPredicate.Composite.wrap(entityPredicate), lastDamageSource);
        }

        public static TriggerInstance fall(final MinMaxBounds.Doubles height, final EntityPredicate entityPredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, height, EntityPredicate.Composite.wrap(entityPredicate), DamageSourcePredicate.ANY);
        }

        public static TriggerInstance fall(final MinMaxBounds.Doubles height, final EntityPredicate entityPredicate, final DamageSourcePredicate lastDamageSource) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, height, EntityPredicate.Composite.wrap(entityPredicate), lastDamageSource);
        }

        public boolean matches(ServerPlayer serverPlayer, LootContext lootContext) {
            Entity killedMob = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
            return killedMob instanceof LivingEntity livingEntity && livingEntity.lastDamageSource != null && this.lastDamageSource.matches(serverPlayer, livingEntity.lastDamageSource) && this.entityPredicate.matches(lootContext) && this.height.matches(livingEntity.fallDistance);
        }

        @Override
        public JsonObject serializeToJson(final SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("height", this.height.serializeToJson());
            jsonObject.add("entity", this.entityPredicate.toJson(serializationContext));
            jsonObject.add("lastDamageSource", this.lastDamageSource.serializeToJson());
            return jsonObject;
        }
    }
}
