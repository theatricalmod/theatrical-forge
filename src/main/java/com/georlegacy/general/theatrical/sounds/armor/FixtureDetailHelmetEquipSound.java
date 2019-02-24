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

package com.georlegacy.general.theatrical.sounds.armor;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class FixtureDetailHelmetEquipSound extends SoundEvent {

    public FixtureDetailHelmetEquipSound() {
        super(new ResourceLocation(Reference.MOD_ID, "fixture_detail_helmet_equip"));
        this
            .setRegistryName(Reference.MOD_ID, "fixture_detail_helmet_equip");
    }

}
