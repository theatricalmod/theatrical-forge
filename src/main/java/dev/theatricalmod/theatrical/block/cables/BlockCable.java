package dev.theatricalmod.theatrical.block.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.TileEntityCable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockCable extends SixWayBlock {

    private final CableType cableType;

    public BlockCable(CableType cableType) {
        super(0.125F, TheatricalBlocks.BASE_PROPERTIES.notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
        this.cableType = cableType;
    }

    public CableType getCableType() {
        return this.cableType;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult ray, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(BlockItem.BLOCK_TO_ITEM.get(this));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        boolean flag = canConnect(worldIn, facingPos, facing);
        return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), flag);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.makeConnections(context.getWorld(), context.getPos());
    }

    public BlockState makeConnections(IBlockReader p_196497_1_, BlockPos p_196497_2_) {
        boolean down = canConnect(p_196497_1_, p_196497_2_.down(), Direction.DOWN);
        boolean up = canConnect(p_196497_1_, p_196497_2_.up(), Direction.UP);
        boolean north = canConnect(p_196497_1_, p_196497_2_.north(), Direction.NORTH);
        boolean east = canConnect(p_196497_1_, p_196497_2_.east(), Direction.EAST);
        boolean south = canConnect(p_196497_1_, p_196497_2_.south(), Direction.SOUTH);
        boolean west = canConnect(p_196497_1_, p_196497_2_.west(), Direction.WEST);
        return this.getDefaultState().with(DOWN, down).with(UP, up).with(NORTH, north).with(EAST, east).with(SOUTH, south).with(WEST, west);
    }

    public boolean containsType(CableType[] types){
        for(CableType type : types){
            if(type == this.cableType){
                return true;
            }
        }
        return false;
    }

    public boolean canConnect(IBlockReader blockReader, BlockPos pos, Direction direction){
        Block block = blockReader.getBlockState(pos).getBlock();
        TileEntity tileEntity = blockReader.getTileEntity(pos);
        if(tileEntity instanceof IAcceptsCable){
            return containsType(((IAcceptsCable) tileEntity).getAcceptedCables(direction.getOpposite()));
        }
        return block == this;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntityCable tileEntityCable = new TileEntityCable();
        tileEntityCable.setCableType(cableType);
        return tileEntityCable;
    }
}
