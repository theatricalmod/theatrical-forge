package dev.theatricalmod.theatrical;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TheatricalMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheatricalDataGen {

    @SubscribeEvent
    public static void dataGenEvent(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        if(event.includeServer()){
            gen.addProvider(new TheatricalRecipes(gen));
            gen.addProvider(new TheatricalLootTables(gen));
            BlockTagProvider blockTagProvider = new BlockTagProvider(event.getGenerator());
            gen.addProvider(new ItemTagProvider(event.getGenerator(), blockTagProvider, event.getExistingFileHelper()));
        }
    }

    public static class ItemTagProvider extends ItemTagsProvider {

        public ItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
            super(generatorIn, blockTagProvider, TheatricalMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerTags() {
            this.getOrCreateBuilder(ItemTags.makeWrapperTag("forge:gears/iron")).add(TheatricalItems.COG.get());
        }
    }

    public static class BlockTagProvider extends BlockTagsProvider {

        public BlockTagProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {

        }
    }

    private static class TheatricalRecipes extends RecipeProvider {
        public final ITag<Item> IRON_INGOT = ItemTags.makeWrapperTag("forge:ingots/iron");
        public final ITag<Item> IRON_BLOCK = ItemTags.makeWrapperTag("forge:storage_blocks/iron");
        public final ITag<Item> GLOWSTONE_DUST = ItemTags.makeWrapperTag("forge:dusts/glowstone");
        public final ITag<Item> REDSTONE_DUST = ItemTags.makeWrapperTag("forge:dusts/redstone");
        public final ITag<Item> GOLD_NUGGET = ItemTags.makeWrapperTag("forge:nuggets/gold");
        public final ITag<Item> GLASS = ItemTags.makeWrapperTag("forge:glass");
        public final ITag<Item> STONE = ItemTags.makeWrapperTag("forge:stone");
        public final ITag<Item> GEAR = ItemTags.makeWrapperTag("forge:gears/iron");

        public TheatricalRecipes(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.TRUSS.get())
                    .addCriterion("has_item", hasItem(IRON_INGOT))
                    .patternLine("III")
                    .patternLine("I I")
                    .patternLine("III")
                    .key('I', Items.IRON_BARS)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.BULB.get())
                    .addCriterion("has_item", hasItem(GLOWSTONE_DUST))
                    .patternLine("GGG")
                    .patternLine("GDG")
                    .patternLine("IRI")
                    .key('G', GLASS)
                    .key('D', GLOWSTONE_DUST)
                    .key('I', IRON_INGOT)
                    .key('R', Items.REDSTONE)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.GENERIC_LIGHT.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.BULB.get()))
                    .patternLine("III")
                    .patternLine("IBI")
                    .patternLine("IRI")
                    .key('I', IRON_INGOT)
                    .key('B', TheatricalItems.BULB.get())
                    .key('R', REDSTONE_DUST)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.COG.get())
                    .addCriterion("has_item", hasItem(IRON_INGOT))
                    .patternLine(" I ")
                    .patternLine("I I")
                    .patternLine(" I ")
                    .key('I', IRON_INGOT)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.MOTOR.get())
                    .addCriterion("has_item", hasItem(GEAR))
                    .patternLine("IRI")
                    .patternLine("RCR")
                    .patternLine("IRI")
                    .key('I', IRON_INGOT)
                    .key('R', REDSTONE_DUST)
                    .key('C', GEAR)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.LED.get())
                    .addCriterion("has_item", hasItem(REDSTONE_DUST))
                    .patternLine("GRG")
                    .key('G', GOLD_NUGGET)
                    .key('R', REDSTONE_DUST)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.MOVING_LIGHT.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.LED.get()))
                    .patternLine("IGI")
                    .patternLine("MLM")
                    .patternLine("BDB")
                    .key('I', IRON_INGOT)
                    .key('G', GLASS)
                    .key('M', TheatricalItems.MOTOR.get())
                    .key('L', TheatricalItems.LED.get())
                    .key('B', IRON_BLOCK)
                    .key('D', TheatricalItems.DMX_CABLE.get())
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.DMX_CABLE.get(), 6)
                    .addCriterion("has_item", hasItem(IRON_INGOT))
                    .patternLine("WWW")
                    .patternLine("IRI")
                    .patternLine("WWW")
                    .key('W', Items.BLACK_WOOL)
                    .key('I', IRON_INGOT)
                    .key('R', REDSTONE_DUST)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.DIMMED_POWER_CABLE.get(), 6)
                    .addCriterion("has_item", hasItem(IRON_INGOT))
                    .patternLine("WWW")
                    .patternLine("IRI")
                    .patternLine("WWW")
                    .key('W', Items.BLUE_WOOL)
                    .key('I', IRON_INGOT)
                    .key('R', REDSTONE_DUST)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.SOCAPEX_CABLE.get(), 6)
                    .addCriterion("has_item", hasItem(IRON_INGOT))
                    .patternLine("WWW")
                    .patternLine("IRI")
                    .patternLine("WWW")
                    .key('W', Items.LIGHT_BLUE_WOOL)
                    .key('I', IRON_INGOT)
                    .key('R', REDSTONE_DUST)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.SOCAPEX_DISTRIBUTION.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.SOCAPEX_CABLE.get()))
                    .patternLine("III")
                    .patternLine("IWI")
                    .patternLine("III")
                    .key('I', IRON_INGOT)
                    .key('W', TheatricalItems.SOCAPEX_CABLE.get())
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.DMX_REDSTONE_INTERFACE.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.DMX_CABLE.get()))
                    .patternLine("III")
                    .patternLine("RDR")
                    .patternLine("III")
                    .key('I', IRON_INGOT)
                    .key('R', REDSTONE_DUST)
                    .key('D', TheatricalItems.DMX_CABLE.get())
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.BASIC_LIGHTING_DESK.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.DMX_CABLE.get()))
                    .patternLine("   ")
                    .patternLine("WWW")
                    .patternLine("IRI")
                    .key('W', Items.LIGHT_BLUE_WOOL)
                    .key('I', IRON_BLOCK)
                    .key('R', TheatricalItems.DMX_CABLE.get())
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.DIMMER_RACK.get())
                    .addCriterion("has_item", hasItem(TheatricalItems.SOCAPEX_CABLE.get()))
                    .patternLine("III")
                    .patternLine("SDS")
                    .patternLine("III")
                    .key('I', IRON_INGOT)
                    .key('S', TheatricalItems.SOCAPEX_CABLE.get())
                    .key('D', TheatricalItems.DMX_CABLE.get())
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.POSITIONER.get())
                    .addCriterion("has_item", hasItem(REDSTONE_DUST))
                    .patternLine("SRS")
                    .patternLine("SBS")
                    .patternLine("SIS")
                    .key('S', STONE)
                    .key('R', REDSTONE_DUST)
                    .key('B', Items.STONE_BUTTON)
                    .key('I', IRON_INGOT)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.IWB.get())
                    .addCriterion("has_item", hasItem(REDSTONE_DUST))
                    .patternLine("III")
                    .patternLine("DPD")
                    .patternLine("III")
                    .key('P', TheatricalItems.DIMMED_POWER_CABLE.get())
                    .key('D', TheatricalItems.DMX_CABLE.get())
                    .key('I', IRON_INGOT)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(TheatricalItems.ARTNET_INTERFACE.get())
                    .addCriterion("has_item", hasItem(REDSTONE_DUST))
                    .patternLine("III")
                    .patternLine("DRD")
                    .patternLine("III")
                    .key('R', REDSTONE_DUST)
                    .key('D', TheatricalItems.DMX_CABLE.get())
                    .key('I', IRON_INGOT)
                    .build(consumer);
        }
    }

    private static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            TheatricalBlocks.BLOCKS.getEntries().forEach(blockRegistryObject -> {
                if(!blockRegistryObject.getId().getPath().equalsIgnoreCase("illuminator")){
                    registerDropSelfLootTable(blockRegistryObject.get());
                }
            });
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return TheatricalBlocks.BLOCKS.getEntries().stream().filter(blockRegistryObject -> !blockRegistryObject.getId().getPath().equalsIgnoreCase("illuminator")).map(RegistryObject::get).collect(Collectors.toList());
        }
    }

    private static class TheatricalLootTables extends LootTableProvider {
        public TheatricalLootTables(DataGenerator dataGeneratorIn) {
            super(dataGeneratorIn);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        }
    }
}
