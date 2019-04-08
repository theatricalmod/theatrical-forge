package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.WorldSocapexNetwork;
import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.power.TheatricalPower;
import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexReceiver;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexReceiver;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.rigging.TileSocapexPipe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TilePipePanel extends TileEntity implements IAcceptsCable, ITickable, ISocapexReceiver {

    private IBlockState state = Blocks.AIR.getDefaultState();

    private ArrayList<BlockPos> pipes = new ArrayList<>();

    private int ticks = 0;
    private int[] channels;
    private String identifier;

    public TilePipePanel() {
        channels = new int[8];
    }

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setInteger("skin", Block.getStateId(state));
        if (identifier != null) {
            nbtTagCompound.setString("identifier", identifier);
        }
        for (int i = 0; i < channels.length; i++) {
            nbtTagCompound.setInteger("channel_" + i, channels[i]);
        }
        nbtTagCompound.setTag("pos", NBTUtil.createPosTag(pos));
        if (pipes != null) {
            NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
            for (int i = 0; i < pipes.size(); i++) {
                nbtTagCompound1.setTag("pipes_" + i, NBTUtil.createPosTag(pipes.get(i)));
            }
            nbtTagCompound.setTag("pipes", nbtTagCompound1);
        }
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound) {
        state = Block.getStateById(nbtTagCompound.getInteger("skin"));
        if (nbtTagCompound.hasKey("identifier")) {
            this.identifier = nbtTagCompound.getString("identifier");
        }
        int[] channels = new int[8];
        for (int i = 0; i < 8; i++) {
            if (nbtTagCompound.hasKey("channel_" + i)) {
                channels[i] = nbtTagCompound.getInteger("channel_" + i);
            }
        }
        if (nbtTagCompound.hasKey("pos")) {
            pos = NBTUtil.getPosFromTag(nbtTagCompound.getCompoundTag("pos"));
        }
        if (nbtTagCompound.hasKey("pipes")) {
            pipes.clear();
            NBTTagCompound tagCompound = nbtTagCompound.getCompoundTag("pipes");
            for (String s : tagCompound.getKeySet()) {
                pipes.add(NBTUtil.getPosFromTag(tagCompound.getCompoundTag(s)));
            }
        }
        this.channels = channels;
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
        readNBT(tag);
        super.onDataPacket(net, pkt);
    }

    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, EnumFacing facing) {
        if (scanned.size() >= 8) {
            return;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileSocapexPipe) {
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
                extractSocapex(extractedPower, false);
                return;
            }
            BlockPos provider = pipes.get(i);
            TileEntity tile = world.getTileEntity(provider);
            if (tile != null && (tile instanceof TileSocapexPipe)) {
                ITheatricalPowerStorage powerStorage = tile.getCapability(TheatricalPower.CAP, null);
                if (powerStorage != null) {
                    if (powerStorage.getEnergyStored() < getEnergyStored(i)) {
                        powerStorage.receiveEnergy(getEnergyStored(i), false);
                        extractedPower[i] = getEnergyStored(i);
                    }
                }
            }
        }
        extractSocapex(extractedPower, false);
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
            pipes.sort((o1, o2) -> {
                double dis1 = pos.getDistance(o1.getX(), o1.getY(), o1.getZ());
                double dis2 = pos.getDistance(o2.getX(), o2.getY(), o2.getZ());
                return Double.compare(dis1, dis2);
            });
        }
        markDirty();
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
            return SocapexReceiver.CAP.cast(this);
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

    @Override
    public int[] receiveSocapex(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = Math.min(255 - this.channels[i], Math.min(1000, channels[i]));
            if (!simulate) {
                this.channels[i] = energyReceived[i];
            }
        }
        return energyReceived;
    }

    @Override
    public int[] extractSocapex(int[] channels, boolean simulate) {
        int[] energyExtracted = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canExtract(i)) {
                energyExtracted[i] = 0;
                continue;
            }
            energyExtracted[i] = Math.min(this.channels[i], Math.min(1000, channels[i]));
            if (!simulate) {
                this.channels[i] -= energyExtracted[i];
            }
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(int channel) {
        return this.channels[channel];
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return 255;
    }

    @Override
    public boolean canExtract(int channel) {
        return true;
    }

    @Override
    public boolean canReceive(int channel) {
        return true;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void assignIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public List<BlockPos> getDevices() {
        return pipes;
    }
}
