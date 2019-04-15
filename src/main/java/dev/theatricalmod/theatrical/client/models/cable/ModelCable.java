package dev.theatricalmod.theatrical.client.models.cable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.theatricalmod.theatrical.tiles.cables.CableType;
import dev.theatricalmod.theatrical.util.ClientUtils;
import dev.theatricalmod.theatrical.util.Reference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelCable implements IModel {

    public static final ModelRotation[] FACE_ROTATIONS = {
        ModelRotation.X0_Y0,
        ModelRotation.X180_Y0,
        ModelRotation.X90_Y180,
        ModelRotation.X90_Y0,
        ModelRotation.X90_Y90,
        ModelRotation.X90_Y270
    };


    private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> TRANSFORM_MAP;

    private static TRSRTransformation get(float ty, float ax, float ay, float s)
    {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new javax.vecmath.Vector3f(0F, ty / 16F, 0F), TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, 0F)), new javax.vecmath.Vector3f(s, s, s), null));
    }

    static
    {
        TRSRTransformation thirdperson = get(2.5F, 75, 45, 0.375F);
        TRSRTransformation flipX = new TRSRTransformation(null, null, new javax.vecmath.Vector3f(-1, 1, 1), null);
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.GUI, get(0, 30, 225, 0.625F));
        builder.put(ItemCameraTransforms.TransformType.GROUND, get(3, 0, 0, 0.25F));
        builder.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0.5F));
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(thirdperson)).compose(flipX)));
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 45, 0.4F));
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 225, 0.4F));
        TRANSFORM_MAP = Maps.immutableEnumMap(builder.build());
    }

    public static org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handleModelPerspective(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType)
    {
        return PerspectiveMapWrapper.handlePerspective(model, TRANSFORM_MAP, cameraTransformType);
    }

    public final List<ResourceLocation> modelNorth, modelSouth, modelEast, modelWest, modelCenter;
    public final ResourceLocation particle;

    public final Collection<ResourceLocation> textures;

    public ModelCable() {
        modelNorth = new ArrayList<>();
        modelSouth = new ArrayList<>();
        modelEast = new ArrayList<>();
        modelWest = new ArrayList<>();
        modelCenter = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            modelCenter.add(new ResourceLocation(Reference.MOD_ID, "block/cable/center/cable_middle_" + i));
            modelNorth.add(new ResourceLocation(Reference.MOD_ID, "block/cable/north/cable_north_extended_" + i));
            modelSouth.add(new ResourceLocation(Reference.MOD_ID, "block/cable/south/cable_south_extended_" + i));
            modelEast.add(new ResourceLocation(Reference.MOD_ID, "block/cable/east/cable_east_extended_" + i));
            modelWest.add(new ResourceLocation(Reference.MOD_ID, "block/cable/west/cable_west_extended_" + i));
        }
        particle = new ResourceLocation("minecraft:blocks/concrete_gray");
        textures = new HashSet<>();
        for(CableType type : CableType.values()){
            if(type != CableType.NONE){
                textures.add(type.getTexture());
            }
        }
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return textures;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
        Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new ModelCableBaked(this, bakedTextureGetter.apply(particle), (id, rotation, uvlock, retextures) -> {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

            for (Map.Entry entry : retextures)
            {
                builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
            }
            IModel model = ModelLoaderRegistry.getModelOrMissing(id).uvlock(uvlock).retexture(builder.build());
            IBakedModel bakedModel = model.bake(rotation, format, bakedTextureGetter);
            return ClientUtils.optimize(bakedModel.getQuads(null, null, 0L));
        });
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        Stream<ResourceLocation> combinedStream = Stream.of(modelCenter, modelNorth, modelSouth, modelEast, modelWest).flatMap(Collection::stream);

        return combinedStream.collect(Collectors.toList());
    }

    public interface ModelCallback
    {
        List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, boolean uvlock, Map.Entry... retextures);

        default List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, Map.Entry... retextures){
            return get(id, rotation, true, retextures);
        }
    }
}
