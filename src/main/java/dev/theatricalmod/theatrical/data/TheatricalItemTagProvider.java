package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import net.minecraft.ResourceLocationException;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TheatricalItemTagProvider extends ItemTagsProvider {

    public TheatricalItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(generatorIn, blockTagProvider, TheatricalMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {

        this.tag(ItemTags.create(new ResourceLocation("forge:gears/iron"))).add(TheatricalItems.COG.get());
        this.tag(ItemTags.create(new ResourceLocation("forge:wrenches"))).add(TheatricalItems.WRENCH.get());
    }
}
