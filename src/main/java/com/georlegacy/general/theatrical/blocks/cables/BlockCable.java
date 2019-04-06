package com.georlegacy.general.theatrical.blocks.cables;

import com.georlegacy.general.theatrical.api.capabilities.WorldSocapexNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.integration.top.ITOPProvider;
import com.georlegacy.general.theatrical.tiles.cables.CableSide;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.cables.TileCable;
import java.util.Random;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
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

public class BlockCable extends Block implements ITileEntityProvider, IHasTileEntity, ITOPProvider {

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

    public static final IUnlistedProperty<TileCable> CABLE = new IUnlistedProperty<TileCable>() {
        @Override
        public String getName() {
            return "cable";
        }

        @Override
        public boolean isValid(TileCable value) {
            return true;
        }

        @Override
        public Class<TileCable> getType() {
            return  TileCable.class;
        }

        @Override
        public String valueToString(TileCable value) {
            return TileEntity.getKey(value.getClass()).getPath();
        }
    };

    public BlockCable() {
        super(Material.ROCK);
        setTranslationKey("dmx_cable");
        setRegistryName("dmx_cable");
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileCable.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCable();
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

            if (tileEntity instanceof TileCable)
            {
                return ((IExtendedBlockState) state).withProperty(CABLE, (TileCable) tileEntity);
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

        if (!(tileEntity instanceof TileCable))
        {
            return super.collisionRayTrace(state, world, pos, start, end);
        }

        TileCable tile = (TileCable) tileEntity;

        if(!hasSide(tile)){
            return super.collisionRayTrace(state, world, pos, start, end);
        }

        Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult ray1 = null;
        double dist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < BOXES.length; i++)
        {
            if (!tile.hasSide(i))
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

    public boolean hasSide(TileCable dmxCable){
        for(CableSide side : dmxCable.sides){
            if(side != null){
                return true;
            }
        }
        return false;
    }


    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileCable) {
            TileCable tile = (TileCable) tileEntity;
            for(int i = 0; i < 6; i++){
                if(tile.hasSide(i)){
                    BlockPos offset = pos.offset(EnumFacing.byIndex(i));
                    if(worldIn.getBlockState(offset).getBlock() instanceof BlockAir){
                        for (int x = 0; x < tile.sides[i].getTypes().length; x++) {
                            if (tile.sides[i].getTypes()[x] != CableType.NONE) {
                                spawnAsEntity(worldIn, pos, new ItemStack(CableType.getItemForCable(tile.sides[i].getTypes()[x])));
                            }
                        }
                        tile.sides[i] = null;
                        if (hasSide(tile))
                        {
                            worldIn.notifyBlockUpdate(pos, state, state, 11);
                            WorldDMXNetwork.getCapability(worldIn).setRefresh(true);
                            WorldSocapexNetwork.getCapability(worldIn).setRefresh(true);
                        }
                        else
                        {
                            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), worldIn.isRemote ? 11 : 3);
                            WorldDMXNetwork.getCapability(worldIn).setRefresh(true);
                            WorldSocapexNetwork.getCapability(worldIn).setRefresh(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TileCable)
        {
            TileCable tile = (TileCable) tileEntity;
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
                    spawnAsEntity(world, pos, new ItemStack(TheatricalItems.ITEM_DMX_CABLE));
                }

                tile.sides[side.getIndex()] = null;

                if (hasSide(tile))
                {
                    world.notifyBlockUpdate(pos, state, state, 11);
                    WorldDMXNetwork.getCapability(world).setRefresh(true);
                }
                else
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
                    WorldDMXNetwork.getCapability(world).setRefresh(true);
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

            if (tileEntity instanceof TileCable)
            {
                TileCable tile = (TileCable) tileEntity;

                if (tile.hasSide(side.getIndex()))
                {
                    if (tile.sides[side.getIndex()].getTotalTypes() == 1) {
                        return new ItemStack(CableType.getItemForCable(tile.sides[side.getIndex()].getFirstType()));
                    } else {
                        return new ItemStack(TheatricalItems.ITEM_BUNDLED_CABLE);
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        EnumFacing side = data.getSideHit().getOpposite();

        if (side != null) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());

            if (tileEntity instanceof TileCable) {
                TileCable tile = (TileCable) tileEntity;

                if (tile.hasSide(side.getIndex())) {
                    if (tile.sides[side.getIndex()].getTotalTypes() > 1) {
                        for (int i = 0; i < 5; i++) {
                            if (tile.sides[side.getIndex()].getTypes()[i] != CableType.NONE) {
                                probeInfo.text("#" + i + ": " + tile.sides[side.getIndex()].getTypes()[i].getName());
                            }
                        }
                    }
                }
            }
        }
    }
}
