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

import com.georlegacy.general.theatrical.sounds.armor.FixtureDetailHelmetEquipSound;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class TheatricalSoundEvents {

    public static final List<SoundEvent> SOUNDS = new ArrayList<>();

    public static final FixtureDetailHelmetEquipSound SOUND_FIXTURE_DETAIL_HELMET_EQUIP = new FixtureDetailHelmetEquipSound();

    static {
        SOUNDS.add(SOUND_FIXTURE_DETAIL_HELMET_EQUIP);
    }

}
