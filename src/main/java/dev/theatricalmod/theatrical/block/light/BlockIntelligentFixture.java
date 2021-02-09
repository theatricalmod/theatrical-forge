package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockIntelligentFixture extends BlockLight {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    public BlockIntelligentFixture(Fixture fixture, Properties builder) {
        super(builder, fixture);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(HANGING, context.getFace() == Direction.DOWN || isHanging(context.getWorld(), context.getPos()));
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HANGING);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(state.get(HANGING) && !isHanging(worldIn, pos)){
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        } else if (!isHanging(worldIn, pos) && worldIn.getBlockState(pos.offset(Direction.DOWN)).isAir(worldIn, pos)) {
            if(!worldIn.isRemote) {
                FallingBlockEntity fallingBlock = new FallingBlockEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), worldIn.getBlockState(pos));
                fallingBlock.shouldDropItem = false;
                worldIn.addEntity(fallingBlock);
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity ent, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if(!world.isRemote){
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof INamedContainerProvider){
                NetworkHooks.openGui((ServerPlayerEntity) ent, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            }
            return ActionResultType.PASS;
        }
        return super.onBlockActivated(state, world, pos, ent, hand, blockRayTraceResult);
    }
}
