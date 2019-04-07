package com.georlegacy.general.theatrical.guis.widgets;

import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonPlug extends GuiButton {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    private int plugNumber;
    private String identifier;
    private boolean active;

    public ButtonPlug(int buttonId, int x, int y, int plugNumber, String identifier, boolean active) {
        super(buttonId, x, y, 14, 12, "");
        this.plugNumber = plugNumber;
        this.identifier = identifier;
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        mc.getTextureManager().bindTexture(background);
        drawScaledCustomSizeModalRect(x, y, 250, 0, 19, 17, 14, 12, 512, 512);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 4, y + 5, 0);
        GlStateManager.scale(0.5F, 0.5F, 1F);
        mc.fontRenderer.drawString(identifier + plugNumber, 0, 0, 0xFFFFFF);
        GlStateManager.popMatrix();
        if (hovered) {
            drawRect(x, y, x + width, y + height, -2130706433);
        }
        if (active) {
            drawRect(x, y, x + width, y + height, 0x6666FF66);
        }
    }
}
