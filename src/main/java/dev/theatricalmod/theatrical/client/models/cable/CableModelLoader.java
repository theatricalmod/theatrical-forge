package dev.theatricalmod.theatrical.client.models.cable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class CableModelLoader implements IModelLoader<CableModelGeometry> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public CableModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        // Validate that it's a valid model
        ModelLoaderRegistry.VanillaProxy north = null;
        ModelLoaderRegistry.VanillaProxy south = null;
        ModelLoaderRegistry.VanillaProxy east = null;
        ModelLoaderRegistry.VanillaProxy west = null;
        ModelLoaderRegistry.VanillaProxy center = null;
        north = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, JSONUtils.getJsonObject(modelContents, "north"));
        south = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, JSONUtils.getJsonObject(modelContents, "south"));
        east = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, JSONUtils.getJsonObject(modelContents, "east"));
        west = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, JSONUtils.getJsonObject(modelContents, "west"));
        center = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, JSONUtils.getJsonObject(modelContents, "center"));
        return new CableModelGeometry(north, south, east, west, center);
    }
}
