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

package com.georlegacy.general.theatrical.items.attr.fixture.gel;

import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.tabs.GelsTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Gel extends Item implements IHasModel {

    public Gel() {
        this
                .setRegistryName("theatrical", "gel")
                .setUnlocalizedName("gel")
                .setMaxStackSize(64)
                .setMaxDamage(0)
                .setHasSubtypes(true).setCreativeTab(
            com.georlegacy.general.theatrical.tabs.base.CreativeTabs.GELS_TAB);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName() + "." + GelType.getGelType(stack.getMetadata()).getId();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(!(tab instanceof GelsTab)){
            return;
        }
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (GelType gelType : GelType.values()) {
            itemStacks.add(new ItemStack(this,
                1,
                gelType.getId()
            ));
        }
        items.addAll(itemStacks);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        GelType gelType = GelType.getGelType(stack.getMetadata());
        return this.getUnlocalizedName() + "." + gelType.getId();
    }

    @Override
    public void registerModels() {
        TheatricalItems.registerGelRenderers();
    }
}
