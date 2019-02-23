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

import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")
public class TileEntityFresnel extends TileEntity implements IPeripheral, ITickable {

    public static final int SIZE = 1;
    private GelType gelType = GelType.CLEAR;
    private int pan, tilt = 0;
    private int focus = 6;

    public int prevTilt, prevPan, prevFocus = 0;

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
        super.readFromNBT(compound);
        if (compound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
        pan = compound.getInteger("pan");
        tilt = compound.getInteger("tilt");
        focus = compound.getInteger("focus");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        compound.setInteger("pan", pan);
        compound.setInteger("tilt", tilt);
        compound.setInteger("focus", focus);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound nbtTag = new NBTTagCompound();
        //Write your data into the nbtTag
        nbtTag.setTag("items", itemStackHandler.serializeNBT());
        nbtTag.setInteger("tilt", tilt);
        nbtTag.setInteger("pan", pan);
        nbtTag.setInteger("focus", focus);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        nbtTagCompound.setTag("items", itemStackHandler.serializeNBT());
        nbtTagCompound.setInteger("pan", pan);
        nbtTagCompound.setInteger("tilt", tilt);
        nbtTagCompound.setInteger("focus", focus);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        if (tag.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) tag.getTag("items"));
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
        tilt = tag.getInteger("tilt");
        pan = tag.getInteger("pan");
        focus = tag.getInteger("focus");
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        NBTTagCompound tag = pkt.getNbtCompound();
        if (tag.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) tag.getTag("items"));
            gelType =  GelType.getGelType(itemStackHandler.getStackInSlot(0).getMetadata());
        }
        tilt = tag.getInteger("tilt");
        pan = tag.getInteger("pan");
        focus = tag.getInteger("focus");
        //Handle your Data
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState,
        IBlockState newSate) {
        return (oldState.getBlock() != newSate.getBlock());
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
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
        return new String[]{"setPan", "setTilt", "setFocus", "getPan", "getTilt", "getFocus"};
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
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral != null && iPeripheral.getType().equals(this.getType());
    }

    @Override
    public void update() {
        prevFocus = focus;
        prevPan = pan;
        prevTilt = tilt;
    }
}
