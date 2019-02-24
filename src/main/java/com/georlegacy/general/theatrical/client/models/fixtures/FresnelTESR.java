/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.client.models.fixtures;

import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FresnelTESR extends TileEntitySpecialRenderer<TileEntityFresnel> {

    private static float lastBrightnessX, lastBrightnessY;


    @Override
    public void render(TileEntityFresnel te, double x, double y, double z, float partialTicks,
        int destroyStage, float a) {
        EnumFacing direction = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0F, 1F, 0F);
        GlStateManager.translate(0F, 1F, 1F);
        GlStateManager.disableLighting();
        GlStateManager.translate(0.5F, 0, -.5F);
        GlStateManager.rotate(te.prevPan + (te.getPan() - te.prevPan) * partialTicks, 0, 1, 0);
        GlStateManager.rotate(te.prevTilt + (te.getTilt() - te.prevTilt) * partialTicks, direction.getAxis() == Z ? 1 : 0, 0, direction.getAxis() == X ? 1 : 0);
        GlStateManager.translate(-.5F, 0, 0.5F);
        renderLight(te);
        double distance = te.getDistance();
        GlStateManager.translate(0F, -1.5F, -1F);
        GlStateManager.translate(0.5F, .5F, .5F);
        float angle = direction.getHorizontalAngle()+ 180F;
        GlStateManager.rotate(-angle, 0F, 1F, 0F);
        GlStateManager.translate(-.5F, -.5F, -.5F);
        if(te.getPower() > 0) {
            renderLightBeam(te, partialTicks, ((te.getPower() / 255) * 0.4f), 0.25, distance,
                te.getGelType().getHex());
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFresnel te) {
        return true;
    }


    public void drawLine(TileEntityFresnel te, Vec3d start, Vec3d end){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder render = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
        render.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//        render.pos(0,0,0).color(255, 0, 0, 255).endVertex();
//        render.pos(10,1 , 10).color(255, 0, 0, 255).endVertex();
        render.pos(start.x ,start.y, start.z).color(0, 0, 255, 255).endVertex();
        render.pos(end.x, end.y, end.z).color(255, 0, 0, 255).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public void renderLight(TileEntityFresnel te){
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockState state = getWorld().getBlockState(pos);
        state = state.getBlock().getActualState(state, getWorld(), pos);
        IBakedModel model = blockrendererdispatcher.getModelForState(state);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        bufferbuilder.setTranslation(-pos.getX(), -1-pos.getY(), -1-pos.getZ());
        bufferbuilder.color(255, 255, 255, 255);
        blockrendererdispatcher.getBlockModelRenderer().renderModel(getWorld(), model, state, pos, bufferbuilder, false);
        bufferbuilder.setTranslation(0, 0, 0);
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void pushBrightness(int u, int t)
    {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, u, t);
    }

    public static void pushMaxBrightness()
    {
        pushBrightness(240, 240);
    }

    public static void popBrightness()
    {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }

    public void renderLightBeam(TileEntityFresnel tileEntityFresnel, float partialTicks, float alpha, double beamSize, double length, int color){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder render = tessellator.getBuffer();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int)(alpha * 255);
        pushMaxBrightness();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.8, 0);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
//        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        //GlStateManager.translate(startX- TileEntityRendererDispatcher.staticPlayerX, startY-TileEntityRendererDispatcher.staticPlayerY, startZ-TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.disableTexture2D();
        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        double width = beamSize;
        double endMultiplier = tileEntityFresnel.getFocus();
        render.pos( width * endMultiplier,  width * endMultiplier ,  -length).color(r, g, b, 0).endVertex();
        render.pos( width,  width,  0).color(r, g, b, a).endVertex();
        render.pos( width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( width * endMultiplier,  -width * endMultiplier,  -length).color(r, g, b, 0).endVertex();

        render.pos( -width * endMultiplier,  -width * endMultiplier,  -length).color(r, g, b, 0).endVertex();
        render.pos( -width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width, width,  0).color(r, g, b, a).endVertex();
        render.pos( -width * endMultiplier,  width * endMultiplier,  -length).color(r, g, b, 0).endVertex();

        render.pos( -width * endMultiplier,  width * endMultiplier,  -length).color(r, g, b, 0).endVertex();
        render.pos( -width,   width,  0).color(r, g, b, a).endVertex();
        render.pos( width,   width,  0).color(r, g, b, a).endVertex();
        render.pos( width * endMultiplier,   width * endMultiplier,  -length).color(r, g, b, 0).endVertex();

        render.pos( width * endMultiplier,  -width * endMultiplier,  -length).color(r, g, b, 0).endVertex();
        render.pos( width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width * endMultiplier,  -width * endMultiplier,   -length).color(r, g, b, 0).endVertex();

//        render.pos( length,  width,  width).color(r, g, b, a).endVertex();
//        render.pos( 0,  width,  width).color(r, g, b, a).endVertex();
//        render.pos( 0,  -width,  width).color(r, g, b, a).endVertex();
//        render.pos( length,  -width,  width).color(r, g, b, a).endVertex();
//
//        render.pos( length,  -width,  -width).color(r, g, b, a).endVertex();
//        render.pos( 0,  -width,  -width).color(r, g, b, a).endVertex();
//        render.pos( 0, width,  -width).color(r, g, b, a).endVertex();
//        render.pos( length,  width,  -width).color(r, g, b, a).endVertex();
//
//        render.pos( length,  width,   -width).color(r, g, b, a).endVertex();
//        render.pos( 0,   width,  -width).color(r, g, b, a).endVertex();
//        render.pos( 0,   width,  width).color(r, g, b, a).endVertex();
//        render.pos( length,   width,  width).color(r, g, b, a).endVertex();
//
//        render.pos( length,  -width,  width).color(r, g, b, a).endVertex();
//        render.pos( 0,  -width,  width).color(r, g, b, a).endVertex();
//        render.pos( 0,  -width,  -width).color(r, g, b, a).endVertex();
//        render.pos( length,  -width,   -width).color(r, g, b, a).endVertex();

//        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(tileEntityFresnel.getWorld().getBlockState(tileEntityFresnel.getLightBlock()), tileEntityFresnel.getLightBlock(), tileEntityFresnel.getWorld(), render);
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();

        GlStateManager.alphaFunc(func, ref);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
//        GlStateManager.disableDepth();
//        GlStateManager.disableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        popBrightness();
    }
}
