package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.util.ResourceLocation;

public enum CableType {

    NONE(-1, null),
    DMX(0, new ResourceLocation(Reference.MOD_ID, "blocks/cables/cable")),
    POWER(1, new ResourceLocation(Reference.MOD_ID, "blocks/cables/power"));

    private int index;
    private ResourceLocation texture;

    CableType(int index, ResourceLocation location){
        this.index = index;
        this.texture = location;
    }

    public int getIndex() {
        return index;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static CableType byIndex(int index){
        for(CableType type : CableType.values()){
            if(type.getIndex() == index){
                return type;
            }
        }
        return NONE;
    }
}
