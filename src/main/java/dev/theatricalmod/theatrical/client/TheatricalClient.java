package dev.theatricalmod.theatrical.client;

import dev.theatricalmod.theatrical.TheatricalCommon;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.client.gui.container.TheatricalContainers;
import dev.theatricalmod.theatrical.client.gui.screen.*;
import dev.theatricalmod.theatrical.client.tile.TileEntityRendererBasicLightingDesk;
import dev.theatricalmod.theatrical.entity.TheatricalEntities;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;

import static dev.theatricalmod.theatrical.client.tile.TheatricalRenderType.MAIN_BEAM;

@Mod.EventBusSubscriber(modid = TheatricalMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TheatricalClient extends TheatricalCommon {

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::textureStitch);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modelLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::worldRenderLastEvent);
    }

    public void setup(FMLClientSetupEvent event) {
        RenderType cutout = RenderType.cutout();
        ItemBlockRenderTypes.setRenderLayer(TheatricalBlocks.TRUSS.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TheatricalBlocks.MOVING_LIGHT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TheatricalBlocks.GENERIC_LIGHT.get(), cutout);
        ItemBlockRenderTypes.setRenderLayer(TheatricalBlocks.BASIC_LIGHTING_DESK.get(), cutout);
        MenuScreens.register(TheatricalContainers.INTELLIGENT_FIXTURE.get(), ScreenIntelligentFixture::new);
        MenuScreens.register(TheatricalContainers.ARTNET_INTERFACE.get(), ScreenArtNetInterface::new);
        MenuScreens.register(TheatricalContainers.GENERIC_FIXTURE.get(), ScreenGenericFixture::new);
        MenuScreens.register(TheatricalContainers.DIMMER_RACK.get(), ScreenDimmerRack::new);
        MenuScreens.register(TheatricalContainers.BASIC_LIGHTING_CONSOLE.get(), ScreenBasicLightingConsole::new);
        MenuScreens.register(TheatricalContainers.DMX_REDSTONE_INTERFACE.get(), ScreenDMXRedstoneInterface::new);
    }

    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(TheatricalEntities.FALLING_LIGHT.get(), FallingLightRenderer::new);
        event.registerBlockEntityRenderer(TheatricalTiles.MOVING_LIGHT.get(), TileEntityFixtureRenderer::new);
        event.registerBlockEntityRenderer(TheatricalTiles.GENERIC_LIGHT.get(), TileEntityFixtureRenderer::new);
        event.registerBlockEntityRenderer(TheatricalTiles.BASIC_LIGHTING_DESK.get(), TileEntityRendererBasicLightingDesk::new);
    }

    @SubscribeEvent
    public void worldRenderLastEvent(RenderLevelStageEvent event){
        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(MAIN_BEAM).endVertex();
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(MAIN_BEAM);
    }

    public void modelLoad(ModelRegistryEvent event) {
        for (Fixture fixture : Fixture.getRegistry().get()) {
            ForgeModelBakery.addSpecialModel(fixture.getHookedModelLocation());
            ForgeModelBakery.addSpecialModel(fixture.getPanModelLocation());
            ForgeModelBakery.addSpecialModel(fixture.getStaticModelLocation());
            ForgeModelBakery.addSpecialModel(fixture.getTiltModelLocation());
        }
    }

    public static BlockHitResult getLookingAt(Player player, double range) {
        return getLookingAt(player, range, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult getLookingAt(Player player, double range, ClipContext.Fluid fluidMode) {
        Level world = player.level;

        Vec3 look = player.getLookAngle();
        Vec3 startPos = getVec3d(player).add(0, player.getEyeHeight(), 0);
        Vec3 endPos = startPos.add(look.multiply(range, range, range));
        ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, fluidMode, player);
        return world.clip(context);
    }


    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static Vec3 getVec3d(Entity entity) {
        return entity.position();
    }

    public void textureStitch(TextureStitchEvent.Pre event) {
        for (Fixture fixture : Fixture.getRegistry().get()) {
            Arrays.stream(fixture.getTextures()).forEach(event::addSprite);
        }
    }

    public void handleDMXUpdate(BlockPos pos, byte[] data) {
        BlockEntity tileFresnel = getWorld().getBlockEntity(pos);
        if (tileFresnel != null) {
//            LazyOptional<IDMXReceiver> optional = tileFresnel.getCapability(DMXReceiver.CAP, Direction.NORTH);
//            optional.ifPresent(dmxReceiver -> {
//                if (data != null) {
//                    for (int i = 0; i < data.length; i++) {
//                        dmxReceiver
//                                .updateChannel(i, data[i]);
//                    }
//                }
//            });
        }
    }

    public void handleProviderDMXUpdate(BlockPos pos, byte[] data) {
        BlockEntity tile = getWorld().getBlockEntity(pos);
        if (tile != null) {
            //TODO: Re-implement this!
//            tile.getCapability(DMXProvider.CAP, Direction.NORTH).ifPresent(idmxProvider -> {
//                idmxProvider.getUniverse(getWorld()).setDmxChannels(data);
//                idmxProvider.refreshDevices();
//                idmxProvider.updateDevices(getWorld(), pos);
//            });
        }
    }
}
