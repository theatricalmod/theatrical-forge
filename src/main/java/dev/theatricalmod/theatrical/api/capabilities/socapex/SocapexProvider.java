package dev.theatricalmod.theatrical.api.capabilities.socapex;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.block.cables.BlockCable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
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
    private HashMap<Direction, BlockPos> devices = null;
    private int[] channels = new int[8];
    private HashMap<Integer, SocapexPatch[]> patch = new HashMap<>();

    public void addToList(HashMap<Direction, BlockPos> scanned, World world, BlockPos pos, Direction facing, Direction connectionSide) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockCable && ((BlockCable) blockState.getBlock()).getCableType() == CableType.SOCAPEX) {
            for (Direction direction : Direction.values()) {
                if(direction != facing) {
                    if (((BlockCable) blockState.getBlock()).canConnect(world, pos, direction)) {
                        addToList(scanned, world, pos.offset(direction), direction.getOpposite(), connectionSide);
                    }
                }
            }
        } else {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && (tileEntity.getCapability(SocapexReceiver.CAP, facing).isPresent())) {
                if (!scanned.containsKey(connectionSide)) {
                    scanned.put(connectionSide, pos);
                }
            }
        }
    }

    @Override
    public void updateDevices(World world, BlockPos controllerPos) {
        if (devices == null) {
            if (world.isRemote) {
                devices = new HashMap<>();
                return;
            }
            HashMap<Direction, BlockPos> receivers = new HashMap<>();
            for (Direction facing : Direction.values()) {
                addToList(receivers, world, controllerPos.offset(facing), facing.getOpposite(), facing);
            }
            devices = new HashMap<Direction, BlockPos>(receivers);
        }
        for (Direction direction : devices.keySet()) {
            BlockPos provider = devices.get(direction);
            BlockState blockState = world.getBlockState(provider);
            if (blockState.getBlock() instanceof BlockCable) {
                continue;
            }
            TileEntity tile = world.getTileEntity(provider);
            if (tile != null) {
               tile.getCapability(SocapexReceiver.CAP, blockState.get(BlockStateProperties.FACING)).ifPresent(iSocapexReceiver1 -> {
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
        if (!devices.containsValue(receiver.getPos())) {
            return;
        }
        SocapexPatch[] patches;
        if (patch.containsKey(dmxChannel)) {
            patches = patch.get(dmxChannel);
        } else {
            patches = new SocapexPatch[2];
        }
        patches[patchSocket - 1] = new SocapexPatch(receiver.getPos(), receiverSocket);
        patch.put(dmxChannel, patches);
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
    public String getIdentifier(BlockPos pos) {
        return devices.containsValue(pos) ? devices.entrySet().stream().filter(directionBlockPosEntry -> directionBlockPosEntry.getValue().equals(pos)).findFirst().get().getKey().getName() : "";
    }

    @Override
    public List<ISocapexReceiver> getDevices(World world, BlockPos controller) {
        List<ISocapexReceiver> receivers = new ArrayList<>();
        if (world.isRemote || devices == null) {
            HashMap<Direction, BlockPos> blockPos = new HashMap<>();
            for (Direction facing : Direction.values()) {
                addToList(blockPos, world, controller.offset(facing), facing.getOpposite(), facing);
            }
            devices = new HashMap<>(blockPos);
            updateDevices(world, controller);
        }
        for (Direction direction : devices.keySet()) {
            BlockPos pos = devices.get(direction);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                tileEntity.getCapability(SocapexReceiver.CAP, world.getBlockState(pos).get(BlockStateProperties.FACING)).ifPresent(receivers::add);
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

