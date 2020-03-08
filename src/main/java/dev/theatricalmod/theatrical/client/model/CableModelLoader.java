package dev.theatricalmod.theatrical.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class CableModelLoader implements IModelLoader<CableModelGeometry> {
    public CableModelLoader() {  }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public CableModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new CableModelGeometry();
    }


}
