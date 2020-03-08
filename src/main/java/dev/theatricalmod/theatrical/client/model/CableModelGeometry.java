package dev.theatricalmod.theatrical.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.util.ClientUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;
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


    public final List<ResourceLocation> modelNorth, modelSouth, modelEast, modelWest, modelCenter;
    public final Material particle;

    public final Collection<Material> textures;

    public CableModelGeometry(){
        modelNorth = new ArrayList<>();
        modelSouth = new ArrayList<>();
        modelEast = new ArrayList<>();
        modelWest = new ArrayList<>();
        modelCenter = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            modelCenter.add(new ResourceLocation(TheatricalMod.MOD_ID, "block/cable/center/cable_middle_" + i));
            modelNorth.add(new ResourceLocation(TheatricalMod.MOD_ID, "block/cable/north/cable_north_extended_" + i));
            modelSouth.add(new ResourceLocation(TheatricalMod.MOD_ID, "block/cable/south/cable_south_extended_" + i));
            modelEast.add(new ResourceLocation(TheatricalMod.MOD_ID, "block/cable/east/cable_east_extended_" + i));
            modelWest.add(new ResourceLocation(TheatricalMod.MOD_ID, "block/cable/west/cable_west_extended_" + i));
        }
        particle = new Material(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("minecraft:blocks/concrete_gray"));
        textures = new HashSet<>();
        for(CableType type : CableType.values()){
            if(type != CableType.NONE){
                textures.add(new Material(PlayerContainer.LOCATION_BLOCKS_TEXTURE, type.getTexture()));
            }
        }
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new CableDynamicBakedModel(this, spriteGetter.apply(particle), (id, rotation, uvlock, retextures) -> {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

            for (Map.Entry entry : retextures)
            {
                builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
            }
            IUnbakedModel model = ModelLoader.instance().getModelOrMissing(id);
            model = retexture((BlockModel)model, builder.build());
            IBakedModel bakedModel = model.bakeModel(bakery, spriteGetter, rotation, id);
            return ClientUtils.optimize(bakedModel.getQuads(null, null, new Random()));
        });
    }

    public BlockModel retexture(BlockModel model, ImmutableMap<String, String> textures){
        if (textures.isEmpty())
            return model;

        List<BlockPart> elements = Lists.newArrayList(); //We have to duplicate this so we can edit it below.
        for (BlockPart part : model.getElements())
        {
            elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
        }

        BlockModel newModel = new BlockModel(model.getParentLocation(), elements,
            Maps.newHashMap(model.textures), model.isAmbientOcclusion(), model.func_230176_c_(), //New Textures man VERY IMPORTANT
            model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
        newModel.name = model.name;
        newModel.parent = model.parent;

        Set<String> removed = Sets.newHashSet();

        for (Entry<String, String> e : textures.entrySet())
        {
            if ("".equals(e.getValue()))
            {
                removed.add(e.getKey());
                newModel.textures.remove(e.getKey());
            }
            else
                newModel.textures.put(e.getKey(), Either.left(new Material(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(e.getValue()))));
        }

        // Map the model's texture references as if it was the parent of a model with the retexture map as its textures.
        Map<String, Either<Material, String>> remapped = Maps.newHashMap();

        for (Entry<String, Either<Material, String>> e : newModel.textures.entrySet())
        {
            e.getValue().right().ifPresent(s -> {
                if(s.startsWith("#")){
                    String key = s.substring(1);
                    if (newModel.textures.containsKey(key))
                        remapped.put(e.getKey(), newModel.textures.get(key));
                }
            });
        }

        newModel.textures.putAll(remapped);

        //Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
        for (BlockPart part : newModel.getElements())
        {
            part.mapFaces.entrySet().removeIf(entry -> removed.contains(entry.getValue().texture));
        }

        return newModel;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return textures;
    }



    public interface ModelCallback
    {
        List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, boolean uvlock, Map.Entry... retextures);

        default List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, Map.Entry... retextures){
            return get(id, rotation, true, retextures);
        }
    }

}
