package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.items.TheatricalItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

class TheatricalRecipes extends RecipeProvider {
    public final TagKey<Item> IRON_INGOT = ItemTags.create(new ResourceLocation("forge:ingots/iron"));
    public final TagKey<Item> IRON_BLOCK = ItemTags.create(new ResourceLocation("forge:storage_blocks/iron"));
    public final TagKey<Item> GLOWSTONE_DUST = ItemTags.create(new ResourceLocation("forge:dusts/glowstone"));
    public final TagKey<Item> REDSTONE_DUST = ItemTags.create(new ResourceLocation("forge:dusts/redstone"));
    public final TagKey<Item> GOLD_NUGGET = ItemTags.create(new ResourceLocation("forge:nuggets/gold"));
    public final TagKey<Item> GLASS = ItemTags.create(new ResourceLocation("forge:glass"));
    public final TagKey<Item> STONE = ItemTags.create(new ResourceLocation("forge:stone"));
    public final TagKey<Item> GEAR = ItemTags.create(new ResourceLocation("forge:gears/iron"));

    public TheatricalRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }


    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(TheatricalItems.TRUSS.get(), 4)
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.IRON_BARS)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.BULB.get())
                .unlockedBy("has_item", has(GLOWSTONE_DUST))
                .pattern("GGG")
                .pattern("GDG")
                .pattern("IRI")
                .define('G', GLASS)
                .define('D', GLOWSTONE_DUST)
                .define('I', IRON_INGOT)
                .define('R', Items.REDSTONE)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.GENERIC_LIGHT.get())
                .unlockedBy("has_item", has(TheatricalItems.BULB.get()))
                .pattern("III")
                .pattern("IBI")
                .pattern("IRI")
                .define('I', IRON_INGOT)
                .define('B', TheatricalItems.BULB.get())
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(TheatricalItems.GENERIC_LIGHT.get())
                .unlockedBy("has_item", has(TheatricalItems.GENERIC_LIGHT.get()))
                .requires(TheatricalItems.GENERIC_LIGHT.get())
                .requires(TheatricalItems.BULB.get())
                .save(consumer, "generic_repair");
        ShapedRecipeBuilder.shaped(TheatricalItems.COG.get())
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern(" I ")
                .pattern("I I")
                .pattern(" I ")
                .define('I', IRON_INGOT)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.MOTOR.get())
                .unlockedBy("has_item", has(GEAR))
                .pattern("IRI")
                .pattern("RCR")
                .pattern("IRI")
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .define('C', GEAR)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.LED.get())
                .unlockedBy("has_item", has(REDSTONE_DUST))
                .pattern("GRG")
                .define('G', GOLD_NUGGET)
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.MOVING_LIGHT.get())
                .unlockedBy("has_item", has(TheatricalItems.LED.get()))
                .pattern("IGI")
                .pattern("MLM")
                .pattern("BDB")
                .define('I', IRON_INGOT)
                .define('G', GLASS)
                .define('M', TheatricalItems.MOTOR.get())
                .define('L', TheatricalItems.LED.get())
                .define('B', IRON_BLOCK)
                .define('D', TheatricalItems.DMX_CABLE.get())
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(TheatricalItems.MOVING_LIGHT.get())
                .unlockedBy("has_item", has(TheatricalItems.MOVING_LIGHT.get()))
                .requires(TheatricalItems.MOVING_LIGHT.get())
                .requires(TheatricalItems.MOTOR.get())
                .requires(TheatricalItems.LED.get())
                .save(consumer, "mover_repair");
        ShapedRecipeBuilder.shaped(TheatricalItems.DMX_CABLE.get(), 6)
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern("WWW")
                .pattern("IRI")
                .pattern("WWW")
                .define('W', Items.BLACK_WOOL)
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.POWER_CABLE.get(), 6)
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern("WWW")
                .pattern("IRI")
                .pattern("WWW")
                .define('W', Items.CYAN_WOOL)
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.DIMMED_POWER_CABLE.get(), 6)
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern("WWW")
                .pattern("IRI")
                .pattern("WWW")
                .define('W', Items.BLUE_WOOL)
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.SOCAPEX_CABLE.get(), 6)
                .unlockedBy("has_item", has(IRON_INGOT))
                .pattern("WWW")
                .pattern("IRI")
                .pattern("WWW")
                .define('W', Items.LIGHT_BLUE_WOOL)
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.SOCAPEX_DISTRIBUTION.get())
                .unlockedBy("has_item", has(TheatricalItems.SOCAPEX_CABLE.get()))
                .pattern("III")
                .pattern("IWI")
                .pattern("III")
                .define('I', IRON_INGOT)
                .define('W', TheatricalItems.SOCAPEX_CABLE.get())
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.DMX_REDSTONE_INTERFACE.get())
                .unlockedBy("has_item", has(TheatricalItems.DMX_CABLE.get()))
                .pattern("III")
                .pattern("RDR")
                .pattern("III")
                .define('I', IRON_INGOT)
                .define('R', REDSTONE_DUST)
                .define('D', TheatricalItems.DMX_CABLE.get())
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.BASIC_LIGHTING_DESK.get())
                .unlockedBy("has_item", has(TheatricalItems.DMX_CABLE.get()))
                .pattern("   ")
                .pattern("WWW")
                .pattern("IRI")
                .define('W', Items.LIGHT_BLUE_WOOL)
                .define('I', IRON_BLOCK)
                .define('R', TheatricalItems.DMX_CABLE.get())
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.DIMMER_RACK.get())
                .unlockedBy("has_item", has(TheatricalItems.SOCAPEX_CABLE.get()))
                .pattern("III")
                .pattern("SDS")
                .pattern("III")
                .define('I', IRON_INGOT)
                .define('S', TheatricalItems.SOCAPEX_CABLE.get())
                .define('D', TheatricalItems.DMX_CABLE.get())
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.POSITIONER.get())
                .unlockedBy("has_item", has(REDSTONE_DUST))
                .pattern("SRS")
                .pattern("SBS")
                .pattern("SIS")
                .define('S', STONE)
                .define('R', REDSTONE_DUST)
                .define('B', Items.STONE_BUTTON)
                .define('I', IRON_INGOT)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.IWB.get(), 6)
                .unlockedBy("has_item", has(REDSTONE_DUST))
                .pattern("III")
                .pattern("DPD")
                .pattern("III")
                .define('P', TheatricalItems.DIMMED_POWER_CABLE.get())
                .define('D', TheatricalItems.DMX_CABLE.get())
                .define('I', IRON_INGOT)
                .save(consumer);
        ShapedRecipeBuilder.shaped(TheatricalItems.ARTNET_INTERFACE.get())
                .unlockedBy("has_item", has(REDSTONE_DUST))
                .pattern("III")
                .pattern("DRD")
                .pattern("III")
                .define('R', REDSTONE_DUST)
                .define('D', TheatricalItems.DMX_CABLE.get())
                .define('I', IRON_INGOT)
                .save(consumer);
    }
}
