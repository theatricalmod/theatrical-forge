package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheatricalContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, TheatricalMod.MOD_ID);

    public static final RegistryObject<ContainerType<ContainerIntelligentFixture>> INTELLIGENT_FIXTURE = CONTAINERS.register("intelligent_gui", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerIntelligentFixture(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<ContainerType<ContainerArtNetInterface>> ARTNET_INTERFACE = CONTAINERS.register("artnet_gui", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerArtNetInterface(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<ContainerType<ContainerGenericFixture>> GENERIC_FIXTURE = CONTAINERS.register("generic_fixture_gui", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerGenericFixture(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<ContainerType<ContainerDimmerRack>> DIMMER_RACK = CONTAINERS.register("dimmer_rack_gui", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerDimmerRack(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<ContainerType<ContainerBasicLightingConsole>> BASIC_LIGHTING_CONSOLE = CONTAINERS.register("basic_lighting_console", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerBasicLightingConsole(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<ContainerType<ContainerDMXRedstoneInterface>> DMX_REDSTONE_INTERFACE = CONTAINERS.register("dmx_redstone_interface", () -> IForgeContainerType.create((windowId, inv, data) -> new ContainerDMXRedstoneInterface(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));
}
