package dev.theatricalmod.theatrical.tiles.cables;

import net.minecraft.nbt.NBTTagCompound;

public class CableSide {

    private CableType[] types;

    public CableSide(){
        this.types = new CableType[]{CableType.NONE, CableType.NONE, CableType.NONE, CableType.NONE, CableType.NONE};
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

    public NBTTagCompound getNBT(){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        int[] types = new int[5];
        for(int i = 0; i < this.types.length; i++){
            types[i] = this.types[i].getIndex();
        }
        nbtTagCompound.setIntArray("types", types);
        return nbtTagCompound;
    }

    public static CableSide readNBT(NBTTagCompound nbtTagCompound){
        CableSide side = new CableSide();
        CableType[] cableTypes = new CableType[5];
        if(nbtTagCompound.hasKey("types")){
            int[] types = nbtTagCompound.getIntArray("types");
            for(int i = 0; i < types.length; i++){
                cableTypes[i] = CableType.byIndex(types[i]);
            }
        }
        side.setTypes(cableTypes);
        return side;
    }
}
