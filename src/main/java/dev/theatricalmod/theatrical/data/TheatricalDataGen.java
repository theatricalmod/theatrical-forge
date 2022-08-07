package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = TheatricalMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheatricalDataGen {

    @SubscribeEvent
    public static void dataGenEvent(GatherDataEvent event){
        final DataGenerator gen = event.getGenerator();
        final ExistingFileHelper ex = event.getExistingFileHelper();
        if(event.includeClient()) {
            gen.addProvider(new TheatricalUSLangProvider(gen));
            gen.addProvider(new TheatricalItemModelProvider(gen, ex));
        }
        if(event.includeServer()){
            gen.addProvider(new TheatricalRecipes(gen));
            gen.addProvider(new TheatricalLootTables(gen));
            BlockTagProvider blockTagProvider = new BlockTagProvider(event.getGenerator());
            gen.addProvider(new TheatricalItemTagProvider(event.getGenerator(), blockTagProvider, ex));
            gen.addProvider(new TheatricalBookProvider((gen)));
        }
    }

    public static class BlockTagProvider extends BlockTagsProvider {

        public BlockTagProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void addTags() {

        }
    }

}
