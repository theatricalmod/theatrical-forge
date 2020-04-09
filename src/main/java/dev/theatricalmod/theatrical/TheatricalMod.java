package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.artnet.ArtNetManager;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.client.TheatricalClient;
import dev.theatricalmod.theatrical.client.gui.container.TheatricalContainers;
import dev.theatricalmod.theatrical.fixtures.FixtureFresnel;
import dev.theatricalmod.theatrical.fixtures.MovingLightFixture;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.util.CapabilityStorageProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheatricalMod.MOD_ID)
public class TheatricalMod {

    public static final String MOD_ID = "theatrical";

    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemGroup theatricalItemGroup = new ItemGroup("theatrical") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.PUMPKIN);
        }
    };

    public static TheatricalCommon proxy;
    private static ArtNetManager artNetManager;

    private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(MOD_ID, "dmx_world_network");
    private static final ResourceLocation PIPE_PANEL_NETWORK = new ResourceLocation(MOD_ID, "pipe_panel_network");
    private static final ResourceLocation SOCAPEX_NETWORK_ID = new ResourceLocation(MOD_ID, "socapex_network");


    public TheatricalMod() {
        //noinspection Convert2MethodRef
        proxy = DistExecutor.runForDist(() -> () -> new TheatricalClient(), () -> () -> new TheatricalCommon());
        Fixture.createRegistry();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fixture.class, this::registerFixture);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::shutdown);
        TheatricalBlocks.BLOCKS.register(eventBus);
        TheatricalTiles.TILES.register(eventBus);
        TheatricalItems.ITEMS.register(eventBus);
        TheatricalContainers.CONTAINERS.register(eventBus);
        proxy.init();
        TheatricalNetworkHandler.init();
        artNetManager = new ArtNetManager();

    }

    public static ArtNetManager getArtNetManager(){
        return artNetManager;
    }

    private void worldTickEvent(WorldTickEvent event) {
        if(event.phase == Phase.END){
            event.world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.tick(event.world));
            event.world.getCapability(WorldSocapexNetwork.CAP).ifPresent(WorldSocapexNetwork::tick);
//            event.world.getCapability(WorldPipePanelNetwork.CAP).ifPresent(WorldPipePanelNetwork::tick);
        }
    }

    private void attachWorldCap(final AttachCapabilitiesEvent t) {
        if(t.getObject() instanceof World){
            World w = (World) t.getObject();
            if(w.isRemote) return;
            t.addCapability(WORLD_CAP_ID, new WorldDMXNetwork());
            t.addCapability(SOCAPEX_NETWORK_ID, new WorldSocapexNetwork(w));
        }
//        t.addCapability(PIPE_PANEL_NETWORK, new WorldPipePanelNetwork(t.getObject()));
    }

    private void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IDMXProvider.class, new CapabilityStorageProvider<>(), DMXProvider::new);
        CapabilityManager.INSTANCE.register(IDMXReceiver.class, new CapabilityStorageProvider<>(), DMXReceiver::new);
        CapabilityManager.INSTANCE.register(WorldDMXNetwork.class, new CapabilityStorageProvider<>(), WorldDMXNetwork::new);

        CapabilityManager.INSTANCE.register(ISocapexReceiver.class, new CapabilityStorageProvider<>(), () ->  null);
        CapabilityManager.INSTANCE.register(ISocapexProvider.class, new CapabilityStorageProvider<>(), () ->  null);
        CapabilityManager.INSTANCE.register(WorldSocapexNetwork.class, new CapabilityStorageProvider<>(), () ->  null);
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Initialising Theatrical ");
        registerCapabilities();
        MinecraftForge.EVENT_BUS.addListener(this::attachWorldCap);
        MinecraftForge.EVENT_BUS.addListener(this::worldTickEvent);
    }

    public void shutdown(FMLServerStoppedEvent serverStoppedEvent){
        getArtNetManager().shutdownAll();
    }


    private void registerFixture(RegistryEvent.Register<Fixture> event){
        event.getRegistry().register(new MovingLightFixture());
        event.getRegistry().register(new FixtureFresnel());
    }

}
