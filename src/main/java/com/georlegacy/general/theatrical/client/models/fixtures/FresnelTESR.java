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

import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

@SideOnly(Side.CLIENT)
public class FresnelTESR extends TileEntitySpecialRenderer<TileEntityFresnel> {

    @Override
    public void render(TileEntityFresnel te, double x, double y, double z, float partialTicks,
        int destroyStage, float alpha) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
// because of the way 3D rendering is done, all coordinates are relative to the camera.  This "resets" the "0,0,0" position to the location that is (0,0,0) in the world.
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

// you will need to supply your own position vectors
        renderLight(te);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderLight(TileEntityFresnel tileEntityFresnel){
//        System.out.print(tileEntityFresnel.getGelType().getName());
        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GelType gelType = tileEntityFresnel.getGelType();
        if(gelType != GelType.CLEAR){
            byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.decode(tileEntityFresnel.getGelType().getHex())).array();
            GL11.glColor4ub(bytes[1],bytes[2],bytes[3], (byte) 100);
        }else{
            GL11.glColor4d(0, 0, 0, 0);
        }
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        switch(tileEntityFresnel.getWorld().getBlockState(tileEntityFresnel.getPos()).getValue(
            BlockFresnel.FACING)){
            case NORTH:
                GL11.glVertex3d(0.3,0.05, 0.04);
                GL11.glVertex3d(0.3,0.45, 0.04);
                GL11.glVertex3d(0.7, 0.45, 0.04);
                GL11.glVertex3d(0.7, 0.05, 0.04);
                GL11.glVertex3d(0.3,0.05, 0.04);
                GL11.glVertex3d(0.3, 0.45, 0.04);
                break;
            case EAST:
                GL11.glVertex3d(0.96,0.05, 0.3);
                GL11.glVertex3d(0.96,0.45, 0.3);
                GL11.glVertex3d(0.96, 0.45, 0.7);
                GL11.glVertex3d(0.96, 0.05, 0.7);
                GL11.glVertex3d(0.96,0.05, 0.3);
                GL11.glVertex3d(0.96, 0.45, 0.3);
                break;
            case WEST:
                GL11.glVertex3d(0.04,0.05, 0.3);
                GL11.glVertex3d(0.04,0.45, 0.3);
                GL11.glVertex3d(0.04, 0.45, 0.7);
                GL11.glVertex3d(0.04, 0.05, 0.7);
                GL11.glVertex3d(0.04,0.05, 0.3);
                GL11.glVertex3d(0.04, 0.45, 0.3);
                break;
            case SOUTH:
                GL11.glVertex3d(0.3,0.05, 0.96);
                GL11.glVertex3d(0.3,0.45, 0.96);
                GL11.glVertex3d(0.7, 0.45, 0.96);
                GL11.glVertex3d(0.7, 0.05, 0.96);
                GL11.glVertex3d(0.3,0.05, 0.96);
                GL11.glVertex3d(0.3, 0.45, 0.96);
            break;
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
