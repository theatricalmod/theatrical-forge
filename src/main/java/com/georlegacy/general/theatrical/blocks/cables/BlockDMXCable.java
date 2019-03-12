package com.georlegacy.general.theatrical.blocks.cables;

import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.tiles.cables.TileDMXCable;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDMXCable extends Block implements ITileEntityProvider, IHasTileEntity {

    public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[6];

    static {
        double h0 = 1D / 16D;
        double h1 = 1D - h0;

        double v0 = 1D / 16D;
        double v1 = 1D - v0;

        BOXES[0] = new AxisAlignedBB(h0, 0D, h0, h1, v0, h1);
        BOXES[1] = new AxisAlignedBB(h0, v1, h0, h1, 1D, h1);
        BOXES[2] = new AxisAlignedBB(h0, h0, 0D, h1, h1, v0);
        BOXES[3] = new AxisAlignedBB(h0, h0, v1, h1, h1, 1D);
        BOXES[4] = new AxisAlignedBB(0D, h0, h0, v0, h1, h1);
        BOXES[5] = new AxisAlignedBB(v1, h0, h0, 1D, h1, h1);
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
        super(Material.ROCK);
        setTranslationKey("dmx_cable_block");
        setRegistryName("dmx_cable_block");
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
    @Nullable
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;

        if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
        {
            return BOXES[ray.subHit].offset(pos);
        }

        return super.getSelectedBoundingBox(state, worldIn, pos);
    }

    @Override
    @Nullable
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (!(tileEntity instanceof TileDMXCable))
        {
            return super.collisionRayTrace(state, world, pos, start, end);
        }

        TileDMXCable tile = (TileDMXCable) tileEntity;

        if(!hasSide(tile)){
            return super.collisionRayTrace(state, world, pos, start, end);
        }

        Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult ray1 = null;
        double dist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < BOXES.length; i++)
        {
            if (!tile.sides[i])
            {
                continue;
            }

            RayTraceResult ray = BOXES[i].calculateIntercept(start1, end1);

            if (ray != null)
            {
                double dist1 = ray.hitVec.squareDistanceTo(start1);

                if (dist >= dist1)
                {
                    dist = dist1;
                    ray1 = ray;
                    ray1.subHit = i;
                }
            }
        }

        if (ray1 != null)
        {
            RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
            ray2.subHit = ray1.subHit;
            return ray2;
        }

        return null;
    }

    public boolean hasSide(TileDMXCable dmxCable){
        for(boolean bool : dmxCable.sides){
            if(bool){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileDMXCable)
        {
            TileDMXCable tile = (TileDMXCable) tileEntity;
            double dist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
            Vec3d start = player.getPositionEyes(1F);
            Vec3d look = player.getLookVec();
            Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
            RayTraceResult ray = collisionRayTrace(state, world, pos, start, end);
            EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

            if (side != null)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    spawnAsEntity(world, pos, new ItemStack(ItemBlock.getItemFromBlock(this)));
                }

                tile.sides[side.getIndex()] = false;

                if (hasSide(tile))
                {
                    world.notifyBlockUpdate(pos, state, state, 11);
                }
                else
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult ray, World world, BlockPos pos, EntityPlayer player)
    {
        EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

        if (side != null)
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof TileDMXCable)
            {
                TileDMXCable tile = (TileDMXCable) tileEntity;

                if (tile.sides[side.getIndex()])
                {
                    return new ItemStack(TheatricalItems.ITEM_DMX_CABLE);
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }
}
