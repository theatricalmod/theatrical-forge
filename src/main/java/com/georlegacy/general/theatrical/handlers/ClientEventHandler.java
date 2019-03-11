package com.georlegacy.general.theatrical.handlers;

import com.georlegacy.general.theatrical.client.models.ModelCableLoader;
import com.georlegacy.general.theatrical.client.tesr.FixtureRenderer;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import com.georlegacy.general.theatrical.tiles.fixtures.TileMovingHead;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (GelType gelType : GelType.values()) {
            registerItemRenderer(TheatricalItems.ITEM_GEL, gelType.getId(), "gel/gel_0");
        }
        for(Block block : TheatricalBlocks.BLOCKS){
            registerItemRenderer(Item.getItemFromBlock(block), 0, block.getRegistryName());
        }
        registerItemRenderer(TheatricalItems.ITEM_BLANK_GEL, 0, "gel/base/blank_gel");
        registerItemRenderer(TheatricalItems.ITEM_BLANK_GOBO, 0, "gobo/base/blank_gobo");
        ClientRegistry.bindTileEntitySpecialRenderer(TileFresnel.class, new FixtureRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMovingHead.class, new FixtureRenderer());
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
        TheatricalModels.MOVING_HEAD_BAR = loadModel(new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_bar"));
    }

    @SubscribeEvent
    public static void registerColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(
            (stack, tintIndex) -> 0xFF000000 | GelType.getGelType(stack.getMetadata()).getHex(),
            TheatricalItems.ITEM_GEL);
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

    public static void registerItemRenderer(Item item, int meta, String fileName) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, fileName),
                "inventory"));
    }


    public static void registerItemRenderer(Item item, int meta, ResourceLocation  fileName) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            new ModelResourceLocation(fileName,
                "inventory"));
    }
}
