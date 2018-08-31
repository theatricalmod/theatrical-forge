package com.georlegacy.general.theatrical.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class FixtureDetailHelmetItem extends ItemArmor {

    public FixtureDetailHelmetItem(ArmorMaterial materialIn, int renderIndexIn) {
        super(materialIn, renderIndexIn, EntityEquipmentSlot.HEAD);
    }

}
