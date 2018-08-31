package com.georlegacy.general.theatrical.armor.materials;

import com.georlegacy.general.theatrical.armor.materials.base.SingleArmorMaterial;

public class UtilityArmorMaterial extends SingleArmorMaterial {

    @Override
    public String getName() {
        return "Utility Armor Material";
    }

    @Override
    public int getDurability() {
        return -1;
    }

    @Override
    public int[] getDamageReductions() {
        return new int[]{0,0,0,0};
    }

    @Override
    public int getEnchantability() {
        return 0;
    }
}
