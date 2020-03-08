package dev.theatricalmod.theatrical.api.capabilities.socapex;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.block.cables.CableBlockEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class SocapexProvider implements ISocapexProvider, INBTSerializable<CompoundNBT> {

    public static final String[] IDENTIFIERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @CapabilityInject(ISocapexProvider.class)
    public static Capability<ISocapexProvider> CAP;

    private int lastIdentifier = -1;
    private HashSet<BlockPos> devices = null;
    private int[] channels = new int[8];
    private HashMap<Integer, SocapexPatch[]> patch = new HashMap<>();

    public void addToList(HashSet<BlockPos> scanned, World world, BlockPos pos, Direction facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity.getCapability(SocapexReceiver.CAP, facing).isPresent()) {
            if (scanned.add(pos)) {
                if (tileEntity instanceof CableBlockEntity) {
                    CableBlockEntity cable = (CableBlockEntity) tileEntity;
                    if (cable.hasType(CableType.SOCAPEX) && cable.isConnected(facing, CableType.SOCAPEX)) {
                        addToList(scanned, world, cable.getPos(), facing.getOpposite());
                    }
                } else {
                    for (Direction facing1 : Direction.values()) {
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
            for (Direction facing : Direction.values()) {
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(receivers);
        }
        for (BlockPos provider : devices) {
            TileEntity tile = world.getTileEntity(provider);
            if (tile != null && !(tile instanceof CableBlockEntity)) {
                ISocapexReceiver iSocapexReceiver = tile.getCapability(SocapexReceiver.CAP, null).orElse(null);
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
    public SocapexPatch[] getPatch(int channel) {
        return patch.get(channel);
    }

    @Override
    public void patch(int dmxChannel, ISocapexReceiver receiver, int receiverSocket, int patchSocket) {
        if (!devices.contains(receiver.getPos())) {
            return;
        }
        if (patch.containsKey(dmxChannel)) {
            patch.get(dmxChannel)[patchSocket] = new SocapexPatch(receiver.getPos(), receiverSocket);
        } else {
            SocapexPatch[] patches = new SocapexPatch[2];
            patches[patchSocket] = new SocapexPatch(receiver.getPos(), receiverSocket);
            patch.put(dmxChannel, patches);
        }
    }

    @Override
    public boolean hasPatch(ISocapexReceiver receiver) {
        for (SocapexPatch[] patches : patch.values()) {
            for (SocapexPatch socapexPatch : patches) {
                if (socapexPatch != null && receiver.getPos().equals(socapexPatch.getReceiver())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void removePatch(int dmxChannel, int patchSocket) {
        if (patch.containsKey(dmxChannel)) {
            patch.get(dmxChannel)[patchSocket] = new SocapexPatch();
        }
    }

    @Override
    public int[] getChannelsForReceiver(ISocapexReceiver receiver) {
        int[] channels = new int[8];
        for (Integer i : patch.keySet()) {
            if (patch.containsKey(i)) {
                SocapexPatch[] patches = patch.get(i);
                if (patches != null && receiver != null) {
                    for (SocapexPatch socapexPatch : patches) {
                        if (socapexPatch != null && receiver.getPos().equals(socapexPatch.getReceiver())) {
                            channels[socapexPatch.getReceiverSocket()] = this.channels[i];
                        }
                    }
                }
            }
        }
        return channels;
    }


    @Override
    public int[] getPatchedCables(ISocapexReceiver socapexReceiver) {
        int[] channels = new int[8];
        for (Integer i : patch.keySet()) {
            if (patch.containsKey(i)) {
                SocapexPatch[] patches = patch.get(i);
                if (patches != null && socapexReceiver != null) {
                    for (SocapexPatch socapexPatch : patches) {
                        if (socapexPatch != null && socapexReceiver.getPos().equals(socapexPatch.getReceiver())) {
                            channels[socapexPatch.getReceiverSocket()] = 1;
                        }
                    }
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
            for (Direction facing : Direction.values()) {
                addToList(blockPos, world, controller.offset(facing), facing.getOpposite());
            }
            devices = new HashSet<>(blockPos);
            updateDevices(world, controller);
        }
        for (BlockPos pos : devices) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                ISocapexReceiver iSocapexReceiver = tileEntity.getCapability(SocapexReceiver.CAP, null).orElse(null);
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        nbtTagCompound.putInt("lastIdentifier", lastIdentifier);
        CompoundNBT patchTag = new CompoundNBT();
        for (Integer i : patch.keySet()) {
            if (patch.get(i) != null) {
                CompoundNBT tagCompound = new CompoundNBT();
                if (patch.get(i).length < 2) {
                    tagCompound.put("socket_0", patch.get(i)[0].serialize());
                } else if (patch.get(i).length < 3) {
                    if (patch.get(i) != null && patch.get(i)[1] != null) {
                        tagCompound.put("socket_1", patch.get(i)[1].serialize());
                    }
                }
                patchTag.put("patch_" + i, tagCompound);
            }
        }
        nbtTagCompound.put("patch", patchTag);
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("lastIdentifier")) {
            lastIdentifier = nbt.getInt("lastIdentifier");
        }
        if (nbt.contains("patch")) {
            patch.clear();
            CompoundNBT patchTag = nbt.getCompound("patch");
            for (int i = 0; i < 6; i++) {
                if (patchTag.contains("patch_" + i)) {
                    CompoundNBT patchTag1 = patchTag.getCompound("patch_" + i);
                    SocapexPatch[] patches = new SocapexPatch[2];
                    for (int x = 0; x < 2; x++) {
                        if (patchTag1.contains("socket_" + i)) {
                            SocapexPatch socapexPatch = new SocapexPatch();
                            socapexPatch.deserialize(patchTag1.getCompound("socket_" + i));
                            patches[x] = socapexPatch;
                        }
                    }
                    patch.put(i, patches);
                }
            }
        }
    }
}

