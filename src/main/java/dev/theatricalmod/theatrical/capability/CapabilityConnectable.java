package dev.theatricalmod.theatrical.capability;

import dev.theatricalmod.theatrical.api.ConnectableType;
import dev.theatricalmod.theatrical.api.IConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Arrays;

public class CapabilityConnectable implements IConnectable, INBTSerializable<CompoundTag> {

    BlockPos pos;
    ConnectableType[] allowedConnectionTypes;

    public CapabilityConnectable(BlockPos pos, ConnectableType[] allowedConnectionTypes) {
        this.pos = pos;
        this.allowedConnectionTypes = allowedConnectionTypes;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void setPos(BlockPos newPos) {
        this.pos = newPos;
    }

    @Override
    public ConnectableType[] getAllowedTypes() {
        return allowedConnectionTypes;
    }

    public void setAllowedConnectionTypes(ConnectableType[] types){
        this.allowedConnectionTypes = types;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        int[] allowedTypes = Arrays.stream(allowedConnectionTypes).mapToInt(Enum::ordinal).toArray();
        tag.putIntArray("allowedTypes", allowedTypes);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("pos")){
            this.pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
        }
        if(nbt.contains("allowedTypes")){
            int[] allowedTypes = nbt.getIntArray("allowedTypes");
            this.allowedConnectionTypes = (ConnectableType[]) Arrays.stream(allowedTypes).mapToObj(i -> ConnectableType.values()[i]).toArray();
        }
    }
}
