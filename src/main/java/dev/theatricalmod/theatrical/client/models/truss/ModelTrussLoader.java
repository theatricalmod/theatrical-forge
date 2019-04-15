package dev.theatricalmod.theatrical.client.models.truss;

import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelTrussLoader extends DefaultStateMapper implements ICustomModelLoader {


    public static final ModelTrussLoader INSTANCE = new ModelTrussLoader();
    public static final ModelResourceLocation ID = new ModelResourceLocation(Reference.MOD_ID + ":square#normal");


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return ID.getNamespace().equalsIgnoreCase(modelLocation.getNamespace()) && ID.getPath().equalsIgnoreCase(modelLocation.getPath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new ModelTruss();
    }
}
