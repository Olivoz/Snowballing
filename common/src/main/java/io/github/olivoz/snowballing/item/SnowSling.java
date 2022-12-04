package io.github.olivoz.snowballing.item;

import io.github.olivoz.snowballing.block.SnowPileBlock;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class SnowSling extends ProjectileWeaponItem implements Vanishable {

    public static final Predicate<ItemStack> SNOWBALL = itemStack -> itemStack.is(Items.SNOWBALL);
    public static final int PROJECTILE_RANGE = 8;
    private static final String TAG_CHARGED = "Charged";
    private static final String TAG_FILLED = "Filled";

    public SnowSling() {
        super(new Properties().durability(384)
            .tab(SnowballingItems.SNOWBALLING_MOD_TAB));
    }

    private static void shoot(Level level, LivingEntity shooter, InteractionHand interactionHand, ItemStack itemStack) {
        Snowball snowball = new Snowball(level, shooter);
        snowball.setItem(Items.SNOWBALL.getDefaultInstance());
        snowball.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);

        itemStack.hurtAndBreak(1, shooter, livingEntity -> livingEntity.broadcastBreakEvent(interactionHand));
        level.addFreshEntity(snowball);
    }

    public static boolean isFilled(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.getBoolean(TAG_FILLED);
    }

    public static void setFilled(ItemStack itemStack, boolean filled) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putBoolean(TAG_FILLED, filled);
    }

    public static boolean isCharged(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.getBoolean(TAG_CHARGED);
    }

    public static void setCharged(ItemStack itemStack, boolean charged) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putBoolean(TAG_CHARGED, charged);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return SNOWBALL;
    }

    public int getDefaultProjectileRange() {
        return PROJECTILE_RANGE;
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if(isCharged(itemStack)) {
            shoot(level, player, interactionHand, itemStack);
            setFilled(itemStack, false);
            setCharged(itemStack, false);
            return InteractionResultHolder.consume(itemStack);
        }

        if(isFilled(itemStack) || !player.getProjectile(itemStack)
            .isEmpty()) {
            if(!isCharged(itemStack)) {
                player.startUsingItem(interactionHand);
            }

            return InteractionResultHolder.consume(itemStack);
        }

        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public boolean useOnRelease(ItemStack itemStack) {
        return itemStack.is(this);
    }

    @Override
    public int getUseDuration(final ItemStack itemStack) {
        return 25;
    }

    @Override
    public InteractionResult useOn(final UseOnContext useOnContext) {
        ItemStack itemInHand = useOnContext.getItemInHand();
        if(isFilled(itemInHand)) return super.useOn(useOnContext);

        BlockPos blockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(SnowballingBlocks.SNOWBALL_PILE.get())) return super.useOn(useOnContext);

        SnowPileBlock.removeSnowball(level, blockPos, blockState, 1);
        setFilled(itemInHand, true);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void releaseUsing(final ItemStack itemStack, final Level level, final LivingEntity livingEntity, final int useTimeLeft) {
        if(useTimeLeft < 0 && !isCharged(itemStack)) {
            if(livingEntity instanceof Player player && !isFilled(itemStack)) {
                ItemStack projectileItem = player.getProjectile(itemStack);
                if(projectileItem.isEmpty()) return;
                projectileItem.shrink(1);
            }

            setCharged(itemStack, true);
        }
    }
}
