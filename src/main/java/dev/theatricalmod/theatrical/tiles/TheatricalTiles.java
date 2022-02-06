package dev.theatricalmod.theatrical.tiles;


import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXRedstoneInterface;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmedPowerCable;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import dev.theatricalmod.theatrical.tiles.power.TileEntityPowerCable;
import dev.theatricalmod.theatrical.tiles.power.TileEntitySocapexDistribution;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheatricalTiles {


    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TheatricalMod.MOD_ID);

    public static final RegistryObject<TileEntityType<TileEntityIntelligentFixture>> MOVING_LIGHT = TILES.register("moving_light", () -> TileEntityType.Builder.create(TileEntityIntelligentFixture::new, TheatricalBlocks.MOVING_LIGHT.get()).build(null));
//    public static final RegistryObject<TileEntityType<TileEntityDimmedPowerCable>> DIMMED_POWER_CABLE = TILES.register("dimmed_power_cable", () -> TileEntityType.Builder.create(TileEntityDimmedPowerCable::new, TheatricalBlocks.DIMMED_POWER_CABLE.get()).build(null));
//    public static final RegistryObject<TileEntityType<TileEntityPowerCable>> POWER_CABLE = TILES.register("power_cable", () -> TileEntityType.Builder.create(TileEntityPowerCable::new, TheatricalBlocks.POWER_CABLE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityArtNetInterface>> ARTNET_INTERFACE = TILES.register("artnet_interface", () -> TileEntityType.Builder.create(TileEntityArtNetInterface::new, TheatricalBlocks.ARTNET_INTERFACE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityTestDMX>> TEST_DMX = TILES.register("test_dmx", () -> TileEntityType.Builder.create(TileEntityTestDMX::new, TheatricalBlocks.TEST_DMX.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityGenericFixture>> GENERIC_LIGHT = TILES.register("generic_light", () -> TileEntityType.Builder.create(TileEntityGenericFixture::new, TheatricalBlocks.GENERIC_LIGHT.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityDimmerRack>> DIMMER_RACK = TILES.register("dimmer_rack", () -> TileEntityType.Builder.create(TileEntityDimmerRack::new, TheatricalBlocks.DIMMER_RACK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntitySocapexDistribution>> SOCAPEX_DISTRIBUTION = TILES.register("socapex_distribution", () -> TileEntityType.Builder.create(TileEntitySocapexDistribution::new, TheatricalBlocks.SOCAPEX_DISTRIBUTION.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityCable>> CABLE = TILES.register("cable", () -> TileEntityType.Builder.create(TileEntityCable::new, TheatricalBlocks.CABLE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityBasicLightingControl>> BASIC_LIGHTING_DESK = TILES.register("basic_lighting_desk", () -> TileEntityType.Builder.create(TileEntityBasicLightingControl::new, TheatricalBlocks.BASIC_LIGHTING_DESK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityDMXRedstoneInterface>> DMX_REDSTONE_INTERFACE = TILES.register("redstone_interface", () -> TileEntityType.Builder.create(TileEntityDMXRedstoneInterface::new, TheatricalBlocks.DMX_REDSTONE_INTERFACE.get()).build(null));

}
