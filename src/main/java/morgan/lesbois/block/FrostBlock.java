package morgan.lesbois.block;

import com.mojang.serialization.MapCodec;
import morgan.lesbois.powers.FrostGlidingPowerType;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FrostBlock extends TranslucentBlock implements Waterloggable {
    public static final MapCodec<FrostBlock> CODEC = createCodec(FrostBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);

    public MapCodec<FrostBlock> getCodec() {
        return CODEC;
    }

    public FrostBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isAbove(COLLISION_SHAPE, pos, true)) {
            return COLLISION_SHAPE;
        }

        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return COLLISION_SHAPE;
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.increaseAge(state, world, pos)) {
            world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 10, 20));
        }
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int currentAge = state.get(AGE);
        if (currentAge < MAX_AGE) {
            world.setBlockState(pos, state.with(AGE, currentAge + 1), 2);
            return true;
        } else {
            melt(state, world, pos);
            return false;
        }
    }

    public static void melt(BlockState state, World world, BlockPos pos) {
        world.syncWorldEvent(net.minecraft.world.WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));

        if (state.get(WATERLOGGED)) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, WATERLOGGED);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    public static boolean canReplace(BlockState blockState) {
        if (blockState.isReplaceable() && !blockState.isOf(Blocks.LAVA)) {
            return true;
        }

        if (blockState.isOf(LesboisBlocks.FROST_BLOCK) && blockState.get(AGE) > 0) {
            return true;
        }

        return false;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity instanceof PlayerEntity player && FrostGlidingPowerType.shouldFrostGlide(player)) {
            entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
        }
        else {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }
    }
}
