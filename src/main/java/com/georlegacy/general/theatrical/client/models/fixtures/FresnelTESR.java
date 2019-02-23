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

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import java.awt.Color;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

@SideOnly(Side.CLIENT)
public class FresnelTESR extends TileEntitySpecialRenderer<TileEntityFresnel> {

    private static final int LIGHTMAP = 0xF000F0;

    @Override
    public void render(TileEntityFresnel te, double x, double y, double z, float partialTicks,
        int destroyStage, float a) {
        EnumFacing direction = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0F, 1F, 0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate(direction.getHorizontalAngle()+ 180F, 0F, 1F, 0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        GlStateManager.disableLighting();
        BlockPos blockPos = te.getPos().offset(EnumFacing.getFacingFromAxis(direction.getAxisDirection(), direction.getAxis()), 10);
        BlockPos start = te.getPos().offset(EnumFacing.getFacingFromAxis(direction.getAxisDirection(), direction.getAxis()), 1);
        RayTraceResult result = te.getWorld().rayTraceBlocks(new Vec3d(start.getX(), start.getY(), start.getZ()), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        double distance = 10;
        if(result != null) {
            distance = result.hitVec.distanceTo(new Vec3d(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()));
        }
        renderLightBeam(te, partialTicks, 0.4f, 0.25, distance, te.getGelType().getHex());
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFresnel te) {
        return true;
    }

    public void renderLightBeam(TileEntityFresnel tileEntityFresnel, float partialTicks, float alpha, double beamSize, double length, int color){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder render = tessellator.getBuffer();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int)(alpha * 255);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.8, 0);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        //GlStateManager.translate(startX- TileEntityRendererDispatcher.staticPlayerX, startY-TileEntityRendererDispatcher.staticPlayerY, startZ-TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.disableTexture2D();
        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        double width = beamSize;
        
        render.pos( width,  width,  -length).color(r, g, b, a).endVertex();
        render.pos( width,  width,  0).color(r, g, b, a).endVertex();
        render.pos( width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( width,  -width,  -length).color(r, g, b, a).endVertex();

        render.pos( -width,  -width,  -length).color(r, g, b, a).endVertex();
        render.pos( -width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width, width,  0).color(r, g, b, a).endVertex();
        render.pos( -width,  width,  -length).color(r, g, b, a).endVertex();

        render.pos( -width,  width,  -length).color(r, g, b, a).endVertex();
        render.pos( -width,   width,  0).color(r, g, b, a).endVertex();
        render.pos( width,   width,  0).color(r, g, b, a).endVertex();
        render.pos( width,   width,  -length).color(r, g, b, a).endVertex();

        render.pos( width,  -width,  -length).color(r, g, b, a).endVertex();
        render.pos( width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width,  -width,  0).color(r, g, b, a).endVertex();
        render.pos( -width,  -width,   -length).color(r, g, b, a).endVertex();

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
        
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
//        GlStateManager.disableDepth();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
