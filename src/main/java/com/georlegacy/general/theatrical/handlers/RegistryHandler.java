/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.handlers;

import com.georlegacy.general.theatrical.blocks.fixtures.base.IFixture;
import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.init.TheatricalSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
        TheatricalBlocks.BLOCKS.forEach(block -> {
            if(block instanceof IFixture){
                IFixture fixture = (IFixture) block;
                GameRegistry.registerTileEntity(fixture.getTileEntity(), block.getRegistryName());
            }
        });
    }

    @SubscribeEvent
    public static void onSoundEventRegister(RegistryEvent.Register<SoundEvent> event) {
        TheatricalSoundEvents.SOUNDS.forEach(sound -> {
            event.getRegistry().register(sound);
        });
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : TheatricalItems.ITEMS)
            if (item instanceof IHasModel)
                ((IHasModel) item).registerModels();
        for (Block block : TheatricalBlocks.BLOCKS)
            if(block instanceof IHasModel)
                ((IHasModel) block).registerModels();
    }

}
