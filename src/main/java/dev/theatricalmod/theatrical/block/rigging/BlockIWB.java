package dev.theatricalmod.theatrical.block.rigging;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockIWB extends HorizontalBlock implements ISupport {

    private final VoxelShape Z_BOX = VoxelShapes.create(new AxisAlignedBB(0.35, 0, 0, 0.65, 0.2, 1));
    private final VoxelShape X_BOX = VoxelShapes.create(new AxisAlignedBB(0, 0, 0.4, 1, 0.2, 0.6));

    public BlockIWB() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Override
    public Direction getBlockPlacementDirection(IWorldReader world, BlockPos pos, Direction facing) {
        return facing.getOpposite();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing());
    }

    @Override
    public float[] getLightTransforms(World world, BlockPos pos, Direction facing) {
        return new float[]{0, 0.19F, 0};
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(HORIZONTAL_FACING).getAxis() == Axis.Z ? Z_BOX : X_BOX;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if(!player.getHeldItem(handIn).isEmpty()){
            Item item = player.getHeldItem(handIn).getItem();
            if (getBlockFromItem(item) instanceof BlockHangable) {
                BlockHangable block = (BlockHangable) getBlockFromItem(item);
                BlockPos down = pos.offset(Direction.DOWN);
                if(!worldIn.isAirBlock(down)){
                    return ActionResultType.FAIL;
                }
                worldIn.setBlockState(down, block.getDefaultState().with(BlockHangable.FACING, player.getHorizontalFacing()));
                if(player.getHeldItem(handIn).getCount() > 1) {
                    player.getHeldItem(handIn).setCount(player.getHeldItem(handIn).getCount() - 1);
                } else {
                    player.setHeldItem(handIn, new ItemStack(Items.AIR));
                }
                return ActionResultType.CONSUME;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, p_225533_6_);
    }
}
