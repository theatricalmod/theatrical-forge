package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IFixture;
import com.georlegacy.general.theatrical.api.IFixtureModelProvider;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.base.BlockIlluminator;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.packets.UpdateIlluminatorPacket;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
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


    private double distance = 0;
    private int pan, tilt = 0;
    private int focus = 6;
    private float power = 255;

    private long timer = 0;

    public int prevTilt, prevPan, prevFocus = 0;

    private BlockPos lightBlock;

    public TileFixture(){}

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
        nbtTagCompound.setFloat("power", power);
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
        power = nbtTagCompound.getFloat("power");
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
        int prevLightValue = (int) (power * 15F / 255F);
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
        int newLightValue = (int) (power * 15F / 255F);
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

    public double doRayTrace() {
        if (!this.getBlock().isInstance(world.getBlockState(pos).getBlock())) {
            return 0;
        }
        EnumFacing direction = world.getBlockState(pos).getValue(
            BlockDirectional.FACING);
        float horizontalAngle = direction.getOpposite().getHorizontalAngle();
        float lookingAngle = -(horizontalAngle + getPan());
        float tilt = getTilt() + getDefaultRotation();
        Vec3d look = getVectorForRotation(-tilt, lookingAngle);
        double distance = getMaxLightDistance();
        BlockPos start = pos.add( 0.5 + (look.x * 0.65) , 0.5 + (look.y * 0.65), 0.5 + (look.z * 0.65));
        BlockPos blockPos = start.add(look.x * distance, look.y * distance, look.z * distance);
        RayTraceResult result = world
            .rayTraceBlocks(new Vec3d(start), new Vec3d(blockPos), false, true, false);
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
            return distance;
        }
        if (getLightBlock() != null && getLightBlock() != lightPos && world.getBlockState(getLightBlock()) instanceof BlockIlluminator) {
            world.setBlockToAir(getLightBlock());
        }
        if((!(world.getBlockState(lightPos).getBlock() instanceof BlockAir) && !(world.getBlockState(lightPos).getBlock() instanceof BlockIlluminator))){
            return distance;
        }
        setLightBlock(lightPos);
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
        return distance;
    }

    @Override
    public void update() {
        prevFocus = focus;
        prevPan = pan;
        prevTilt = tilt;
        timer++;
        if (timer >= 20) {
            this.distance = doRayTrace();
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


}
