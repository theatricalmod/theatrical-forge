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

package dev.theatricalmod.theatrical.armor.utility;

import dev.theatricalmod.theatrical.api.fixtures.IHasModel;
import dev.theatricalmod.theatrical.armor.materials.FixtureDetailArmorMaterial;
import dev.theatricalmod.theatrical.handlers.ClientEventHandler;
import dev.theatricalmod.theatrical.init.TheatricalArmorMaterials;
import dev.theatricalmod.theatrical.util.Reference;
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
        ClientEventHandler.registerItemRenderer(this, 0, "armor/utility/fixture_detail_helmet");
    }

}
