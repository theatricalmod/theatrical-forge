package dev.theatricalmod.theatrical.block.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.TileEntityCable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockCable extends PipeBlock implements EntityBlock {

    private final CableType cableType;

    public BlockCable(CableType cableType) {
        super(0.125F, TheatricalBlocks.BASE_PROPERTIES.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
        this.cableType = cableType;
    }

    public CableType getCableType() {
        return this.cableType;
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public boolean causesSuffocation(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return false;
    }

    public boolean isNormalCube(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return false;
    }

    public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(BlockItem.BY_BLOCK.get(this));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        boolean flag = canConnect(worldIn, facingPos, facing);
        return stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.makeConnections(context.getLevel(), context.getClickedPos());
    }

    public BlockState makeConnections(BlockGetter p_196497_1_, BlockPos p_196497_2_) {
        boolean down = canConnect(p_196497_1_, p_196497_2_.below(), Direction.DOWN);
        boolean up = canConnect(p_196497_1_, p_196497_2_.above(), Direction.UP);
        boolean north = canConnect(p_196497_1_, p_196497_2_.north(), Direction.NORTH);
        boolean east = canConnect(p_196497_1_, p_196497_2_.east(), Direction.EAST);
        boolean south = canConnect(p_196497_1_, p_196497_2_.south(), Direction.SOUTH);
        boolean west = canConnect(p_196497_1_, p_196497_2_.west(), Direction.WEST);
        return this.defaultBlockState().setValue(DOWN, down).setValue(UP, up).setValue(NORTH, north).setValue(EAST, east).setValue(SOUTH, south).setValue(WEST, west);
    }

    public boolean containsType(CableType[] types){
        for(CableType type : types){
            if(type == this.cableType){
                return true;
            }
        }
        return false;
    }

    public boolean canConnect(BlockGetter blockReader, BlockPos pos, Direction direction){
        Block block = blockReader.getBlockState(pos).getBlock();
        BlockEntity tileEntity = blockReader.getBlockEntity(pos);
        if(tileEntity instanceof IAcceptsCable){
            return containsType(((IAcceptsCable) tileEntity).getAcceptedCables(direction.getOpposite()));
        }
        return block == this;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        TileEntityCable tileEntityCable = new TileEntityCable(blockPos, blockState);
        tileEntityCable.setCableType(cableType);
        return tileEntityCable;
    }
}
