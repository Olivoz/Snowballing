package io.github.olivoz.snowballing.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

@SuppressWarnings("deprecation")
public class SnowPileBlock extends Block {

    public static final int MAX_SIZE = 6;
    public static final IntegerProperty SNOWBALLS = IntegerProperty.create("snowballs", 1, MAX_SIZE);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public SnowPileBlock() {
        super(BlockBehaviour.Properties.of(Material.TOP_SNOW)
            .randomTicks()
            .noCollission());

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(SNOWBALLS, 1));
    }

    public static void removeSnowball(Level level, BlockPos blockPos, BlockState blockState) {
        int newSize = blockState.getValue(SNOWBALLS) - 1;

        if(newSize < 1) {
            level.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
            return;
        }

        level.setBlockAndUpdate(blockPos, blockState.setValue(SNOWBALLS, newSize));
    }

    public static void addSnowball(Level level, BlockPos blockPos, BlockState blockState) {
        int newSize = blockState.getValue(SNOWBALLS) + 1;
        if(newSize > MAX_SIZE) return;

        level.setBlockAndUpdate(blockPos, blockState.setValue(SNOWBALLS, newSize));
        level.playSound(null, blockPos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
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
        if(serverLevel.getBrightness(LightLayer.BLOCK, blockPos) > 11) return;
        if(serverLevel.getBiome(blockPos)
            .value()
            .coldEnoughToSnow(blockPos)) return;
        SnowPileBlock.removeSnowball(serverLevel, blockPos, blockState);
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

        if(blockState.is(this)) {
            int size = blockState.getValue(SNOWBALLS);
            return blockState.setValue(SNOWBALLS, Math.min(size + 1, MAX_SIZE));
        }

        return super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWBALLS);
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos, final Player player, final InteractionHand interactionHand, final BlockHitResult blockHitResult) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        Item itemInHandType = itemInHand.getItem();
        if(itemInHandType != Items.SNOWBALL && !(itemInHandType instanceof ShovelItem)) return InteractionResult.PASS;

        if(!level.isClientSide) {
            int size = blockState.getValue(SNOWBALLS);
            if(size < MAX_SIZE) {
                if(!player.getAbilities().instabuild) {
                    if(itemInHand.getMaxDamage() > 0) {
                        itemInHand.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    } else {
                        itemInHand.shrink(1);
                    }
                }

                SnowPileBlock.addSnowball(level, blockPos, blockState);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
