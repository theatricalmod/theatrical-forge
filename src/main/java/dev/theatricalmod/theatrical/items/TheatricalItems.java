package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheatricalItems {

    private static Item.Properties BASE_PROPERTIES = new Item.Properties().group(TheatricalMod.theatricalItemGroup);

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TheatricalMod.MOD_ID);

    public static final RegistryObject<Item> TRUSS = ITEMS.register("truss", () -> new BlockItem(TheatricalBlocks.TRUSS.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> ARTNET_INTERFACE = ITEMS.register("artnet_interface", () -> new BlockItem(TheatricalBlocks.ARTNET_INTERFACE.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> DMX_CABLE = ITEMS.register("dmx_cable", () -> new BlockItem(TheatricalBlocks.DMX_CABLE.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> SOCAPEX_CABLE = ITEMS.register("socapex_cable", () -> new BlockItem(TheatricalBlocks.SOCAPEX_CABLE.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> POWER_CABLE = ITEMS.register("power_cable", () -> new BlockItem(TheatricalBlocks.POWER_CABLE.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> IWB = ITEMS.register("iwb", () -> new BlockItem(TheatricalBlocks.IWB.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> TEST_DMX = ITEMS.register("test_dmx", () -> new BlockItem(TheatricalBlocks.TEST_DMX.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> MOVING_LIGHT = ITEMS.register("moving_light", () -> new BlockItem(TheatricalBlocks.MOVING_LIGHT.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> GENERIC_LIGHT = ITEMS.register("generic_light", () -> new BlockItem(TheatricalBlocks.GENERIC_LIGHT.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> DIMMER_RACK = ITEMS.register("dimmer_rack", () -> new BlockItem(TheatricalBlocks.DIMMER_RACK.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> SOCAPEX_DISTRIBUTION = ITEMS.register("socapex_distribution", () -> new BlockItem(TheatricalBlocks.SOCAPEX_DISTRIBUTION.get(), BASE_PROPERTIES));
    public static final RegistryObject<Item> BASIC_LIGHTING_DESK = ITEMS.register("basic_lighting_desk", () -> new BlockItem(TheatricalBlocks.BASIC_LIGHTING_DESK.get(), BASE_PROPERTIES));

}
