package com.georlegacy.general.theatrical.handlers;

import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handler for registry of mod entities
 *
 * @author James Conway
 */
@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(TheatricalItems.ITEMS.toArray(new Item[0]));
        TheatricalBlocks.BLOCKS.forEach(block -> event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName())));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event){
        event.getRegistry().registerAll(TheatricalBlocks.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : TheatricalItems.ITEMS)
            if (item instanceof IHasModel)
                ((IHasModel) item).registerModels();
        for (Block block : TheatricalBlocks.BLOCKS)
            if(block instanceof IHasModel)
                ((IHasModel)block).registerModels();
    }

}
