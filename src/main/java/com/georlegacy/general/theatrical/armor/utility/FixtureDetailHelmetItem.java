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

package com.georlegacy.general.theatrical.armor.utility;

import com.georlegacy.general.theatrical.armor.materials.FixtureDetailArmorMaterial;
import com.georlegacy.general.theatrical.entities.core.IHasModel;
import com.georlegacy.general.theatrical.init.TheatricalArmorMaterials;
import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class FixtureDetailHelmetItem extends ItemArmor implements IHasModel {

    public FixtureDetailHelmetItem() {
        super(TheatricalArmorMaterials.getByClass(FixtureDetailArmorMaterial.class), 0,
            EntityEquipmentSlot.HEAD);
        this
            .setTranslationKey("fixture_detail_helmet")
            .setRegistryName(Reference.MOD_ID, "fixture_detail_helmet");
    }

    @Override
    public void registerModels() {
        TheatricalItems.registerItemRenderer(this, 0, "armor/utility/fixture_detail_helmet");
    }

}
