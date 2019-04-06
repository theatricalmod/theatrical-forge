package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.WorldSocapexNetwork;
import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.power.TheatricalPower;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexReceiver;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.rigging.TilePipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TilePipePanel extends TileEntity implements IAcceptsCable, ITickable {

    private IBlockState state = Blocks.AIR.getDefaultState();

    private ArrayList<BlockPos> pipes = new ArrayList<>();

    private int ticks = 0;
    private SocapexReceiver socapexReceiver;

    public TilePipePanel() {
        socapexReceiver = new SocapexReceiver(this.pos);
    }

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setInteger("skin", Block.getStateId(state));
        nbtTagCompound.setTag("socapex", socapexReceiver.serializeNBT());
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound) {
        state = Block.getStateById(nbtTagCompound.getInteger("skin"));
        if (nbtTagCompound.hasKey("socapex")) {
            socapexReceiver.deserializeNBT(nbtTagCompound.getCompoundTag("socapex"));
        }
    }

    public IBlockState getState() {
        return state;
    }

    public void setState(IBlockState state) {
        this.state = state;
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        super.onDataPacket(net, pkt);
    }

    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, EnumFacing facing) {
        if (scanned.size() >= 8) {
            return;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TilePipe) {
            if (scanned.add(pos)) {
                for (EnumFacing facing1 : EnumFacing.VALUES) {
                    if (facing1 != facing) {
                        addToList(scanned, world, pos.offset(facing1), facing1.getOpposite());
                    }
                }
            }
        }
    }

    public void sendPower() {
        if (pipes == null) {
            findPipes();
        }
        int[] extractedPower = new int[8];
        for (int i = 0; i < 8; i++) {
            if (i > (pipes.size() - 1)) {
                socapexReceiver.extractSocapex(extractedPower, false);
                return;
            }
            BlockPos provider = pipes.get(i);
            TileEntity tile = world.getTileEntity(provider);
            if (tile != null && (tile instanceof TilePipe)) {
                ITheatricalPowerStorage powerStorage = tile.getCapability(TheatricalPower.CAP, null);
                if (powerStorage != null) {
                    if (powerStorage.getEnergyStored() < socapexReceiver.getEnergyStored(i)) {
                        powerStorage.receiveEnergy(socapexReceiver.getEnergyStored(i), false);
                        extractedPower[i] = socapexReceiver.getEnergyStored(i);
                    }
                }
            }
        }
        socapexReceiver.extractSocapex(extractedPower, false);
    }

    public void refreshDevices() {
        pipes = null;
    }

    public void findPipes() {
        if (pipes == null) {
            if (world.isRemote) {
                pipes = new ArrayList<>();
                return;
            }
            HashSet<BlockPos> receivers = new HashSet<>();
            for (EnumFacing facing : EnumFacing.VALUES) {
                addToList(receivers, world, pos.offset(facing), facing.getOpposite());
            }
            pipes = new ArrayList<>(receivers);
            Collections.reverse(pipes);
        }
        sendPower();
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.SOCAPEX};
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == SocapexReceiver.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == SocapexReceiver.CAP) {
            return SocapexReceiver.CAP.cast(socapexReceiver);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        sendPower();
    }


    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldSocapexNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldSocapexNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }

}
