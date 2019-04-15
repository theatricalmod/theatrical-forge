package dev.theatricalmod.theatrical.handlers;

import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.client.models.cable.ModelCableLoader;
import dev.theatricalmod.theatrical.client.tesr.FixtureRenderer;
import dev.theatricalmod.theatrical.client.tesr.PlugPanelRenderer;
import dev.theatricalmod.theatrical.init.TheatricalBlocks;
import dev.theatricalmod.theatrical.init.TheatricalItems;
import dev.theatricalmod.theatrical.items.attr.fixture.gel.GelType;
import dev.theatricalmod.theatrical.tiles.TileMovingHead;
import dev.theatricalmod.theatrical.tiles.TileTungstenFixture;
import dev.theatricalmod.theatrical.tiles.dimming.TilePipePanel;
import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
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
        registerItemRenderer(TheatricalItems.ITEM_DMX_CABLE, 0, "cable/dmx");
        registerItemRenderer(TheatricalItems.ITEM_POWER_CABLE, 0, "cable/power");
        registerItemRenderer(TheatricalItems.ITEM_BUNDLED_CABLE, 0, "cable/bundled");
        registerItemRenderer(TheatricalItems.ITEM_SOCAPEX_CABLE, 0, "cable/socapex");
        ClientRegistry.bindTileEntitySpecialRenderer(TileTungstenFixture.class, new FixtureRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMovingHead.class, new FixtureRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePipePanel.class, new PlugPanelRenderer());
        ModelLoaderRegistry.registerLoader(ModelCableLoader.INSTANCE);
        ModelLoader.setCustomStateMapper(TheatricalBlocks.BLOCK_CABLE, ModelCableLoader.INSTANCE);

//        ModelLoaderRegistry.registerLoader(ModelTrussLoader.INSTANCE);
//        ModelLoader.setCustomStateMapper(TheatricalBlocks.BLOCK_SQUARE_TRUSS, ModelTrussLoader.INSTANCE);
    }

    public static IBakedModel bakeModel(ResourceLocation location) {
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
        for (Fixture fixture : Fixture.getRegistry()) {
            fixture.setStaticModel(bakeModel(fixture.getStaticModelLocation()));
            fixture.setTiltModel(bakeModel(fixture.getTiltModelLocation()));
            fixture.setPanModel(bakeModel(fixture.getPanModelLocation()));
            fixture.setHookedModel(bakeModel(fixture.getHookedModelLocation()));
        }
    }

    @SubscribeEvent
    public static void registerColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(
            (stack, tintIndex) -> 0xFF000000 | GelType.getGelType(stack.getMetadata()).getHex(),
            TheatricalItems.ITEM_GEL);
    }

//    @SubscribeEvent
//    public static void registerColors(ColorHandlerEvent.Block event) {
//        event.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
//            if (pos == null || worldIn == null) {
//                return 0xFFFFFFFF;
//            }
//            if (tintIndex == 0) {
//                TileEntity tileEntity = worldIn.getTileEntity(pos);
//                if (tileEntity instanceof TileTungstenFixture) {
//                    return 0xFF000000 | ((TileTungstenFixture) tileEntity).getGel().getHex();
//                }
//            }
//            return 0;
//        }, TheatricalBlocks.BLOCK_FRESNEL);
//    }

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
