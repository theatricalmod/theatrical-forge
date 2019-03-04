package com.georlegacy.general.theatrical.api.capabilities;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class DMXController implements ICapabilityProvider {

    @CapabilityInject(DMXController.class)
    public static Capability<DMXController> CAP;

    public static DMXController get(World world)
    {
        return world.getCapability(CAP, null);
    }

    public final World world;
    public final HashMap<UUID, DMXUniverse> universeList = new HashMap<>();

    public DMXController(World world){
        this.world = world;
    }

    public DMXUniverse newUniverse(){
        DMXUniverse dmxUniverse = new DMXUniverse();
        universeList.put(dmxUniverse.getUuid(), dmxUniverse);
        return dmxUniverse;
    }

    public void destroyUniverse(UUID uuid){
        if(!universeList.containsKey(uuid)){
            return;
        }
        universeList.remove(uuid);
    }

    public DMXUniverse getUniverse(UUID uuid){
        if(!universeList.containsKey(uuid)){
            return null;
        }
        return universeList.get(uuid);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAP ? (T) this : null;
    }


}
