package dev.theatricalmod.theatrical.api;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public enum CableType implements StringRepresentable {

    NONE(-1, null, "none", null),
    DMX(0, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/cable"), "dmx", ConnectableType.DMX),
    DIMMED_POWER(1, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/dimmed_power"), "dimmed_power", ConnectableType.DIMMED_POWER),
    POWER(3, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/power"), "power", ConnectableType.POWER),
    SOCAPEX(2, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/socapex"), "socapex", ConnectableType.SOCAPEX),
    BUNDLED(99, new ResourceLocation(TheatricalMod.MOD_ID, "block/cables/bundled"), "bundled", null);

    private final int index;
    private final ResourceLocation texture;
    private final String name;
    private final ConnectableType connectableType;

    CableType(int index, ResourceLocation location, String name, ConnectableType connectableType) {
        this.index = index;
        this.texture = location;
        this.name = name;
        this.connectableType = connectableType;
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
    public String getSerializedName() {
        return name;
    }

    public ConnectableType getConnectableType() {
        return connectableType;
    }
}
