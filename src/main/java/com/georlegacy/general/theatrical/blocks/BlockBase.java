package com.georlegacy.general.theatrical.blocks;

import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockBase extends Block implements IHasModel {

    private String name;

    public BlockBase(String name) {
        super(Material.ROCK);
        this.name = name;
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabs.mainTab);
        setRegistryName(name);

        TheatricalBlocks.BLOCKS.add(this);
    }


    @Override
    public void registerModels() {
        TheatricalItems.registerItemRenderer(Item.getItemFromBlock(this), 0, this.name);
    }
}
