package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.dmx.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.artnet.ArtNetManager;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.client.TheatricalClient;
import dev.theatricalmod.theatrical.client.gui.container.TheatricalContainers;
import dev.theatricalmod.theatrical.compat.top.TOPCompat;
import dev.theatricalmod.theatrical.entity.TheatricalEntities;
import dev.theatricalmod.theatrical.fixtures.FixtureFresnel;
import dev.theatricalmod.theatrical.fixtures.MovingLightFixture;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheatricalMod.MOD_ID)
public class TheatricalMod {

    public static final String MOD_ID = "theatrical";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final CreativeModeTab theatricalItemGroup = new CreativeModeTab("theatrical") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TheatricalItems.DIMMER_RACK.get());
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
        ModLoadingContext.get().registerConfig(Type.COMMON, TheatricalConfigHandler.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(Type.CLIENT, TheatricalConfigHandler.CLIENT_SPEC);
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::registerRegistries);
        eventBus.addGenericListener(Fixture.class, this::registerFixture);
        eventBus.addListener(this::imc);
        TheatricalBlocks.BLOCKS.register(eventBus);
        TheatricalItems.ITEMS.register(eventBus);
        TheatricalTiles.TILES.register(eventBus);
        TheatricalEntities.ENTITIES.register(eventBus);
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
//            event.world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.tick(event.world));
//            event.world.getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> worldSocapexNetwork.tick(event.world));
//            event.world.getCapability(WorldPipePanelNetwork.CAP).ifPresent(WorldPipePanelNetwork::tick);
        }
    }

    private void imc(final InterModEnqueueEvent enqueueEvent){
        TOPCompat.register();
    }

    private void attachWorldCap(final AttachCapabilitiesEvent<Level> t) {
        if(t.getObject() != null){
            Level w = t.getObject();
            if(w.isClientSide) return;
//            t.addCapability(WORLD_CAP_ID, new WorldDMXNetwork());
//            t.addCapability(SOCAPEX_NETWORK_ID, new WorldSocapexNetwork());
        }
//        t.addCapability(PIPE_PANEL_NETWORK, new WorldPipePanelNetwork(t.getObject()));
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initialising Theatrical ");
        MinecraftForge.EVENT_BUS.addGenericListener(Level.class, this::attachWorldCap);
        MinecraftForge.EVENT_BUS.addListener(this::worldTickEvent);
        MinecraftForge.EVENT_BUS.addListener(this::shutdown);
        MinecraftForge.EVENT_BUS.addListener(this::registerCapabiltiies);
    }

    public void registerCapabiltiies(RegisterCapabilitiesEvent event){
        event.register(IDMXProvider.class);
        event.register(IDMXReceiver.class);
//        event.register(WorldDMXNetwork.class);

        event.register(ISocapexReceiver.class);
        event.register(ISocapexProvider.class);
//        event.register(WorldSocapexNetwork.class);

        event.register(ITheatricalPowerStorage.class);
    }

    public void registerRegistries(NewRegistryEvent newRegistryEvent){

        Fixture.createRegistry(newRegistryEvent);
    }

    public void shutdown(ServerStoppedEvent serverStoppedEvent){
        getArtNetManager().shutdownAll();
    }


    private void registerFixture(RegistryEvent.Register<Fixture> event){
        event.getRegistry().register(new MovingLightFixture());
        event.getRegistry().register(new FixtureFresnel());
    }

}
