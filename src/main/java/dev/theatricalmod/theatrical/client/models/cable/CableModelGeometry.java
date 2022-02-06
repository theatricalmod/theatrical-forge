package dev.theatricalmod.theatrical.client.models.cable;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import java.util.*;
import java.util.function.Function;

import dev.theatricalmod.theatrical.api.CableType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class CableModelGeometry implements IModelGeometry<CableModelGeometry> {

    public static final ModelRotation[] FACE_ROTATIONS = {
        ModelRotation.X0_Y0,
        ModelRotation.X180_Y0,
        ModelRotation.X90_Y180,
        ModelRotation.X90_Y0,
        ModelRotation.X90_Y90,
        ModelRotation.X90_Y270
    };


    public final ModelLoaderRegistry.VanillaProxy north, south, east, west, center;
    public final RenderMaterial particle;

    public final Collection<ResourceLocation> textures;

    public CableModelGeometry(ModelLoaderRegistry.VanillaProxy north, ModelLoaderRegistry.VanillaProxy south, ModelLoaderRegistry.VanillaProxy east, ModelLoaderRegistry.VanillaProxy west, ModelLoaderRegistry.VanillaProxy center) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.center = center;
        particle = ModelLoaderRegistry.blockMaterial(new ResourceLocation("minecraft:blocks/concrete_gray"));
        textures = new HashSet<>();
        for(CableType type : CableType.values()){
            if(type != CableType.NONE){
                textures.add(type.getTexture());
            }
        }
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new CableModelBaked(this, spriteGetter.apply(particle), (partModel, rotation, uvlock, retextures) -> {
            IBakedModel bakedModel = partModel.bake(owner, bakery, (material) -> bakery.getSpriteMap().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(retextures), rotation, null, modelLocation);
            return bakedModel.getQuads(null, null, new Random());
        });
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        ImmutableList.Builder<RenderMaterial> builder = ImmutableList.builder();
        for(ResourceLocation texture : textures){
            builder.add(owner.resolveTexture(texture.toString()));
        }
        return builder.build();
    }

    public interface ModelCallback
    {
        List<BakedQuad> get(ModelLoaderRegistry.VanillaProxy model, ModelRotation rotation, boolean uvlock, ResourceLocation rexture);

        default List<BakedQuad> get(ModelLoaderRegistry.VanillaProxy model, ModelRotation rotation, ResourceLocation rexture){
            return get(model, rotation, true, rexture);
        }
    }
}
