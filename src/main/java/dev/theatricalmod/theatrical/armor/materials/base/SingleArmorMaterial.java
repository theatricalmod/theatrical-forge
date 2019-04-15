package dev.theatricalmod.theatrical.armor.materials.base;

import net.minecraft.util.SoundEvent;

public abstract class SingleArmorMaterial {

    public abstract String getName();

    public abstract String getTextureName();

    public abstract int getDurability();

    public abstract int[] getDamageReductions();

    public abstract int getEnchantability();

    public abstract SoundEvent getSound();

    public abstract float getToughness();

    public abstract Class getPreClass();

}
