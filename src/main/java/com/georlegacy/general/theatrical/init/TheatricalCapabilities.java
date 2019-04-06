package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.api.capabilities.WorldSocapexNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexProvider;
import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexReceiver;
import com.georlegacy.general.theatrical.util.CapabilityStorageProvider;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class TheatricalCapabilities {

    public static void init() {
        //DMX
        CapabilityManager.INSTANCE.register(IDMXProvider.class, new CapabilityStorageProvider<IDMXProvider>(), () -> null);
        CapabilityManager.INSTANCE.register(IDMXReceiver.class, new CapabilityStorageProvider<IDMXReceiver>(), () -> null);
        CapabilityManager.INSTANCE.register(WorldDMXNetwork.class, new CapabilityStorageProvider<WorldDMXNetwork>(), () -> null);

        //Theatrical Power
        CapabilityManager.INSTANCE.register(ITheatricalPowerStorage.class, new CapabilityStorageProvider<ITheatricalPowerStorage>(), () -> null);

        //Socapex
        CapabilityManager.INSTANCE.register(ISocapexReceiver.class, new CapabilityStorageProvider<ISocapexReceiver>(), () -> null);
        CapabilityManager.INSTANCE.register(ISocapexProvider.class, new CapabilityStorageProvider<ISocapexProvider>(), () -> null);
        CapabilityManager.INSTANCE.register(WorldSocapexNetwork.class, new CapabilityStorageProvider<WorldSocapexNetwork>(), () -> null);

    }

}
