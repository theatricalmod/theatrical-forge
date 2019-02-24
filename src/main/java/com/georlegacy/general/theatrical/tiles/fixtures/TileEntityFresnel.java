/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.tiles.fixtures;

import com.georlegacy.general.theatrical.api.IFixture;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.base.BlockIlluminator;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.packets.UpdateIlluminatorPacket;
import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
import com.georlegacy.general.theatrical.tile.TileIlluminator;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")
public class TileEntityFresnel extends TileEntity implements IPeripheral, ITickable, IFixture {

    public static final int SIZE = 1;
    private GelType gelType = GelType.CLEAR;
    private int pan, tilt = 0;
    private int focus = 6;
    private double distance = 7;
    private float power = 0;

    private long timer = 0;

    public int prevTilt, prevPan, prevFocus = 0;

    private BlockPos lightBlock;

    // This item handler will hold our nine inventory slots
    private ItemStackHandler itemStackHandler = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // We need to tell the tile entity that something has changed so
            // that the chest contents is persisted
            TileEntityFresnel.this.markDirty();
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(slot).getMetadata());
        }
        @Override
        protected void onLoad() {
            TileEntityFresnel.this.markDirty();
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
    };

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound){
        if(nbtTagCompound == null){
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setTag("items", itemStackHandler.serializeNBT());
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

    public void readNBT(NBTTagCompound nbtTagCompound){
        if (nbtTagCompound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) nbtTagCompound.getTag("items"));
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
        pan = nbtTagCompound.getInteger("pan");
        tilt = nbtTagCompound.getInteger("tilt");
        focus = nbtTagCompound.getInteger("focus");
        prevPan = nbtTagCompound.getInteger("prevPan");
        prevTilt = nbtTagCompound.getInteger("prevTilt");
        prevFocus = nbtTagCompound.getInteger("prevFocus");
        timer = nbtTagCompound.getLong("timer");
        power = nbtTagCompound.getFloat("power");
    }


    public GelType getGelType() {
        return gelType;
    }

    public int getFocus() {
        return focus;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
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
    public SPacketUpdateTileEntity getUpdatePacket(){
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        float val = (power / 255F);
        int prevThing = (int) (val * 15F);
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
        float val2 = (power / 255F);
        int prevThing2 = (int) (val2 * 15F);
        if(prevThing2 != prevThing){
            if(world != null && lightBlock != null){
                world.checkLightFor(EnumSkyBlock.BLOCK, lightBlock);
            }
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState,
        IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        return super.getCapability(capability, facing);
    }

    public int getPan() {
        return pan;
    }

    public void setPan(int pan) {
        this.pan = pan;
        this.markDirty();
        if(!world.isRemote)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
        this.markDirty();
        if(!world.isRemote)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
    }

    public void setFocus(int focus) {
        this.focus = focus;
        this.markDirty();
        if(!world.isRemote)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
    }

    @Nonnull
    @Override
    public String getType() {
        return "fresnel";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{"setPan", "setTilt", "setFocus", "getPan", "getTilt", "getFocus", "setPower"};
    }

    @Method(modid = "computercraft")
    @Nullable
    @Override
    public Object[] callMethod(@Nonnull IComputerAccess cpu,
        @Nonnull ILuaContext ctx, int method, @Nonnull Object[] args)
        throws LuaException, InterruptedException {
        switch(method){
            case 0:
                this.setPan(((Number) args[0]).intValue());
            break;
            case 1:
                this.setTilt(((Number) args[0]).intValue());
            break;
            case 2:
                this.setFocus(((Number) args[0]).intValue());
            break;
            case 3:
                return new Object[]{this.getPan()};
            case 4:
                return new Object[]{this.getTilt()};
            case 5:
                return new Object[]{this.getFocus()};
            case 6:
                this.setPower(((Number) args[0]).floatValue());
            break;
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral != null && iPeripheral.getType().equals(this.getType());
    }

    public final Vec3d getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

    public double doRayTrace(){
        if(!(world.getBlockState(pos).getBlock() instanceof BlockFresnel)){
            return 0;
        }
        EnumFacing direction = world.getBlockState(pos).getValue(
            BlockDirectional.FACING);
        float horizontalAngle = direction.getOpposite().getHorizontalAngle();
        float lookingAngle = -(horizontalAngle + getPan());
        float tilt = getTilt();
        Vec3d look = getVectorForRotation(-tilt, lookingAngle);
        double distance = 7;
        BlockPos start = pos.offset(EnumFacing.getFacingFromAxis(direction.getAxisDirection(), direction.getAxis()), 1);
        BlockPos blockPos = start.add(look.x * distance, look.y * distance, look.z * distance);
        RayTraceResult result = world.rayTraceBlocks(new Vec3d(start), new Vec3d(blockPos), false, true, true);
        BlockPos lightPos = blockPos;
        if(result != null) {
            distance = result.hitVec.distanceTo(new Vec3d(pos));
            if(!result.getBlockPos().equals(pos)){
                if(!result.getBlockPos().equals(getLightBlock())){
                    lightPos = result.getBlockPos().offset(direction.getOpposite(), 1);
                }else {
                    return distance;
                }
            }
        }
        if(lightPos.equals(pos)) {
            return distance;
        }
        if(!(world.getBlockState(lightPos).getBlock() instanceof BlockAir) && !(world.getBlockState(lightPos).getBlock() instanceof BlockIlluminator)){
            lightPos = lightPos.add(0,1 ,0);
        }
        if(lightPos.equals(lightBlock)){
            if(world.getBlockState(lightBlock).getBlock() instanceof BlockAir){
                world.setBlockState(lightPos,
                    TheatricalBlocks.BLOCK_ILLUMINATOR.getDefaultState(), 3);
                TileEntity tileEntity = world.getTileEntity(lightPos);
                if(tileEntity != null){
                    TileIlluminator illuminator = (TileIlluminator) tileEntity;
                    illuminator.setController(pos);
                    if(world.isRemote)
                        TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
                }
            }
            return distance;
        }
        if(getLightBlock() != null && getLightBlock() != lightPos){
            world.setBlockToAir(getLightBlock());
        }
        setLightBlock(lightPos);
        world.setBlockState(lightPos,
            TheatricalBlocks.BLOCK_ILLUMINATOR.getDefaultState(), 3);
        TileEntity tileEntity = world.getTileEntity(lightPos);
        if(tileEntity != null){
            TileIlluminator illuminator = (TileIlluminator) tileEntity;
            illuminator.setController(pos);
            world.checkLightFor(EnumSkyBlock.BLOCK, lightBlock);
            if(world.isRemote)
                TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
        }
        return distance;
    }

    @Override
    public void update() {
        prevFocus = focus;
        prevPan = pan;
        prevTilt = tilt;
        timer++;
        if(timer >= 20){
            this.distance = doRayTrace();
            timer = 0;
        }
    }

    @Override
    public float getPower() {
        return power;
    }

    public BlockPos getLightBlock() {
        return lightBlock;
    }

    public void setLightBlock(BlockPos lightBlock) {
        this.lightBlock = lightBlock;
    }

    public double getDistance() {
        return distance;
    }

    public void setPower(float power) {
        this.power = power;
        this.markDirty();
        world.checkLightFor(EnumSkyBlock.BLOCK, lightBlock);
        if (!world.isRemote)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
    }
}
