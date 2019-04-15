package dev.theatricalmod.theatrical.init;

import dev.theatricalmod.theatrical.armor.materials.FixtureDetailArmorMaterial;
import dev.theatricalmod.theatrical.armor.materials.base.SingleArmorMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

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
