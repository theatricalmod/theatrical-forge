package dev.theatricalmod.theatrical.block;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockHangable extends HorizontalBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");

    protected BlockHangable(Properties builder) {
        super(builder);
        this.setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(BROKEN, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader p_190948_2_, List<ITextComponent> tooltips, ITooltipFlag advanced) {
        if(stack.hasTag() && stack.getTag().contains("BlockStateTag") && Boolean.parseBoolean(stack.getTag().getCompound("BlockStateTag").getString("broken"))) {
            tooltips.add(new StringTextComponent(TextFormatting.RED + "Broken!"));
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos blockPos, BlockState state, PlayerEntity playerIn) {
        if (!world.isRemote && playerIn.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ItemStack stack = new ItemStack(this);
            CompoundNBT nbt = new CompoundNBT();
            if(state.get(BlockHangable.BROKEN)) {
                nbt.putString("broken", String.valueOf(state.get(BlockHangable.BROKEN)));
                stack.setTagInfo("BlockStateTag", nbt);
            }
            ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
            itemEntity.setDefaultPickupDelay();
            world.addEntity(itemEntity);
        }
        super.onBlockHarvested(world, blockPos, state, playerIn);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, BROKEN);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return isHanging(worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(!state.isValidPosition(worldIn, pos)) {
            worldIn.addEntity(new FallingLightEntity(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state.with(BROKEN, true)));
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public boolean isHanging(IWorldReader world, BlockPos pos){
        BlockPos up = pos.up();
        return !world.isAirBlock(up) && world.getBlockState(up).getBlock() instanceof ISupport;
    }
}
