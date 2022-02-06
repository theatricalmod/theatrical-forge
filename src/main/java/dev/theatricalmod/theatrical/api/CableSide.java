package dev.theatricalmod.theatrical.api;


import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CableSide {

    private CableType[] types;
    private BlockStateVariantBuilder.ITriFunction<Direction, CableType, Integer, Boolean> isConnected;

    public CableSide(BlockStateVariantBuilder.ITriFunction<Direction, CableType, Integer, Boolean> isConnected){
        this.isConnected = isConnected;
        this.types = new CableType[]{CableType.NONE, CableType.NONE, CableType.NONE, CableType.NONE, CableType.NONE};
    }

    public boolean isConnected(Direction direction, CableType cableType, int side) {
        return isConnected.apply(direction, cableType, side);
    }

    public CableType[] getTypes() {
        return types;
    }

    public void setTypes(CableType[] types) {
        this.types = types;
    }

    public void setSlotToType(CableType toType, int slot) {
        if (!hasTypeInSlot(slot)) {
            types[slot] = toType;
        }
    }

    public int getTotalTypes(){
        int count = 0;
        for(int i = 0; i < types.length; i++){
            if(types[i].getIndex() != CableType.NONE.getIndex()){
                count++;
            }
        }
        return count;
    }

    public boolean hasTypeInSlot(int i){
        return types[i].getIndex() != CableType.NONE.getIndex();
    }

    public boolean hasAnyType(CableType[] types) {
        for (CableType type : types) {
            if (type != CableType.NONE) {
                if (this.hasType(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getSlotForType(CableType type) {
        for (int i = 0; i < 5; i++) {
            if (types[i].getIndex() == type.getIndex()) {
                return i;
            }
        }
        return -1;
    }

    public CableType getFirstType() {
        for (CableType type : types) {
            if (type != CableType.NONE) {
                return type;
            }
        }
        return null;
    }

    public boolean hasType(CableType type){
        for(CableType type1 : types){
            if(type1.getIndex() == type.getIndex()){
                return true;
            }
        }
        return false;
    }

    public boolean addType(CableType type){
        if(!hasType(type)){
            int availableSlot = -1;
            for(int i = 0; i < types.length; i++){
                if(types[i].getIndex() == CableType.NONE.getIndex()){
                    availableSlot = i;
                    break;
                }
            }
            if(availableSlot != -1){
                types[availableSlot] = type;
                return true;
            }
        }
        return false;
    }

    public boolean removeType(CableType type){
        if(hasType(type)){
            int foundSlot = -1;
            for(int i = 0; i < types.length; i++){
                if(types[i].getIndex() == type.getIndex()){
                    foundSlot = i;
                    break;
                }
            }
            if(foundSlot != -1){
                types[foundSlot] = CableType.NONE;
                return true;
            }
        }
        return false;
    }

    public CompoundNBT getNBT(){
        CompoundNBT nbtTagCompound = new CompoundNBT();
        int[] types = new int[5];
        for(int i = 0; i < this.types.length; i++){
            types[i] = this.types[i].getIndex();
        }
        nbtTagCompound.putIntArray("types", types);
        return nbtTagCompound;
    }

}
