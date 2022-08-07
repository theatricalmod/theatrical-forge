package dev.theatricalmod.theatrical.client.tile;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

public class TheatricalRenderType extends RenderType {

    public static final RenderType MAIN_BEAM = create("TheatricalLightBeam",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
        RenderType.CompositeState.builder().setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
            .setTextureState(NO_TEXTURE)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderStateShard.CULL)
            .setLightmapState(RenderStateShard.NO_LIGHTMAP)
            .setWriteMaskState(WriteMaskStateShard.COLOR_WRITE)
            .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
            .createCompositeState(false));

    public static final RenderType FADER = create("TheatricalFader",
        DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
        RenderType.CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .createCompositeState(false));

    public TheatricalRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }
}
