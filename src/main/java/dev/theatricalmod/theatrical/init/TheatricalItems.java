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

package dev.theatricalmod.theatrical.init;

import dev.theatricalmod.theatrical.armor.utility.FixtureDetailHelmetItem;
import dev.theatricalmod.theatrical.items.ItemBundledCable;
import dev.theatricalmod.theatrical.items.ItemDMXCable;
import dev.theatricalmod.theatrical.items.ItemPowerCable;
import dev.theatricalmod.theatrical.items.ItemSocapexCable;
import dev.theatricalmod.theatrical.items.attr.fixture.gel.BlankGel;
import dev.theatricalmod.theatrical.items.attr.fixture.gel.ItemGel;
import dev.theatricalmod.theatrical.items.attr.fixture.gobo.BlankGobo;
import dev.theatricalmod.theatrical.items.base.ItemCable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;

public class TheatricalItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final ItemGel ITEM_GEL = new ItemGel();
    public static final BlankGel ITEM_BLANK_GEL = new BlankGel();

    public static final BlankGobo ITEM_BLANK_GOBO = new BlankGobo();

    public static final FixtureDetailHelmetItem ITEM_FIXTURE_DETAIL_HELMET = new FixtureDetailHelmetItem();

    public static final ItemDMXCable ITEM_DMX_CABLE = new ItemDMXCable();
    public static final ItemPowerCable ITEM_POWER_CABLE = new ItemPowerCable();
    public static final ItemCable ITEM_BUNDLED_CABLE = new ItemBundledCable();
    public static final ItemSocapexCable ITEM_SOCAPEX_CABLE = new ItemSocapexCable();

    static {
        ITEMS.add(ITEM_GEL);
        ITEMS.add(ITEM_BLANK_GEL);

        ITEMS.add(ITEM_BLANK_GOBO);

        ITEMS.add(ITEM_FIXTURE_DETAIL_HELMET);
    }


}
