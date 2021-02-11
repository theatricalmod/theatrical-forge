package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import xyz.brassgoggledcoders.patchouliprovider.BookBuilder;
import xyz.brassgoggledcoders.patchouliprovider.CategoryBuilder;
import xyz.brassgoggledcoders.patchouliprovider.PatchouliBookProvider;

import java.util.function.Consumer;

public class TheatricalBookProvider extends PatchouliBookProvider {

    public TheatricalBookProvider(DataGenerator gen) {
        super(gen, TheatricalMod.MOD_ID, "en_us");
    }

    @Override
    protected void addBooks(Consumer<BookBuilder> consumer) {
        final BookBuilder builder = createBookBuilder("guide", "guide.theatrical.name", "guide.theatrical.landing_text");
        builder.setCreativeTab(TheatricalMod.theatricalItemGroup.getPath());
        builder.setShowProgress(false);
        builder.setVersion("1");
        builder.setModel(TheatricalMod.MOD_ID + ":guide");
        final CategoryBuilder lightingCategory =
                builder.addCategory("lighting_category", "Lighting", "", new ItemStack(TheatricalBlocks.GENERIC_LIGHT.get()));

        builder.build(consumer);
    }
}
