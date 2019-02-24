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

package com.georlegacy.general.theatrical.tabs;

import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.util.Reference;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import scala.actors.threadpool.Arrays;

public class GelsTab extends CreativeTabs {

    public GelsTab() {
        super(Reference.MOD_ID + "_gels");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(TheatricalItems.ITEM_GEL, 1, 79);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> itemStacks) {
        super.displayAllRelevantItems(itemStacks);
        List<Item> order = Arrays
            .asList(new Item[]{TheatricalItems.ITEM_BLANK_GEL, TheatricalItems.ITEM_GEL});
        Comparator<ItemStack> comparator = Ordering.explicit(order).onResultOf(ItemStack::getItem);
        itemStacks.sort(comparator);
    }
}
