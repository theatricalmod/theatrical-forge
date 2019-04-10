package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.TheatricalConfig;
import com.georlegacy.general.theatrical.api.ISupport;
import com.georlegacy.general.theatrical.api.fixtures.Fixture;
import com.georlegacy.general.theatrical.api.fixtures.HangableType;
import com.georlegacy.general.theatrical.api.fixtures.IFixture;
import com.georlegacy.general.theatrical.api.fixtures.IFixtureModelProvider;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.base.BlockIlluminator;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.packets.UpdateIlluminatorPacket;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract  class TileFixture extends TileEntity implements IFixture, ITickable,
    IFixtureModelProvider {

    private Fixture fixture;
    private double distance = 0;
    private int pan, tilt = 0;
    private int focus = 6;

    private long timer = 0;

    public int prevTilt, prevPan, prevFocus = 0;

    private BlockPos lightBlock;

    public TileFixture(){}

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setInteger("pan", this.pan);
        nbtTagCompound.setInteger("tilt", this.tilt);
        nbtTagCompound.setInteger("focus", this.focus);
        nbtTagCompound.setInteger("prevPan", prevPan);
        nbtTagCompound.setInteger("prevTilt", prevTilt);
        nbtTagCompound.setInteger("prevFocus", prevFocus);
        nbtTagCompound.setLong("timer", timer);
        nbtTagCompound.setDouble("distance", distance);
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound) {
        pan = nbtTagCompound.getInteger("pan");
        tilt = nbtTagCompound.getInteger("tilt");
        focus = nbtTagCompound.getInteger("focus");
        prevPan = nbtTagCompound.getInteger("prevPan");
        prevTilt = nbtTagCompound.getInteger("prevTilt");
        prevFocus = nbtTagCompound.getInteger("prevFocus");
        timer = nbtTagCompound.getLong("timer");
        distance = nbtTagCompound.getDouble("distance");
    }

    public int getPan() {
        return pan;
    }

    public void setPan(int pan) {
        this.pan = pan;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    public BlockPos getLightBlock() {
        return lightBlock;
    }

    public void setLightBlock(BlockPos lightBlock) {
        this.lightBlock = lightBlock;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        readNBT(compound);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = getNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getNBT(null));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        nbtTagCompound = getNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readNBT(tag);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        int prevLightValue = (int) (getIntensity() * 15F / 255F);
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
        int newLightValue = (int) (getIntensity() * 15F / 255F);
        if (prevLightValue != newLightValue) {
            if (world != null && lightBlock != null) {
                world.checkLightFor(EnumSkyBlock.BLOCK, lightBlock);
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState,
        IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public final Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    if ((!(block instanceof ISupport) && !(block instanceof BlockHangable))) {
                        RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);

                        if (raytraceresult != null) {
                            return raytraceresult;
                        }
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getBlock() instanceof ISupport || iblockstate1.getBlock() instanceof BlockHangable || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if (raytraceresult1 != null) {
                                return raytraceresult1;
                            }
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public double doRayTrace() {
        if (!this.getBlock().isInstance(world.getBlockState(pos).getBlock())) {
            return 0;
        }
        EnumFacing direction = world.getBlockState(pos).getValue(
            BlockDirectional.FACING);
        float horizontalAngle = direction.getOpposite().getHorizontalAngle();
        float lookingAngle = -(horizontalAngle + getPan());
        float tilt = getTilt() + getDefaultRotation() + getRayTraceRotation();
        Vec3d look = getVectorForRotation(-tilt, lookingAngle);
        double distance = getMaxLightDistance();
        BlockPos start = pos.add( 0.5 + (look.x * 0.65) , 0.5 + (look.y * 0.65), 0.5 + (look.z * 0.65));
        BlockPos blockPos = start.add(look.x * distance, look.y * distance, look.z * distance);
        RayTraceResult result = rayTraceBlocks(new Vec3d(start), new Vec3d(blockPos), false, true, false);
        BlockPos lightPos = blockPos;
        if (result != null) {
            distance = result.hitVec.distanceTo(new Vec3d(pos));
            if (!result.getBlockPos().equals(pos)) {
                if (!result.getBlockPos().equals(getLightBlock())) {
                    lightPos = result.getBlockPos().offset(direction.getOpposite(), 1);
                } else {
                    return distance;
                }
            }
        }
        if (lightPos.equals(pos)) {
            return distance;
        }
        if (!(world.getBlockState(lightPos).getBlock() instanceof BlockAir) && !(world
            .getBlockState(lightPos).getBlock() instanceof BlockIlluminator)) {
            lightPos = lightPos.offset(EnumFacing.getFacingFromVector((float)look.x, (float)look.y, (float)look.z), 1);
        }
        distance = new Vec3d(lightPos).distanceTo(new Vec3d(pos));
        if (lightPos.equals(lightBlock)) {
            if (world.getBlockState(lightBlock).getBlock() instanceof BlockAir) {
                if (TheatricalConfig.general.emitLight && this.emitsLight()) {
                    world.setBlockState(lightPos,
                        TheatricalBlocks.BLOCK_ILLUMINATOR.getDefaultState(), 3);
                    TileEntity tileEntity = world.getTileEntity(lightPos);
                    if (tileEntity != null) {
                        TileIlluminator illuminator = (TileIlluminator) tileEntity;
                        illuminator.setController(pos);
                        if (world.isRemote) {
                            TheatricalPacketHandler.INSTANCE
                                .sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
                        }
                    }
                }
            }
            return distance;
        }
        if (getLightBlock() != null && getLightBlock() != lightPos && world.getBlockState(getLightBlock()) instanceof BlockIlluminator) {
            world.setBlockToAir(getLightBlock());
        }
        if((!(world.getBlockState(lightPos).getBlock() instanceof BlockAir) && !(world.getBlockState(lightPos).getBlock() instanceof BlockIlluminator))){
            return distance;
        }
        setLightBlock(lightPos);
        if (TheatricalConfig.general.emitLight && this.emitsLight()) {
            world.setBlockState(lightPos,
                TheatricalBlocks.BLOCK_ILLUMINATOR.getDefaultState(), 3);
            TileEntity tileEntity = world.getTileEntity(lightPos);
            if (tileEntity != null) {
                TileIlluminator illuminator = (TileIlluminator) tileEntity;
                illuminator.setController(pos);
                if (lightBlock != null) {
                    world.checkLightFor(EnumSkyBlock.BLOCK, lightBlock);
                }
                if (world.isRemote) {
                    TheatricalPacketHandler.INSTANCE
                        .sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
                }
            }
        }
        return distance;
    }

    @Override
    public void update() {
        prevFocus = focus;
        prevPan = pan;
        prevTilt = tilt;
        timer++;
        if (timer >= 20) {
            if (shouldTrace()) {
                this.distance = doRayTrace();
                markDirty();
            }
            timer = 0;
        }
    }

    @Override
    public float getIntensity() {
        return 0;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return null;
    }

    @Override
    public float getMaxLightDistance() {
        return 10;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public HangableType getHangType() {
        return getFixture().getHangableType();
    }

    @Override
    public IBakedModel getStaticModel() {
        Block block = getWorld().getBlockState(pos).getBlock();
        if (block instanceof BlockHangable && ((BlockHangable) block).isHanging(world, pos)) {
            return getFixture().getHookedModel();
        }
        return getFixture().getStaticModel();
    }

    @Override
    public IBakedModel getTiltModel() {
        return getFixture().getTiltModel();
    }

    @Override
    public IBakedModel getPanModel() {
        return getFixture().getPanModel();
    }

    @Override
    public float[] getTiltRotationPosition() {
        if (getFixture() == null) {
            return new float[]{};
        }
        return getFixture().getTiltRotationPosition();
    }

    @Override
    public float[] getPanRotationPosition() {
        if (getFixture() == null) {
            return new float[]{};
        }
        return getFixture().getPanRotationPosition();
    }

    @Override
    public float getDefaultRotation() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getDefaultRotation();
    }

    @Override
    public float[] getBeamStartPosition() {
        if (getFixture() == null) {
            return new float[]{};
        }
        return getFixture().getBeamStartPosition();
    }

    @Override
    public float getBeamWidth() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getBeamWidth();
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getRayTraceRotation();
    }



}
