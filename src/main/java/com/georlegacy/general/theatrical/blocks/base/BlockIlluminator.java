package com.georlegacy.general.theatrical.blocks.base;

import com.georlegacy.general.theatrical.api.IFixture;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tiles.TileIlluminator;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIlluminator extends BlockBase implements ITileEntityProvider, IHasTileEntity {

    private final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);


    public BlockIlluminator() {
        super("illuminator");
        this.setBlockUnbreakable();
        setLightLevel(15);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn,
        BlockPos pos) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return EMPTY;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return EMPTY;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileIlluminator();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileIlluminator.class;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!(world.getTileEntity(pos) instanceof TileIlluminator)) {
            return 0;
        }
        TileIlluminator tileIlluminator = (TileIlluminator) world.getTileEntity(pos);
        if (tileIlluminator == null) {
            return 0;
        }
        if (tileIlluminator.getController() == null) {
            return 0;
        }
        if (world.getTileEntity(tileIlluminator.getController()) instanceof IFixture) {
            IFixture fixture = (IFixture) world.getTileEntity(tileIlluminator.getController());
            if (fixture != null) {
                float val = (fixture.getIntensity() / 255F);
                int thing = (int) (val * 15F);
                return thing;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
