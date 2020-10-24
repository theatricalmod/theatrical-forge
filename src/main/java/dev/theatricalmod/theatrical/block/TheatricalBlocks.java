package dev.theatricalmod.theatrical.block;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.block.cables.BlockCable;
import dev.theatricalmod.theatrical.block.cables.BlockDimmedPowerCable;
import dev.theatricalmod.theatrical.block.cables.BlockPowerCable;
import dev.theatricalmod.theatrical.block.control.BlockBasicLightingControl;
import dev.theatricalmod.theatrical.block.interfaces.BlockDMXRedstoneInterface;
import dev.theatricalmod.theatrical.block.power.BlockDimmerRack;
import dev.theatricalmod.theatrical.block.interfaces.BlockArtNetInterface;
import dev.theatricalmod.theatrical.block.light.BlockGenericFixture;
import dev.theatricalmod.theatrical.block.light.BlockIlluminator;
import dev.theatricalmod.theatrical.block.light.BlockMovingLight;
import dev.theatricalmod.theatrical.block.power.BlockSocapexDistribution;
import dev.theatricalmod.theatrical.block.rigging.BlockIWB;
import dev.theatricalmod.theatrical.block.rigging.BlockTruss;
import dev.theatricalmod.theatrical.block.test.BlockTestDMX;
import dev.theatricalmod.theatrical.fixtures.TheatricalFixtures;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheatricalBlocks {

    public static final AbstractBlock.Properties BASE_PROPERTIES = Block.Properties.create(Material.ANVIL, MaterialColor.GRAY).setRequiresTool().hardnessAndResistance(3, 3);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheatricalMod.MOD_ID);

    public static final RegistryObject<Block> TRUSS = BLOCKS.register("truss", BlockTruss::new);
    public static final RegistryObject<Block> MOVING_LIGHT = BLOCKS.register("moving_light", BlockMovingLight::new);
    public static final RegistryObject<Block> GENERIC_LIGHT = BLOCKS.register("generic_light", () -> new BlockGenericFixture(TheatricalFixtures.FRESNSEL));
    public static final RegistryObject<Block> DMX_CABLE = BLOCKS.register("dmx_cable", () -> new BlockCable(CableType.DMX));
    public static final RegistryObject<Block> SOCAPEX_CABLE = BLOCKS.register("socapex_cable", () -> new BlockCable(CableType.SOCAPEX));
    public static final RegistryObject<Block> DIMMED_POWER_CABLE = BLOCKS.register("dimmed_power_cable", BlockDimmedPowerCable::new);
    public static final RegistryObject<Block> POWER_CABLE = BLOCKS.register("power_cable", BlockPowerCable::new);
    public static final RegistryObject<Block> ILLUMINATOR = BLOCKS.register("illuminator", BlockIlluminator::new);
    public static final RegistryObject<Block> ARTNET_INTERFACE = BLOCKS.register("artnet_interface", BlockArtNetInterface::new);
    public static final RegistryObject<Block> IWB = BLOCKS.register("iwb", BlockIWB::new);
    public static final RegistryObject<Block> TEST_DMX = BLOCKS.register("test_dmx", BlockTestDMX::new);
    public static final RegistryObject<Block> DIMMER_RACK = BLOCKS.register("dimmer_rack", BlockDimmerRack::new);
    public static final RegistryObject<Block> SOCAPEX_DISTRIBUTION = BLOCKS.register("socapex_distribution", BlockSocapexDistribution::new);
    public static final RegistryObject<Block> BASIC_LIGHTING_DESK = BLOCKS.register("basic_lighting_desk", BlockBasicLightingControl::new);
    public static final RegistryObject<Block> DMX_REDSTONE_INTERFACE = BLOCKS.register("redstone_interface", BlockDMXRedstoneInterface::new);
}
