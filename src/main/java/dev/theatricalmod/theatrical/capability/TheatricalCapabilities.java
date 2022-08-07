package dev.theatricalmod.theatrical.capability;

import dev.theatricalmod.theatrical.api.IConnectable;
import dev.theatricalmod.theatrical.api.dmx.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheatricalCapabilities {

    public static final Capability<IConnectable> CAPABILITY_CONNECTABLE = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IDMXProvider> CAPABILITY_DMX_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IDMXReceiver> CAPABILITY_DMX_RECEIVER = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IConnectable.class);
        event.register(IDMXProvider.class);
        event.register(IDMXReceiver.class);
    }

}
