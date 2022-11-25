package io.github.olivoz.snowballing.villager.behaviour;

import io.github.olivoz.snowballing.registry.SnowballingMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Map;

public class SnowballAttack extends Behavior<Villager> {

    public static final int TIMEOUT = 60 * 20;
    public static final int ATTACK_INTERVAL = 20;
    public static final int ATTACK_RADIUS_SQUARED = 20 * 20;

    private final float speed;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public SnowballAttack(float speed) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get(), MemoryStatus.VALUE_PRESENT, SnowballingMemoryModules.SNOW_AT.get(), MemoryStatus.VALUE_ABSENT), TIMEOUT);
        this.speed = speed;
    }

    private static ItemStack firstStackOfType(Villager villager, Item item) {
        SimpleContainer inventory = villager.getInventory();
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemInSlot = inventory.getItem(i);
            if(itemInSlot.getItem() == item) return itemInSlot;
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected boolean timedOut(final long currentTick) {
        return this.seeTime < -TIMEOUT;
    }

    @Override
    protected boolean checkExtraStartConditions(final ServerLevel serverLevel, final Villager villager) {
        return villager.getInventory()
            .countItem(Items.SNOWBALL) >= Items.SNOWBALL.getMaxStackSize();
    }

    @Override
    protected void start(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        villager.setItemSlot(EquipmentSlot.MAINHAND, firstStackOfType(villager, Items.SNOWBALL));
        villager.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void stop(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        villager.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    @Override
    public void tick(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        LivingEntity target = villager.getBrain()
            .getMemory(SnowballingMemoryModules.SNOWBALL_FIGHT_ENEMY.get())
            .orElse(null);

        if(target == null) {
            stop(serverLevel, villager, currentTick);
            return;
        }

        double distance = villager.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean hasLineOfSight = villager.getSensing()
            .hasLineOfSight(target);

        boolean hasSeen = this.seeTime > 0;
        if(hasLineOfSight != hasSeen) {
            this.seeTime = 0;
        }

        if(hasLineOfSight) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if(distance <= ATTACK_RADIUS_SQUARED && this.seeTime >= 20) {
            villager.getNavigation()
                .stop();
            ++this.strafingTime;
        } else {
            villager.getNavigation()
                .moveTo(target, this.speed);
            this.strafingTime = -1;
        }

        if(this.strafingTime >= 20) {
            if(villager.getRandom()
                .nextFloat() < 0.3) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if(villager.getRandom()
                .nextFloat() < 0.3) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if(this.strafingTime > -1) {
            if(distance > ATTACK_RADIUS_SQUARED * 0.75F) {
                this.strafingBackwards = false;
            } else if(distance < ATTACK_RADIUS_SQUARED * 0.25F) {
                this.strafingBackwards = true;
            }

            villager.getMoveControl()
                .strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            villager.lookAt(target, 30.0F, 30.0F);
        } else {
            villager.getLookControl()
                .setLookAt(target, 30.0F, 30.0F);
        }

        if(this.attackTime <= 0) {
            ItemStack mainHandItem = villager.getMainHandItem();
            if(mainHandItem.isEmpty())
                villager.setItemSlot(EquipmentSlot.MAINHAND, firstStackOfType(villager, Items.SNOWBALL));
            mainHandItem.shrink(1);
            throwSnowball(villager, target);
            this.attackTime = ATTACK_INTERVAL;
        } else {
            this.attackTime--;
        }
    }

    @Override
    protected boolean canStillUse(final ServerLevel serverLevel, final Villager villager, final long currentTick) {
        return !timedOut(currentTick) && villager.getInventory()
            .countItem(Items.SNOWBALL) > 0;
    }

    private void throwSnowball(Villager villager, LivingEntity target) {
        Level level = villager.level;
        Snowball snowball = new Snowball(level, villager);
        double x = target.getX() - villager.getX();
        double y = target.getY(1 / 3F) - snowball.getY();
        double z = target.getZ() - villager.getZ();
        double i = Math.sqrt(x * x + z * z) * 0.2;
        snowball.shoot(x, y + i, z, 1.6F, 4.0F);
        villager.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (villager.getRandom()
            .nextFloat() * 0.4F + 0.8F));
        level.addFreshEntity(snowball);
    }

}
