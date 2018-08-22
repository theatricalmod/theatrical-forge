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
