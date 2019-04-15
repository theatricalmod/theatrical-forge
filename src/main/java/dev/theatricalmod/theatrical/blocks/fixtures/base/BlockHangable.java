package dev.theatricalmod.theatrical.blocks.fixtures.base;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.blocks.base.BlockDirectional;
import dev.theatricalmod.theatrical.tabs.base.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHangable extends BlockDirectional {

    public EnumFacing[] allowedPlaces;

    public BlockHangable(String name, EnumFacing[] allowedPlaces) {
        super(name);
        this.allowedPlaces = allowedPlaces;
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int facing = meta;
        EnumFacing facing1 = EnumFacing.byHorizontalIndex(facing);

        return this.getDefaultState().withProperty(FACING, facing1);
    }


    @Override
    public int getMetaFromState(IBlockState state) {
        int facingBits = state.getValue(FACING).getHorizontalIndex();
        return facingBits;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockPos up = pos.offset(EnumFacing.UP);
        if(worldIn.getBlockState(up).getBlock() != Blocks.AIR && worldIn.getBlockState(up).getBlock() instanceof ISupport){
            ISupport support = (ISupport) worldIn.getBlockState(up).getBlock();
            for(EnumFacing facing : allowedPlaces){
                if (support.getBlockPlacementDirection(worldIn, pos, facing).equals(facing)) {
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(!isHanging(worldIn, pos)){
            if(!worldIn.isRemote) {
                EntityFallingBlock fallingBlock = new EntityFallingBlock(worldIn, pos.getX(), pos.getY(), pos.getZ(), worldIn.getBlockState(pos));
                fallingBlock.shouldDropItem = false;
                worldIn.spawnEntity(fallingBlock);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    public boolean isHanging(World world, BlockPos pos){
        BlockPos up = pos.offset(EnumFacing.UP);
        return world.getBlockState(up).getBlock() instanceof ISupport;
    }

}
