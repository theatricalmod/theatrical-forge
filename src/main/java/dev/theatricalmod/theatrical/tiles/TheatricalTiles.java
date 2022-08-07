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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TheatricalTiles {


    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TheatricalMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<TileEntityIntelligentFixture>> MOVING_LIGHT = TILES.register("moving_light", () -> BlockEntityType.Builder.of(TileEntityIntelligentFixture::new, TheatricalBlocks.MOVING_LIGHT.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityDimmedPowerCable>> DIMMED_POWER_CABLE = TILES.register("dimmed_power_cable", () -> BlockEntityType.Builder.of(TileEntityDimmedPowerCable::new, TheatricalBlocks.DIMMED_POWER_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityPowerCable>> POWER_CABLE = TILES.register("power_cable", () -> BlockEntityType.Builder.of(TileEntityPowerCable::new, TheatricalBlocks.POWER_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityArtNetInterface>> ARTNET_INTERFACE = TILES.register("artnet_interface", () -> BlockEntityType.Builder.of(TileEntityArtNetInterface::new, TheatricalBlocks.ARTNET_INTERFACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityTestDMX>> TEST_DMX = TILES.register("test_dmx", () -> BlockEntityType.Builder.of(TileEntityTestDMX::new, TheatricalBlocks.TEST_DMX.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityGenericFixture>> GENERIC_LIGHT = TILES.register("generic_light", () -> BlockEntityType.Builder.of(TileEntityGenericFixture::new, TheatricalBlocks.GENERIC_LIGHT.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityDimmerRack>> DIMMER_RACK = TILES.register("dimmer_rack", () -> BlockEntityType.Builder.of(TileEntityDimmerRack::new, TheatricalBlocks.DIMMER_RACK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntitySocapexDistribution>> SOCAPEX_DISTRIBUTION = TILES.register("socapex_distribution", () -> BlockEntityType.Builder.of(TileEntitySocapexDistribution::new, TheatricalBlocks.SOCAPEX_DISTRIBUTION.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityCable>> CABLE = TILES.register("cable", () -> BlockEntityType.Builder.of(TileEntityCable::new, TheatricalBlocks.SOCAPEX_CABLE.get(), TheatricalBlocks.DMX_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityBasicLightingControl>> BASIC_LIGHTING_DESK = TILES.register("basic_lighting_desk", () -> BlockEntityType.Builder.of(TileEntityBasicLightingControl::new, TheatricalBlocks.BASIC_LIGHTING_DESK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityDMXRedstoneInterface>> DMX_REDSTONE_INTERFACE = TILES.register("redstone_interface", () -> BlockEntityType.Builder.of(TileEntityDMXRedstoneInterface::new, TheatricalBlocks.DMX_REDSTONE_INTERFACE.get()).build(null));

}
