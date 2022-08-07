package dev.theatricalmod.theatrical.api.capabilities.socapex;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.block.cables.BlockCable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SocapexProvider implements ISocapexProvider, INBTSerializable<CompoundTag> {

    public static final String[] IDENTIFIERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int lastIdentifier = -1;
    private HashMap<Direction, BlockPos> devices = null;
    private final int[] channels = new int[6];
    private final HashMap<Integer, SocapexPatch[]> patch = new HashMap<>();

    public void addToList(HashMap<Direction, BlockPos> scanned, Level world, BlockPos pos, Direction facing, Direction connectionSide, HashSet<BlockPos> scannedCable) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockCable && ((BlockCable) blockState.getBlock()).getCableType() == CableType.SOCAPEX) {
            scannedCable.add(pos);
            for (Direction direction : Direction.values()) {
                if(direction != facing) {
                    if (((BlockCable) blockState.getBlock()).canConnect(world, pos, direction) && !scannedCable.contains(pos.relative(direction))) {
                        addToList(scanned, world, pos.relative(direction), direction.getOpposite(), connectionSide, scannedCable);
                    }
                }
            }
        } else {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null && (tileEntity.getCapability(SocapexReceiver.CAP, facing).isPresent())) {
                if (!scanned.containsKey(connectionSide)) {
                    scanned.put(connectionSide, pos);
                }
            }
        }
    }

    @Override
    public void updateDevices(Level world, BlockPos controllerPos) {
        if (devices == null) {
            if (world.isClientSide) {
                devices = new HashMap<>();
                return;
            }
            HashMap<Direction, BlockPos> receivers = new HashMap<>();
            HashSet<BlockPos> scannedCable = new HashSet<>();
            for (Direction facing : Direction.values()) {
                addToList(receivers, world, controllerPos.relative(facing), facing.getOpposite(), facing, scannedCable);
            }
            scannedCable.clear();
            devices = new HashMap<>(receivers);
        }
        for (Direction direction : devices.keySet()) {
            BlockPos provider = devices.get(direction);
            BlockState blockState = world.getBlockState(provider);
            if (blockState.getBlock() instanceof BlockCable) {
                continue;
            }
            BlockEntity tile = world.getBlockEntity(provider);
            if (tile != null) {
               tile.getCapability(SocapexReceiver.CAP, blockState.getValue(BlockStateProperties.FACING)).ifPresent(iSocapexReceiver1 -> {
                    if (hasPatch(iSocapexReceiver1)) {
                        int[] drain = iSocapexReceiver1.receiveSocapex(getChannelsForReceiver(iSocapexReceiver1), false);
                        extractSocapex(drain, false);
                    }
                });
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
        if(channel >= channels.length){
            return false;
        }
        return channels[channel] < 255;
    }

    @Override
    public boolean canExtract(int channel) {
        if(channel >= channels.length){
            return false;
        }
        return channels[channel] > 0;
    }

    @Override
    public SocapexPatch[] getPatch(int channel) {
        if(channel >= channels.length){
            return null;
        }
        return patch.get(channel);
    }

    @Override
    public void patch(int dmxChannel, ISocapexReceiver receiver, int receiverSocket, int patchSocket) {
        if (!devices.containsValue(receiver.getReceiverPos())) {
            return;
        }
        SocapexPatch[] patches;
        if (patch.containsKey(dmxChannel)) {
            patches = patch.get(dmxChannel);
        } else {
            patches = new SocapexPatch[2];
        }
        patches[patchSocket - 1] = new SocapexPatch(receiver.getReceiverPos(), receiverSocket);
        patch.put(dmxChannel, patches);
    }

    @Override
    public boolean hasPatch(ISocapexReceiver receiver) {
        for (SocapexPatch[] patches : patch.values()) {
            for (SocapexPatch socapexPatch : patches) {
                if (socapexPatch != null && receiver.getReceiverPos().equals(socapexPatch.getReceiver())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void removePatch(int dmxChannel, int patchSocket) {
        if (patch.containsKey(dmxChannel)) {
            patch.get(dmxChannel)[patchSocket - 1] = new SocapexPatch();
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
                        if (socapexPatch != null && receiver.getReceiverPos().equals(socapexPatch.getReceiver())) {
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
        if(socapexReceiver == null){
            return new int[0];
        }
        int[] channels = new int[socapexReceiver.getTotalChannels()];
        for (Integer i : patch.keySet()) {
            if (patch.containsKey(i)) {
                SocapexPatch[] patches = patch.get(i);
                if (patches != null) {
                    for (SocapexPatch socapexPatch : patches) {
                        if (socapexPatch != null && socapexReceiver.getReceiverPos().equals(socapexPatch.getReceiver())) {
                            channels[socapexPatch.getReceiverSocket()] = 1;
                        }
                    }
                }
            }
        }
        return channels;
    }

    @Override
    public String getIdentifier(BlockPos pos) {
        return devices.containsValue(pos) ? devices.entrySet().stream().filter(directionBlockPosEntry -> directionBlockPosEntry.getValue().equals(pos)).findFirst().get().getKey().getSerializedName() : "";
    }

    @Override
    public List<ISocapexReceiver> getDevices(Level world, BlockPos controller) {
        List<ISocapexReceiver> receivers = new ArrayList<>();
        if (world.isClientSide || devices == null) {
            HashMap<Direction, BlockPos> blockPos = new HashMap<>();
            HashSet<BlockPos> scannedCable = new HashSet<>();
            for (Direction facing : Direction.values()) {
                addToList(blockPos, world, controller.relative(facing), facing.getOpposite(), facing, scannedCable);
            }
            scannedCable.clear();
            devices = new HashMap<>(blockPos);
            updateDevices(world, controller);
        }
        for (Direction direction : devices.keySet()) {
            BlockPos pos = devices.get(direction);
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null) {
                tileEntity.getCapability(SocapexReceiver.CAP, world.getBlockState(pos).getValue(BlockStateProperties.FACING)).ifPresent(receivers::add);
            }
        }
        return receivers;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbtTagCompound = new CompoundTag();
        nbtTagCompound.putInt("lastIdentifier", lastIdentifier);
        CompoundTag patchTag = new CompoundTag();
        for (Integer i : patch.keySet()) {
            if (patch.get(i) != null) {
                CompoundTag tagCompound = new CompoundTag();
                if(patch.get(i)[0] != null){
                    tagCompound.put("socket_0", patch.get(i)[0].serialize());
                }
                if(patch.get(i)[1] != null){
                    tagCompound.put("socket_1", patch.get(i)[1].serialize());
                }
                patchTag.put("patch_" + i, tagCompound);
            }
        }
        nbtTagCompound.put("patch", patchTag);
        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("lastIdentifier")) {
            lastIdentifier = nbt.getInt("lastIdentifier");
        }
        if (nbt.contains("patch")) {
            patch.clear();
            CompoundTag patchTag = nbt.getCompound("patch");
            for (int i = 0; i < 6; i++) {
                if (patchTag.contains("patch_" + i)) {
                    CompoundTag patchTag1 = patchTag.getCompound("patch_" + i);
                    SocapexPatch[] patches = new SocapexPatch[2];
                    for (int x = 0; x < 2; x++) {
                        if (patchTag1.contains("socket_" + x)) {
                            SocapexPatch socapexPatch = new SocapexPatch();
                            socapexPatch.deserialize(patchTag1.getCompound("socket_" + x));
                            patches[x] = socapexPatch;
                        }
                    }
                    patch.put(i, patches);
                }
            }
        }
    }
}

