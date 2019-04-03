package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.items.base.ItemCable;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.util.ResourceLocation;

public enum CableType {

    NONE(-1, null, null),
    DMX(0, new ResourceLocation(Reference.MOD_ID, "blocks/cables/cable"), TheatricalItems.ITEM_DMX_CABLE),
    POWER(1, new ResourceLocation(Reference.MOD_ID, "blocks/cables/power"), TheatricalItems.ITEM_POWER_CABLE),
    BUNDLED(99, new ResourceLocation(Reference.MOD_ID, "blocks/cables/bundled"), null);

    private int index;
    private ResourceLocation texture;
    private ItemCable cableItem;

    CableType(int index, ResourceLocation location, ItemCable cableItem) {
        this.index = index;
        this.texture = location;
        this.cableItem = cableItem;
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

    public ItemCable getCableItem() {
        return cableItem;
    }
}
