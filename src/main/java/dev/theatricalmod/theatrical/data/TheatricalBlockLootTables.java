package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

class TheatricalBlockLootTables extends BlockLootTables {
    @Override
    protected void addTables() {
        TheatricalBlocks.BLOCKS.getEntries().forEach(blockRegistryObject -> {
            switch (blockRegistryObject.getId().getPath()) {
                case "illuminator":
                    break;
                case "generic_light":
                    this.registerLootTable(TheatricalBlocks.GENERIC_LIGHT.get(), droppingWithBroken(TheatricalBlocks.GENERIC_LIGHT.get()));
                case "moving_light":
                    this.registerLootTable(TheatricalBlocks.MOVING_LIGHT.get(), droppingWithBroken(TheatricalBlocks.MOVING_LIGHT.get()));
                default:
                    registerDropSelfLootTable(blockRegistryObject.get());
            }
        });
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return TheatricalBlocks.BLOCKS.getEntries().stream().filter(blockRegistryObject -> !blockRegistryObject.getId().getPath().equalsIgnoreCase("illuminator")).map(RegistryObject::get).collect(Collectors.toList());
    }

    protected static LootTable.Builder droppingWithBroken(Block blockIn) {
        return LootTable.builder().addLootPool(withSurvivesExplosion(blockIn, LootPool.builder().rolls(ConstantRange.of(1)).acceptFunction(CopyBlockState.func_227545_a_(blockIn).func_227552_a_(BlockHangable.BROKEN)).addEntry(ItemLootEntry.builder(blockIn))));
    }
}
