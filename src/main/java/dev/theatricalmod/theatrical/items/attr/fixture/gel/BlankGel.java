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

package dev.theatricalmod.theatrical.items.attr.fixture.gel;

import dev.theatricalmod.theatrical.tabs.base.CreativeTabs;
import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.item.Item;

public class BlankGel extends Item  {

    public BlankGel() {
        this
            .setRegistryName(Reference.MOD_ID, "blank_gel")
            .setTranslationKey("blank_gel")
            .setMaxStackSize(64)
            .setCreativeTab(CreativeTabs.GELS_TAB);
    }


}
