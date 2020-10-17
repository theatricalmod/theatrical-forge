package dev.theatricalmod.theatrical.client.tile;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class TheatricalRenderType extends RenderType {

    public TheatricalRenderType(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static final RenderType MAIN_BEAM = makeType("TheatricalLightBeam",
        DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256,
        RenderType.State.getBuilder().layer(RenderState.POLYGON_OFFSET_LAYERING)
            .texture(NO_TEXTURE)
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .cull(RenderState.CULL_ENABLED)
            .lightmap(RenderState.LIGHTMAP_DISABLED)
            .writeMask(WriteMaskState.COLOR_WRITE)
            .shadeModel(RenderState.SHADE_ENABLED)
            .build(false));

    public static final RenderType FADER = makeType("TheatricalFader",
        DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256,
        RenderType.State.getBuilder()
            .texture(NO_TEXTURE)
            .shadeModel(SHADE_ENABLED)
            .build(false));
}
