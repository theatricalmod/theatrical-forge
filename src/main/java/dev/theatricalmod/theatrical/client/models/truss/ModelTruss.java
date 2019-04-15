package dev.theatricalmod.theatrical.client.models.truss;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.theatricalmod.theatrical.util.ClientUtils;
import dev.theatricalmod.theatrical.util.Reference;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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

public class ModelTruss implements IModel {

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

    public final ResourceLocation modelNorth, modelSouth, modelEast, modelWest, modelDown, modelUp;
    public final ResourceLocation particle;

    public ModelTruss() {
        modelNorth = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_north");
        modelSouth = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_south");
        modelEast = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_east");
        modelWest = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_west");
        modelUp = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_up");
        modelDown = new ResourceLocation(Reference.MOD_ID, "block/mt/mt100_down");
        particle = new ResourceLocation("minecraft:blocks/concrete_gray");
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
        Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new ModelTrussBaked(this, bakedTextureGetter.apply(particle), (id, rotation, uvlock) -> {
            IModel model = ModelLoaderRegistry.getModelOrMissing(id).uvlock(uvlock);
            IBakedModel bakedModel = model.bake(rotation, format, bakedTextureGetter);
            return ClientUtils.optimize(bakedModel.getQuads(null, null, 0L));
        });
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Arrays.asList(modelDown, modelUp, modelNorth, modelSouth, modelEast, modelWest);
    }

    public interface ModelCallback
    {
        List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, boolean uvlock);

        default List<BakedQuad> get(ResourceLocation id, ModelRotation rotation){
            return get(id, rotation, true);
        }
    }
}
