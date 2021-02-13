package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TheatricalItemModelProvider extends ItemModelProvider {
    public TheatricalItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, TheatricalMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.singleTexture("guide", mcLoc("item/generated"), "layer0", modLoc("item/guide"));
    }
}
