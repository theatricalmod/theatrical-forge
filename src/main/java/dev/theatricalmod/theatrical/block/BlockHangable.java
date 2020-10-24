package dev.theatricalmod.theatrical.block;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.items.ItemWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockHangable extends HorizontalBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public Direction[] allowedPlaces;

    protected BlockHangable(Properties builder, Direction[] allowedPlaces) {
        super(builder);
        this.allowedPlaces = allowedPlaces;
        setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public boolean isHanging(World world, BlockPos pos){
        BlockPos up = pos.offset(Direction.UP);
        return world.getBlockState(up).getBlock() instanceof ISupport;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos up = pos.offset(Direction.UP);
        if(worldIn.getBlockState(up).getBlock() != Blocks.AIR && worldIn.getBlockState(up).getBlock() instanceof ISupport){
            ISupport support = (ISupport) worldIn.getBlockState(up).getBlock();
            for(Direction facing : allowedPlaces){
                if (support.getBlockPlacementDirection(worldIn, pos, facing).equals(facing)) {
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(!isHanging(worldIn, pos)){
            if(!worldIn.isRemote) {
                FallingBlockEntity fallingBlock = new FallingBlockEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), worldIn.getBlockState(pos));
                fallingBlock.shouldDropItem = false;
                worldIn.addEntity(fallingBlock);
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(player.getHeldItem(handIn).getItem() instanceof ItemWrench){
            Direction currentDirection = state.get(FACING);
            int index = currentDirection.getHorizontalIndex();
            if(index == 3) {
                index = 0;
            } else {
                index += 1;
            }
            Direction newDirection = Direction.byHorizontalIndex(index);
            worldIn.setBlockState(pos, state.with(FACING, newDirection));
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
