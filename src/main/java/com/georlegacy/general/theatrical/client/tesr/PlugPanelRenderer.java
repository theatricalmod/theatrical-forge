package com.georlegacy.general.theatrical.client.tesr;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.tiles.TilePipePanel;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PlugPanelRenderer extends TileEntitySpecialRenderer<TilePipePanel> {

    public static final ResourceLocation PLUGS_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/blocks/plug_panel_plugs_front.png");

    @Override
    public void render(TilePipePanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.disableLighting();
            setLightmapDisabled(true);
            GlStateManager.glNormal3f(0F, 1F, 0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate(180F, 0F, 0F, 1F);
            GlStateManager.rotate(facing.getHorizontalAngle() + 180F, 0F, 1F, 0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlStateManager.translate(0F, 0F, -0.005F);
            GlStateManager.color(1F, 1F, 1F, 1F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(PLUGS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(0, 1, 0).tex(0, 1).endVertex();
            buffer.pos(1, 1, 0).tex(1, 1).endVertex();
            buffer.pos(1, 0, 0).tex(1, 0).endVertex();
            buffer.pos(0, 0, 0).tex(0, 0).endVertex();
            tessellator.draw();
            setLightmapDisabled(false);
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();
    }

}
