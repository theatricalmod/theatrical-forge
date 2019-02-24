package com.georlegacy.general.theatrical.armor.materials;

import com.georlegacy.general.theatrical.armor.materials.base.SingleArmorMaterial;
import com.georlegacy.general.theatrical.init.TheatricalSoundEvents;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.util.SoundEvent;

public class FixtureDetailArmorMaterial extends SingleArmorMaterial {

    @Override
    public String getName() {
        return "fixture_detail_armor_material";
    }

    @Override
    public String getTextureName() {
        return Reference.MOD_ID + ":utility/fixture_detail";
    }

    @Override
    public int getDurability() {
        return 0;
    }

    @Override
    public int[] getDamageReductions() {
        return new int[]{0, 0, 0, 0};
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getSound() {
        return TheatricalSoundEvents.SOUND_FIXTURE_DETAIL_HELMET_EQUIP;
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public Class getPreClass() {
        return this.getClass();
    }

}
