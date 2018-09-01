package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.armor.materials.FixtureDetailArmorMaterial;
import com.georlegacy.general.theatrical.armor.materials.base.SingleArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheatricalArmorMaterials {

    public static final List<SingleArmorMaterial> PRE_MATERIALS = new ArrayList<>();
    public static final Map<Class, ItemArmor.ArmorMaterial> MATERIALS = new HashMap<>();

    public static final FixtureDetailArmorMaterial PRE_ARMOR_MATERIAL_FIXTURE_DETAIL = new FixtureDetailArmorMaterial();

    static {
        PRE_MATERIALS.add(PRE_ARMOR_MATERIAL_FIXTURE_DETAIL);

        for (SingleArmorMaterial preArmorMaterial : PRE_MATERIALS) {
            MATERIALS.put(preArmorMaterial.getPreClass(),
                    EnumHelper.addArmorMaterial
                            (
                                    preArmorMaterial.getName(),
                                    preArmorMaterial.getTextureName(),
                                    preArmorMaterial.getDurability(),
                                    preArmorMaterial.getDamageReductions(),
                                    preArmorMaterial.getEnchantability(),
                                    preArmorMaterial.getSound(),
                                    preArmorMaterial.getToughness()
                            ));
        }
    }

    public static ItemArmor.ArmorMaterial getByClass(Class clazz) {
        return MATERIALS.get(clazz);
    }

}
