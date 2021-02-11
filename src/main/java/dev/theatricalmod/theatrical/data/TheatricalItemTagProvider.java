package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TheatricalItemTagProvider extends ItemTagsProvider {

    public TheatricalItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(generatorIn, blockTagProvider, TheatricalMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        this.getOrCreateBuilder(ItemTags.makeWrapperTag("forge:gears/iron")).add(TheatricalItems.COG.get());
        this.getOrCreateBuilder(ItemTags.makeWrapperTag("forge:wrenches")).add(TheatricalItems.WRENCH.get());
    }
}
