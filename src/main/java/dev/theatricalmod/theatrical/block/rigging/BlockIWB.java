package dev.theatricalmod.theatrical.block.rigging;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockIWB extends HorizontalDirectionalBlock implements ISupport {

    private final VoxelShape Z_BOX = Shapes.create(new AABB(0.35, 0, 0, 0.65, 0.2, 1));
    private final VoxelShape X_BOX = Shapes.create(new AABB(0, 0, 0.4, 1, 0.2, 0.6));

    public BlockIWB() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection());
    }

    @Override
    public float[] getLightTransforms(Level world, BlockPos pos, Direction facing) {
        return new float[]{0, 0.19F, 0};
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Axis.Z ? Z_BOX : X_BOX;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        if(!player.getItemInHand(handIn).isEmpty()){
            Item item = player.getItemInHand(handIn).getItem();
            if (byItem(item) instanceof BlockHangable) {
                BlockHangable block = (BlockHangable) byItem(item);
                BlockPos down = pos.relative(Direction.DOWN);
                if(!worldIn.isEmptyBlock(down)){
                    return InteractionResult.FAIL;
                }
                worldIn.setBlockAndUpdate(down, block.defaultBlockState().setValue(BlockHangable.FACING, player.getDirection()));
                if(!player.isCreative()) {
                    if (player.getItemInHand(handIn).getCount() > 1) {
                        player.getItemInHand(handIn).setCount(player.getItemInHand(handIn).getCount() - 1);
                    } else {
                        player.setItemInHand(handIn, new ItemStack(Items.AIR));
                    }
                }
                return InteractionResult.CONSUME;
            }
        }
        return super.use(state, worldIn, pos, player, handIn, p_225533_6_);
    }
}
