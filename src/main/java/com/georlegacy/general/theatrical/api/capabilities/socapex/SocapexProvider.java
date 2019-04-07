package com.georlegacy.general.theatrical.api.capabilities.socapex;

import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.cables.TileCable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class SocapexProvider implements ISocapexProvider, INBTSerializable<NBTTagCompound> {

    public static final String[] IDENTIFIERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @CapabilityInject(ISocapexProvider.class)
    public static Capability<ISocapexProvider> CAP;

    private int lastIdentifier = -1;
    private HashSet<BlockPos> devices = null;
    private int[] channels = new int[8];
    private String[] patch = new String[6];

    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity.hasCapability(SocapexReceiver.CAP, facing)) {
            if (scanned.add(pos)) {
                if (tileEntity instanceof TileCable) {
                    TileCable cable = (TileCable) tileEntity;
                    for (int i = 0; i < 6; i++) {
                        if (cable.hasSide(i)) {
                            for (EnumFacing facing1 : EnumFacing.VALUES) {
                                if (facing1 != facing) {
                                        BlockPos connected = cable.isConnectedSides(facing1, i, CableType.SOCAPEX);
                                    if (connected != null) {
                                        addToList(scanned, world, connected, facing1.getOpposite());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing facing1 : EnumFacing.VALUES) {
                        if (facing1 != facing) {
                            addToList(scanned, world, pos.offset(facing1), facing1.getOpposite());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateDevices(World world, BlockPos controllerPos) {
        if (devices == null) {
            if (world.isRemote) {
                devices = new HashSet<>();
                return;
            }
            HashSet<BlockPos> receivers = new HashSet<>();
            for (EnumFacing facing : EnumFacing.VALUES) {
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(receivers);
        }
        for (BlockPos provider : devices) {
            TileEntity tile = world.getTileEntity(provider);
            if (tile != null && !(tile instanceof TileCable)) {
                ISocapexReceiver iSocapexReceiver = tile.getCapability(SocapexReceiver.CAP, null);
                if (iSocapexReceiver != null) {
                    if (iSocapexReceiver.getIdentifier() == null) {
                        if (lastIdentifier + 1 < IDENTIFIERS.length) {
                            lastIdentifier++;
                            iSocapexReceiver.assignIdentifier(IDENTIFIERS[lastIdentifier]);
                        } else {
                            lastIdentifier = -1;
                            iSocapexReceiver.assignIdentifier(IDENTIFIERS[0] + IDENTIFIERS[lastIdentifier++]);
                        }
                    }
                    if (hasPatch(iSocapexReceiver)) {
                        int[] drain = iSocapexReceiver.receiveSocapex(getChannelsForReceiver(iSocapexReceiver), false);
                        extractSocapex(drain, false);
                    }
                }
            }
        }
    }

    @Override
    public void refreshDevices() {
        devices = null;
    }

    @Override
    public int[] receiveSocapex(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (channels[i] > this.channels[i] && !canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = channels[i];
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
    public boolean canReceive(int channel) {
        return channels[channel] < 255;
    }

    @Override
    public boolean canExtract(int channel) {
        return channels[channel] > 0;
    }

    @Override
    public String getPatch(int channel) {
        return patch[channel];
    }

    @Override
    public void patch(int dmxChannel, ISocapexReceiver receiver, int socket) {
        if (!devices.contains(receiver.getPos())) {
            return;
        }
        patch[dmxChannel] = receiver.getIdentifier() + ":" + socket;
    }

    @Override
    public void patch(int dmxChannel, String patch) {
        this.patch[dmxChannel] = patch;
    }

    @Override
    public boolean hasPatch(ISocapexReceiver receiver) {
        for (String string : patch) {
            if (string != null && receiver.getIdentifier() != null && receiver.getIdentifier().equalsIgnoreCase(string.split(":")[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getChannelsForReceiver(ISocapexReceiver receiver) {
        int[] channels = new int[8];
        for (int i = 0; i < patch.length; i++) {
            String patchString = patch[i];
            if (patchString != null && receiver != null) {
                if (patchString.split(":")[0].equalsIgnoreCase(receiver.getIdentifier())) {
                    channels[Integer.parseInt(patchString.split(":")[1])] = this.channels[i];
                }
            }
        }
        return channels;
    }


    @Override
    public int[] getPatchedCables(ISocapexReceiver socapexReceiver) {
        int[] channels = new int[8];
        for (int i = 0; i < patch.length; i++) {
            String patchString = patch[i];
            if (patchString != null && socapexReceiver != null) {
                if (patchString.split(":")[0].equalsIgnoreCase(socapexReceiver.getIdentifier())) {
                    channels[Integer.parseInt(patchString.split(":")[1])] = 1;
                }
            }
        }
        return channels;
    }

    @Override
    public List<ISocapexReceiver> getDevices(World world, BlockPos controller) {
        List<ISocapexReceiver> receivers = new ArrayList<>();
        if (world.isRemote || devices == null) {
            HashSet<BlockPos> blockPos = new HashSet<>();
            for (EnumFacing facing : EnumFacing.VALUES) {
                addToList(blockPos, world, controller.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(blockPos);
            updateDevices(world, controller);
        }
        for (BlockPos pos : devices) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                ISocapexReceiver iSocapexReceiver = tileEntity.getCapability(SocapexReceiver.CAP, null);
                if (iSocapexReceiver != null) {
                    if (iSocapexReceiver.getIdentifier() == null) {
                        if (lastIdentifier + 1 < IDENTIFIERS.length) {
                            lastIdentifier++;
                            iSocapexReceiver.assignIdentifier(IDENTIFIERS[lastIdentifier]);
                        } else {
                            lastIdentifier = -1;
                            iSocapexReceiver.assignIdentifier(IDENTIFIERS[0] + IDENTIFIERS[lastIdentifier++]);
                        }
                    }
                    receivers.add(iSocapexReceiver);
                }
            }
        }
        return receivers;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("lastIdentifier", lastIdentifier);
        NBTTagCompound patchTag = new NBTTagCompound();
        for (int i = 0; i < patch.length; i++) {
            if (patch[i] != null) {
                patchTag.setString("patch_" + i, patch[i]);
            }
        }
        nbtTagCompound.setTag("patch", patchTag);
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("lastIdentifier")) {
            lastIdentifier = nbt.getInteger("lastIdentifier");
        }
        if (nbt.hasKey("patch")) {
            NBTTagCompound patchTag = nbt.getCompoundTag("patch");
            for (int i = 0; i < 6; i++) {
                if (patchTag.hasKey("patch_" + i)) {
                    patch[i] = patchTag.getString("patch_" + i);
                }
            }
        }
    }
}

