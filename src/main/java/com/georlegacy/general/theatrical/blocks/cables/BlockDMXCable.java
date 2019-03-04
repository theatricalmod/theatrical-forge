package com.georlegacy.general.theatrical.blocks.cables;

import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.cables.TileDMXCable;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockDMXCable extends BlockBase implements ITileEntityProvider, IHasTileEntity {

    public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[1 << 6];

    static {
        double d0 = 7 / 16D;
        double d1 = 1D - d0;
        for (int i = 0; i < BOXES.length; i++)
        {
            boolean x0 = (i & (1 << EnumFacing.WEST.getIndex())) != 0;
            boolean x1 = (i & (1 << EnumFacing.EAST.getIndex())) != 0;
            boolean z0 = (i & (1 << EnumFacing.NORTH.getIndex())) != 0;
            boolean z1 = (i & (1 << EnumFacing.SOUTH.getIndex())) != 0;
            BOXES[i] = new AxisAlignedBB(x0 ? 0D : d0, 0D, z0 ? 0D : d0, x1 ? 1D : d1, 0.05D, z1 ? 1D : d1);
        }
    }

    public static final IUnlistedProperty<TileDMXCable> CABLE = new IUnlistedProperty<TileDMXCable>() {
        @Override
        public String getName() {
            return "cable";
        }

        @Override
        public boolean isValid(TileDMXCable value) {
            return true;
        }

        @Override
        public Class<TileDMXCable> getType() {
            return  TileDMXCable.class;
        }

        @Override
        public String valueToString(TileDMXCable value) {
            return TileEntity.getKey(value.getClass()).getPath();
        }
    };

    public BlockDMXCable() {
        super("dmx_cable");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileDMXCable.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDMXCable();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess,
        BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {CABLE});
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof TileDMXCable)
            {
                return ((IExtendedBlockState) state).withProperty(CABLE, (TileDMXCable) tileEntity);
            }
        }

        return state;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileDMXCable)
        {
            TileDMXCable pipe = (TileDMXCable) tileEntity;

            int id = 0;

            for (int i = 0; i < 6; i++)
            {
                if (pipe.isConnected(EnumFacing.VALUES[i]))
                {
                    id |= 1 << i;
                }
            }

            return BOXES[id];
        }

        return BOXES[0];
    }
    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[0]);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileDMXCable)
        {
            TileDMXCable pipe = (TileDMXCable) tileEntity;

            for (int i = 0; i < 6; i++)
            {
                if (pipe.isConnected(EnumFacing.VALUES[i]))
                {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[1 << i]);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if(playerIn.isSneaking() && !worldIn.isRemote){
            if(worldIn.getTileEntity(pos) instanceof TileDMXCable){
                TileDMXCable tileDMXCable = (TileDMXCable) worldIn.getTileEntity(pos);
                if(tileDMXCable.getUniverse() != null) {
                    System.out.println(tileDMXCable.getUniverse().getUuid().toString());
                    System.out.println(tileDMXCable.getUniverse().getDMXChannels());
                }
            }
            return true;
        }
        return false;
    }
}
