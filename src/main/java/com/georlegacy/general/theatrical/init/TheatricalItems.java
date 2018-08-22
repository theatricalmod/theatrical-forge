package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.items.attr.fixture.gel.Gel;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Item store for Theatrical items
 *
 * @author James Conway
 */
public class TheatricalItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();


    public static final Gel GEL_ITEM = new Gel();

    static {
        ITEMS.add(GEL_ITEM);
    }


    public static void registerItemRenderer(Item item, int meta, String fileName) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, fileName), "inventory"));
    }

    public static void registerGelRenderers() {
        for (GelType gelType : GelType.values())
            registerItemRenderer(GEL_ITEM, gelType.getId(), "gel/gel_" + gelType.getId());
    }


}
