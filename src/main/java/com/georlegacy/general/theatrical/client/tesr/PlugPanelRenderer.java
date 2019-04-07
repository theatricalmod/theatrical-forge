package com.georlegacy.general.theatrical.client.tesr;

import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexReceiver;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexReceiver;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.tiles.TilePipePanel;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
            if (te.getCapability(SocapexReceiver.CAP, null) != null) {
                ISocapexReceiver socapexReceiver = te.getCapability(SocapexReceiver.CAP, null);
                if (socapexReceiver.getIdentifier() != null) {
                    GlStateManager.scale(0.7F, 0.7F, 1F);
                    drawString(Minecraft.getMinecraft().fontRenderer, socapexReceiver.getIdentifier(), 0.5, 0.4D);
                }
            }
            setLightmapDisabled(false);
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();
    }


    private void drawString(FontRenderer font, String string, double y, double size) {
        if (string.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6D, y, 0D);
        int len = font.getStringWidth(string);
        double scale = size / 9D;
        double w = len * scale;

        if (w > 1D) {
            scale /= w;
            w = 1D;
        }

        if (w > 0.9D) {
            scale *= 0.9D;
        }

        GlStateManager.scale(scale, scale, 1D);
        font.drawString(string, 0, 0, 0xFFD8D8D8);
        GlStateManager.popMatrix();
    }
}
