package dev.theatricalmod.theatrical.block.rigging;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;

public class BlockTruss extends RotatedPillarBlock implements SimpleWaterloggedBlock, ISupport {

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public BlockTruss() {
        super(TheatricalBlocks.BASE_PROPERTIES.noOcclusion());
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Axis.X).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState iFluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis()).setValue(BlockStateProperties.WATERLOGGED, iFluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if(state.getValue(BlockStateProperties.WATERLOGGED)){
            world.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, pos,  Fluids.WATER.getTickDelay(world), 0));
        }
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float[] getLightTransforms(Level world, BlockPos pos, Direction facing) {
        return new float[]{0, 0F, 0};
    }
}
