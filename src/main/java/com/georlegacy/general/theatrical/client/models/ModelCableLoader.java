package com.georlegacy.general.theatrical.client.models;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelCableLoader extends DefaultStateMapper implements ICustomModelLoader {


    public static final ModelCableLoader INSTANCE = new ModelCableLoader();
    public static final ModelResourceLocation ID = new ModelResourceLocation(Reference.MOD_ID + ":dmx_cable_block#normal");


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return ID.getNamespace().equalsIgnoreCase(modelLocation.getNamespace()) && ID.getPath().equalsIgnoreCase(modelLocation.getPath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new ModelCable();
    }
}
