package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

class TheatricalBlockLootTables extends BlockLoot {
    @Override
    protected void addTables() {
        this.getKnownBlocks().forEach(block -> {
            switch (block.getRegistryName().getPath()) {
                case "generic_light":
                    this.add(TheatricalBlocks.GENERIC_LIGHT.get(), droppingWithBroken(TheatricalBlocks.GENERIC_LIGHT.get()));
                    break;
                case "moving_light":
                    this.add(TheatricalBlocks.MOVING_LIGHT.get(), droppingWithBroken(TheatricalBlocks.MOVING_LIGHT.get()));
                    break;
                default:
                    dropSelf(block);
            }
        });
    }

    @Override
    @Nonnull
    protected Iterable<Block> getKnownBlocks() {
        return TheatricalBlocks.BLOCKS.getEntries().stream().filter(blockRegistryObject -> !blockRegistryObject.getId().getPath().equalsIgnoreCase("illuminator")).map(RegistryObject::get).collect(Collectors.toList());
    }

    protected static LootTable.Builder droppingWithBroken(Block blockIn) {
        return LootTable.lootTable().withPool(applyExplosionCondition(blockIn, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).apply(CopyBlockState.copyState(blockIn).copy(BlockHangable.BROKEN)).add(LootItem.lootTableItem(blockIn))));
    }
}
