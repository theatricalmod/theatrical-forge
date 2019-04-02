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

package com.georlegacy.general.theatrical.client.tesr;

import com.georlegacy.general.theatrical.api.HangableType;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockMovingHead;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.tiles.TileFixture;
import com.georlegacy.general.theatrical.util.FixtureUtils;
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
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public class FixtureRenderer extends TileEntitySpecialRenderer<TileFixture> {

    private static float lastBrightnessX, lastBrightnessY;

    public FixtureRenderer() {
    }

    @Override
    public void render(TileFixture te, double x, double y, double z, float partialTicks,
        int destroyStage, float a) {
        if(!(te.getWorld().getBlockState(te.getPos()).getBlock() instanceof BlockDirectional)){
            return;
        }
        EnumFacing direction = te.getWorld().getBlockState(te.getPos())
            .getValue(BlockDirectional.FACING);
        boolean isFlipped = false;
        if(te.getBlock() == BlockMovingHead.class){
            isFlipped = te.getWorld().getBlockState(te.getPos()).getValue(BlockMovingHead.FLIPPED);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0F, 1F, 0F);
        GlStateManager.translate(0F, 1F, 1F);
        GlStateManager.disableLighting();
        renderLight(te, direction, partialTicks, isFlipped);
        double distance = te.getDistance();
        float[] startPos = te.getBeamStartPosition();
        GlStateManager.translate(startPos[0], startPos[1], startPos[2]);
        if (te.getIntensity() > 0 && MinecraftForgeClient.getRenderPass() == 1) {
            renderLightBeam(te, partialTicks, (te.getIntensity() * 0.4f) / 255, te.getBeamWidth(), distance,
                FixtureUtils.getColorFromTE(te));
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileFixture te) {
        return true;
    }


    public void renderLight(TileFixture te, EnumFacing direction, float partialTicks, boolean isFlipped) {
        if(te.getHangType() == HangableType.BRACE_BAR && ((BlockHangable) te.getWorld().getBlockState(te.getPos()).getBlock()).isHanging(te.getWorld(), te.getPos())){
            GlStateManager.translate(0, 0.175, 0);
        }
        if(te.getHangType() == HangableType.HOOK_BAR && ((BlockHangable) te.getWorld().getBlockState(te.getPos()).getBlock()).isHanging(te.getWorld(), te.getPos())){
            GlStateManager.translate(0, 0.05, 0);
        }
        GlStateManager.translate(0.5F, 0, -.5F);
        GlStateManager.rotate(direction.getHorizontalAngle() , 0, 1, 0);
        GlStateManager.translate(-.5F, 0, 0.5F);
        GlStateManager.translate(0.5, -.5F, -0.5F);
        GlStateManager.rotate(isFlipped ? 180 : 0, 0, 0, 1);
        GlStateManager.translate(-.5F, .5F, 0.5F);
        renderHookBar(te);
        if(te.getHangType() == HangableType.BRACE_BAR && ((BlockHangable) te.getWorld().getBlockState(te.getPos()).getBlock()).isHanging(te.getWorld(), te.getPos())){
            GlStateManager.translate(0, 0.19, 0);
        }
        float[] pans = te.getPanRotationPosition();
        GlStateManager.translate(pans[0], pans[1], pans[2]);
        GlStateManager.rotate(te.prevPan + (te.getPan() - te.prevPan) * partialTicks, 0, 1, 0);
        GlStateManager.translate(-pans[0], -pans[1], -pans[2]);
        renderLightHandle(te);
        float[] tilts = te.getTiltRotationPosition();
        GlStateManager.translate(tilts[0], tilts[1], tilts[2]);
        GlStateManager.rotate(te.prevTilt + (te.getTilt() - te.prevTilt) * partialTicks,
            1, 0, 0);
        GlStateManager.translate(-tilts[0], -tilts[1], -tilts[2]);
        renderLightBody(te);
        GlStateManager.translate(0.5F, 0, -.5F);
        GlStateManager.rotate(te.getDefaultRotation(), 1, 0, 0);
        GlStateManager.translate(-.5F, 0, 0.5F);
    }

    public void renderModel(IBakedModel model, TileFixture te){
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
            .getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockState state = getWorld().getBlockState(pos);
        state = state.getBlock().getActualState(state, getWorld(), pos);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        bufferbuilder.setTranslation(-pos.getX(), -1 - pos.getY(), -1 - pos.getZ());
        bufferbuilder.color(255, 255, 255, 255);
        blockrendererdispatcher.getBlockModelRenderer()
            .renderModel(getWorld(), model, state, pos, bufferbuilder, false);
        bufferbuilder.setTranslation(0, 0, 0);
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void renderHookBar(TileFixture te) {
        renderModel(te.getStaticModel(), te);
    }

    public void renderLightHandle(TileFixture te) {
       renderModel(te.getPanModel(), te);
    }

    public void renderLightBody(TileFixture te) {
        renderModel(te.getTiltModel(), te);
    }

    public static void pushBrightness(int u, int t) {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, u, t);
    }

    public static void pushMaxBrightness() {
        pushBrightness(240, 240);
    }

    public static void popBrightness() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX,
            lastBrightnessY);
    }

    public void renderLightBeam(TileFixture tileFresnel, float partialTicks,
        float alpha, double beamSize, double length, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder render = tessellator.getBuffer();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        pushMaxBrightness();

        //Open GL Stuff
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.translate(0.5, 0.8, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();

        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        double width = beamSize;
        double endMultiplier = width * tileFresnel.getFocus();


        //Do the actual beam vertexes
        render.pos(width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(-width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(-width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width, width, 0).color(r, g, b, a).endVertex();
        render.pos(-width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(-width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(-width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        tessellator.draw();

        //OpenGL Stuff
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.alphaFunc(func, ref);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();

        popBrightness();
    }
}
