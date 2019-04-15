package dev.theatricalmod.theatrical.tiles.cables;

import dev.theatricalmod.theatrical.init.TheatricalItems;
import dev.theatricalmod.theatrical.items.base.ItemCable;
import dev.theatricalmod.theatrical.util.Reference;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public enum CableType {

    NONE(-1, null, ""),
    DMX(0, new ResourceLocation(Reference.MOD_ID, "blocks/cables/cable"), "DMX"),
    POWER(1, new ResourceLocation(Reference.MOD_ID, "blocks/cables/power"), "Power"),
    SOCAPEX(2, new ResourceLocation(Reference.MOD_ID, "blocks/cables/socapex"), "Socapex"),
    BUNDLED(99, new ResourceLocation(Reference.MOD_ID, "blocks/cables/bundled"), "Bundled");

    private int index;
    private ResourceLocation texture;
    private String name;

    CableType(int index, ResourceLocation location, String name) {
        this.index = index;
        this.texture = location;
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Nullable
    public static ItemCable getItemForCable(CableType type) {
        switch (type) {
            case POWER:
                return TheatricalItems.ITEM_POWER_CABLE;
            case DMX:
                return TheatricalItems.ITEM_DMX_CABLE;
            case SOCAPEX:
                return TheatricalItems.ITEM_SOCAPEX_CABLE;
            default:
                return null;
        }
    }
}
