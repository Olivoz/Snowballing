package io.github.olivoz.snowballing.item;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.olivoz.snowballing.block.SnowballPileBlock;
import io.github.olivoz.snowballing.extend.SlingShotSnowball;
import io.github.olivoz.snowballing.registry.SnowballingBlocks;
import io.github.olivoz.snowballing.registry.SnowballingItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class SnowSling extends ProjectileWeaponItem implements Vanishable {

    public static final Collection<Enchantment> ALLOWED_ENCHANTMENTS = List.of(Enchantments.MULTISHOT);

    public static final Predicate<ItemStack> SNOWBALL = itemStack -> itemStack.is(Items.SNOWBALL);
    public static final int PROJECTILE_RANGE = 8;
    private static final String TAG_FILLED = "Filled";

    public SnowSling() {
        super(new Properties().durability(384)
            .tab(SnowballingItems.SNOWBALLING_MOD_TAB));
    }

    public static boolean isFilled(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.getInt(TAG_FILLED) > 0;
    }

    public static void addSnowballs(ItemStack snowSling, int amount) {
        CompoundTag tag = snowSling.getOrCreateTag();
        tag.putInt(TAG_FILLED, Math.max(0, tag.getInt(TAG_FILLED) + amount));
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
        if(isFilled(itemStack) || !player.getProjectile(itemStack)
            .isEmpty()) {

            if(isFilled(itemStack) || player.getAbilities().instabuild || !player.getProjectile(player.getItemInHand(interactionHand))
                .isEmpty()) {

                player.startUsingItem(interactionHand);
            }

            return InteractionResultHolder.consume(itemStack);
        }

        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public int getUseDuration(final ItemStack itemStack) {
        return 72000;
    }

    @Override
    public InteractionResult useOn(final UseOnContext useOnContext) {
        ItemStack itemInHand = useOnContext.getItemInHand();
        if(isFilled(itemInHand)) return super.useOn(useOnContext);

        BlockPos blockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(SnowballingBlocks.SNOWBALL_PILE.get())) return super.useOn(useOnContext);

        int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, itemInHand);
        int capacity = multishotLevel == 0 ? 1 : 3;

        int removed = SnowballPileBlock.removeSnowball(level, blockPos, blockState, capacity);
        addSnowballs(itemInHand, removed);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void releaseUsing(final ItemStack itemStack, final Level level, final LivingEntity livingEntity, final int useTimeLeft) {
        int snowballs = 0;

        CompoundTag compoundTag = itemStack.getTag();
        if(compoundTag != null) {
            snowballs = compoundTag.getInt(TAG_FILLED);
            compoundTag.remove(TAG_FILLED);
        }

        if(livingEntity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));

            int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, itemStack);
            int capacity = multishotLevel == 0 ? 1 : 3;

            if(!player.getAbilities().instabuild) {
                for(int i = Mth.clamp(snowballs, 0, capacity); i < capacity; i++) {
                    ItemStack projectileItem = player.getProjectile(itemStack);
                    if(projectileItem.isEmpty()) break;

                    projectileItem.shrink(1);
                    if(projectileItem.isEmpty()) player.getInventory()
                        .removeItem(projectileItem);

                    snowballs++;
                }
            } else {
                snowballs = Math.max(capacity, snowballs);
            }
        }

        itemStack.hurtAndBreak(1, livingEntity, consumedPlayer -> consumedPlayer.broadcastBreakEvent(consumedPlayer.getUsedItemHand()));
        if(!level.isClientSide) {

            float power = BowItem.getPowerForTime(getUseDuration(itemStack) - useTimeLeft) * 1.5F;
            float offsetAngle = snowballs > 1 ? -5 * (snowballs - 1) : 0;

            for(int i = 0; i < snowballs; i++) {
                Snowball snowball = new Snowball(level, livingEntity);
                snowball.setItem(Items.SNOWBALL.getDefaultInstance());

                Vec3 upVector = livingEntity.getUpVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(upVector), offsetAngle, true);
                offsetAngle += 10;
                Vec3 viewVector = livingEntity.getViewVector(1.0F);
                Vector3f transformedViewVector = new Vector3f(viewVector);
                transformedViewVector.transform(quaternion);
                snowball.shoot(transformedViewVector.x(), transformedViewVector.y(), transformedViewVector.z(), power, 1F);

                SlingShotSnowball slingShotSnowball = (SlingShotSnowball) snowball;
                slingShotSnowball.setCharge(power);
                slingShotSnowball.setSlingShot(true);

                level.addFreshEntity(snowball);
            }
        }
    }

    // This overrides the Forge method from IForgeItem
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
        return ALLOWED_ENCHANTMENTS.contains(enchantment) || enchantment.category.canEnchant(stack.getItem());
    }
}
