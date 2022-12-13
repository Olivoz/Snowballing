package io.github.olivoz.snowballing.block;

import io.github.olivoz.snowballing.registry.SnowballingGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class SnowballPileBlock extends Block {

    public static final int MAX_SIZE = 6;
    public static final IntegerProperty SNOWBALLS = IntegerProperty.create("snowballs", 1, MAX_SIZE);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public SnowballPileBlock() {
        super(BlockBehaviour.Properties.of(Material.TOP_SNOW)
            .randomTicks()
            .noCollission()
            .strength(0.1F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.SNOW));

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(SNOWBALLS, 1));
    }

    public static int removeSnowball(Level level, BlockPos blockPos, BlockState blockState, int amount) {
        return addSnowball(level, blockPos, blockState, -amount);
    }

    public static int addSnowball(Level level, BlockPos blockPos, BlockState blockState, int amount) {
        int size = blockState.getValue(SNOWBALLS);
        int newSize = Mth.clamp(size + amount, 0, MAX_SIZE);
        int diff = Math.abs(size - newSize);

        if(newSize == 0) {
            level.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
            return diff;
        }

        level.setBlockAndUpdate(blockPos, blockState.setValue(SNOWBALLS, newSize));
        if(newSize > size) level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);

        return diff;
    }

    @Override
    public boolean isPathfindable(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos, final PathComputationType pathComputationType) {
        return pathComputationType == PathComputationType.LAND;
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos below = blockPos.below();
        BlockState belowState = levelReader.getBlockState(below);
        return !belowState.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON) && (belowState.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON) || Block.isFaceFull(belowState.getCollisionShape(levelReader, below), Direction.UP));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if(!blockState.canSurvive(levelAccessor, blockPos)) return Blocks.AIR.defaultBlockState();

        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if(!serverLevel.getGameRules()
            .getBoolean(SnowballingGameRules.SNOWBALL_PILES_MELT)) return;

        if(serverLevel.getBrightness(LightLayer.BLOCK, blockPos) > 11) return;

        Biome biome = serverLevel.getBiome(blockPos)
            .value();

        if(!biome.shouldSnowGolemBurn(blockPos) && !serverLevel.isRainingAt(blockPos))
            return;

        SnowballPileBlock.removeSnowball(serverLevel, blockPos, blockState, 1);
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        ItemStack itemInHand = blockPlaceContext.getItemInHand();
        if(itemInHand.is(Items.SNOW)) return false;

        if(itemInHand.is(this.asItem()) && blockPlaceContext.replacingClickedOnBlock())
            return blockPlaceContext.getClickedFace() == Direction.UP;

        return true;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel()
            .getBlockState(blockPlaceContext.getClickedPos());

        ItemStack itemInHand = blockPlaceContext.getItemInHand();

        CompoundTag tag = itemInHand.getTag();
        int amount = tag == null ? 1 : tag.getInt("size");

        if(blockState.is(this)) {
            int currentSize = blockState.getValue(SNOWBALLS);
            return blockState.setValue(SNOWBALLS, Mth.clamp(currentSize + amount + 1, 1, MAX_SIZE));
        }

        return defaultBlockState().setValue(SNOWBALLS, Mth.clamp(amount, 1, MAX_SIZE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWBALLS);
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos, final Player player, final InteractionHand interactionHand, final BlockHitResult blockHitResult) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        Item itemInHandType = itemInHand.getItem();

        if(itemInHand.isEmpty()) {
            if(player.isCrouching()) return InteractionResult.FAIL;
            if(!level.isClientSide) {
                SnowballPileBlock.removeSnowball(level, blockPos, blockState, 1);
                player.setItemInHand(interactionHand, Items.SNOWBALL.getDefaultInstance());
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if(itemInHandType == Items.SNOWBALL) {
            if(player.getAbilities().instabuild) {
                if(!level.isClientSide) SnowballPileBlock.removeSnowball(level, blockPos, blockState, 1);

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if(itemInHand.getMaxStackSize() > itemInHand.getCount()) {
                if(!level.isClientSide) {
                    SnowballPileBlock.removeSnowball(level, blockPos, blockState, 1);
                    itemInHand.grow(1);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }

        if(itemInHandType instanceof ShovelItem) {
            int size = blockState.getValue(SNOWBALLS);
            if(size >= MAX_SIZE) return InteractionResult.FAIL;

            if(!level.isClientSide) {
                if(!player.getAbilities().instabuild) {
                    if(itemInHand.getMaxDamage() > 0) {
                        itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    } else {
                        itemInHand.shrink(1);
                    }
                }

                SnowballPileBlock.addSnowball(level, blockPos, blockState, 1);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(final ItemStack itemStack, @Nullable final BlockGetter blockGetter, final List<Component> list, final TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockGetter, list, tooltipFlag);
        CompoundTag tag = itemStack.getTag();
        int amount = tag == null ? 1 : tag.getInt("size");
        list.add(Component.translatable("item.snowballing.snowball_pile.size", amount));
    }
}
