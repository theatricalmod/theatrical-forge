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

import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.api.IHasModel;
import com.georlegacy.general.theatrical.client.models.ModelCableLoader;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.init.TheatricalSoundEvents;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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
        TheatricalBlocks.BLOCKS.forEach(block -> event.getRegistry()
            .register(new ItemBlock(block).setRegistryName(block.getRegistryName())));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(TheatricalBlocks.BLOCKS.toArray(new Block[0]));
        TheatricalBlocks.BLOCKS.forEach(block -> {
            if (block instanceof IHasTileEntity) {
                IHasTileEntity fixture = (IHasTileEntity) block;
                GameRegistry.registerTileEntity(fixture.getTileEntity(), block.getRegistryName());
            }
        });
    }

    @SubscribeEvent
    public static void registerColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (pos == null || worldIn == null) {
                return 0xFFFFFFFF;
            }
            if (tintIndex == 0) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof TileFresnel) {
                    return 0xFF000000 | ((TileFresnel) tileEntity).getGel().getHex();
                }
            }
            return 0;
        }, TheatricalBlocks.BLOCK_FRESNEL);
    }

    @SubscribeEvent
    public static void registerColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(
            (stack, tintIndex) -> 0xFF000000 | GelType.getGelType(stack.getMetadata()).getHex(),
            TheatricalItems.ITEM_GEL);
    }

    @SubscribeEvent
    public static void onSoundEventRegister(RegistryEvent.Register<SoundEvent> event) {
        TheatricalSoundEvents.SOUNDS.forEach(sound -> {
            event.getRegistry().register(sound);
        });
    }


    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : TheatricalItems.ITEMS) {
            if (item instanceof IHasModel) {
                ((IHasModel) item).registerModels();
            }
        }
        for (Block block : TheatricalBlocks.BLOCKS) {
            if (block instanceof IHasModel) {
                ((IHasModel) block).registerModels();
            }
        }
        ModelLoaderRegistry.registerLoader(ModelCableLoader.INSTANCE);
        ModelLoader.setCustomStateMapper(TheatricalBlocks.BLOCK_CABLE, ModelCableLoader.INSTANCE);
        ModelLoader.setCustomModelResourceLocation(new ItemBlock(TheatricalBlocks.BLOCK_CABLE),0, ModelCableLoader.ID);
    }

    public static IBakedModel loadModel(ResourceLocation location){
        try {
            IModel iModel = ModelLoaderRegistry.getModel(location);
            IBakedModel iBakedModel = iModel.bake(iModel.getDefaultState(), DefaultVertexFormats.BLOCK,
                ModelLoader.defaultTextureGetter());
            return iBakedModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent bakeEvent){
        TheatricalModels.FRESNEL_BODY = loadModel(new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_body_only"));
        TheatricalModels.FRESNEL_HOOK_BAR = loadModel(new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_hook_bar"));
        TheatricalModels.FRESNEL_HANDLE = loadModel(new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_handle_only"));
        TheatricalModels.FRESNEL_HOOK = loadModel(new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_hook"));

        TheatricalModels.MOVING_HEAD_STATIC = loadModel(new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_static"));
        TheatricalModels.MOVING_HEAD_PAN = loadModel(new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_pan"));
        TheatricalModels.MOVING_HEAD_TILT = loadModel(new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_tilt"));
    }

}
