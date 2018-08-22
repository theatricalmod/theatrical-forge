package com.georlegacy.general.theatrical.items.fixtureattr.gel;

import com.georlegacy.general.theatrical.init.TheatricalItems;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class Gel extends Item {

    public Gel() {
        this
                .setRegistryName("theatrical", "gel")
                .setUnlocalizedName("gel")
                .setMaxStackSize(64)
                .setMaxDamage(0)
                .setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (GelType gelType : GelType.values()) {
            itemStacks.add(new ItemStack(
                this.setUnlocalizedName(this.getUnlocalizedName() + "." + gelType.getId()/*gelType.getName() + " Gel (" + gelType.getId() + ")"*/),
                1,
                gelType.getId()
            ));
        }
        items.addAll(itemStacks);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        GelType gelType = GelType.getGelType(stack.getMetadata());
        return gelType.getName() + " Gel (" + gelType.getId() + ")";
    }

    public static void registerGelModelBakeryVariants() {
        ModelBakery.registerItemVariants(TheatricalItems.GEL_ITEM,
                new ResourceLocation(Reference.MOD_ID, "gel/gel_0"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_1"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_2"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_3"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_4"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_5"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_6"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_7"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_8"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_9"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_10"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_11"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_12"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_13"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_14"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_15"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_16"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_17"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_18"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_19"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_20"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_21"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_22"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_23"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_24"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_25"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_26"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_27"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_30"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_31"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_32"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_33"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_34"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_35"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_36"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_37"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_38"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_39"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_40"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_41"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_42"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_43"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_44"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_45"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_46"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_47"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_48"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_49"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_50"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_51"),
                new ResourceLocation(Reference.MOD_ID, "gel/gel_52")
        );
    }

}
