package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TheatricalContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, TheatricalMod.MOD_ID);

    public static final RegistryObject<MenuType<ContainerIntelligentFixture>> INTELLIGENT_FIXTURE = CONTAINERS.register("intelligent_gui", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerIntelligentFixture(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerArtNetInterface>> ARTNET_INTERFACE = CONTAINERS.register("artnet_gui", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerArtNetInterface(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerGenericFixture>> GENERIC_FIXTURE = CONTAINERS.register("generic_fixture_gui", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerGenericFixture(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerDimmerRack>> DIMMER_RACK = CONTAINERS.register("dimmer_rack_gui", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerDimmerRack(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerBasicLightingConsole>> BASIC_LIGHTING_CONSOLE = CONTAINERS.register("basic_lighting_console", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerBasicLightingConsole(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));

    public static final RegistryObject<MenuType<ContainerDMXRedstoneInterface>> DMX_REDSTONE_INTERFACE = CONTAINERS.register("dmx_redstone_interface", () -> IForgeMenuType.create((windowId, inv, data) -> new ContainerDMXRedstoneInterface(windowId, TheatricalMod.proxy.getWorld(), data.readBlockPos())));
}
