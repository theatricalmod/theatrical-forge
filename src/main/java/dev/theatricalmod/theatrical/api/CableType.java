package dev.theatricalmod.theatrical.api;

import dev.theatricalmod.theatrical.TheatricalMod;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public enum CableType implements IStringSerializable {

    NONE(-1, null, "none"),
    DMX(0, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/cable"), "dmx"),
    DIMMED_POWER(1, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/power"), "power"),
    POWER(3, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/dimmed_power"), "dimmed_power"),
    SOCAPEX(2, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/socapex"), "socapex"),
    BUNDLED(99, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/bundled"), "bundled");

    private final int index;
    private final ResourceLocation texture;
    private final String name;

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
    public static Item getItemForCable(CableType type) {
        switch (type) {
//            case POWER:
//                return TheatricalItems.POWER_CABLE;
//            case DMX:
//                return TheatricalItems.DMX_CABLE;
//            case SOCAPEX:
//                return TheatricalItems.POWER_CABLE;
            default:
                return null;
        }
    }

    @Override
    public String getString() {
        return name;
    }
}
