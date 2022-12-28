package io.github.olivoz.snowballing.datagen;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.advancement.criterion.KilledByFallDamageTrigger;
import io.github.olivoz.snowballing.registry.SnowballingEffects;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class SnowballingAdvancementProvider extends FabricAdvancementProvider {

    private static final String ROOT_TRANSLATION_PATH = "advancement." + SnowballingMod.MOD_ID;
    private static final ResourceLocation ROOT_PATH = new ResourceLocation(SnowballingMod.MOD_ID, "root");

    public SnowballingAdvancementProvider(final FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    private static Advancement.Builder advancement(ItemLike icon, String name, boolean hidden, Advancement parent) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(icon, Component.translatable(ROOT_TRANSLATION_PATH + "." + name + ".title"), Component.translatable(ROOT_TRANSLATION_PATH + "." + name + ".description"), null, FrameType.TASK, true, true, hidden);
    }

    private static Advancement build(Consumer<Advancement> consumer, String name, Advancement.Builder advancementBuilder) {
        Advancement advancement = advancementBuilder.build(new ResourceLocation(SnowballingMod.MOD_ID, name));
        consumer.accept(advancement);
        return advancement;
    }

    @Override
    public void generateAdvancement(final Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.advancement()
            .display(SnowballingItems.SNOWBALL_PILE.get(), Component.translatable(ROOT_TRANSLATION_PATH + ".root.title"), Component.translatable(ROOT_TRANSLATION_PATH + ".root.description"), new ResourceLocation("textures/block/snow.png"), FrameType.TASK, false, false, false)
            .addCriterion("snowball", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SNOWBALL))
            .build(ROOT_PATH);

        consumer.accept(root);

        build(consumer, "freeze", advancement(SnowballingItems.SNOWBALL_PILE.get(), "freeze", false, root).addCriterion("snowballed_effect", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.effects()
            .and(SnowballingEffects.SNOWBALLED.get(), new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.atLeast(3), MinMaxBounds.Ints.ANY, null, null)))));

        Advancement snowballFightAdvancement = build(consumer, "snowball_fight", advancement(SnowballingItems.SNOWBALL_PILE.get(), "snowball_fight", false, root).addCriterion("hit_snowball", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance()
            .type(DamageSourcePredicate.Builder.damageType()
                .isProjectile(true)
                .direct(EntityPredicate.Builder.entity()
                    .of(EntityType.SNOWBALL))), EntityPredicate.Builder.entity()
            .of(EntityType.VILLAGER)
            .build())));

        CompoundTag snowSlingCompound = new CompoundTag();
        snowSlingCompound.putBoolean("IsSlingShot", true);

        Advancement snowSlingAdvancement = build(consumer, "snow_sling", advancement(SnowballingItems.SNOW_SLING.get(), "snow_sling", false, snowballFightAdvancement).addCriterion("shoot_snow_sling", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance()
            .type(DamageSourcePredicate.Builder.damageType()
                .isProjectile(true)
                .direct(EntityPredicate.Builder.entity()
                    .of(EntityType.SNOWBALL)
                    .nbt(new NbtPredicate(snowSlingCompound)))))));

        build(consumer, "accidental_doom", advancement(SnowballingItems.SNOWBALL_PILE.get(), "accidental_doom", true, snowSlingAdvancement).addCriterion("snow_sling_kill", KilledByFallDamageTrigger.TriggerInstance.fall(EntityPredicate.ANY, DamageSourcePredicate.Builder.damageType()
            .isProjectile(true)
            .direct(EntityPredicate.Builder.entity()
                .of(EntityType.SNOWBALL)
                .nbt(new NbtPredicate(snowSlingCompound)))
            .build())));
    }
}
