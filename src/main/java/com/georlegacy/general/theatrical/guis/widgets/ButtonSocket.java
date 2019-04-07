package com.georlegacy.general.theatrical.guis.widgets;

import com.georlegacy.general.theatrical.guis.dimming.ContainerDimmerRack;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonSocket extends GuiButton {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    public final ContainerDimmerRack containerDimmerRack;
    private int channelNumber = 0;
    private String patch;

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y, int channelNumber) {
        super(buttonId, x, y, 17, 17, "");
        this.containerDimmerRack = containerDimmerRack;
        this.channelNumber = channelNumber;
    }

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y, int channelNumber, String patch) {
        this(containerDimmerRack, buttonId, x, y, channelNumber);
        this.patch = patch;
    }


    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    public boolean isPatched() {
        return patch != null && !patch.isEmpty();
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(x, y, 269, 0, 17, 17, 512, 512);
        mc.fontRenderer.drawString("" + (channelNumber + 1), x + 5, y - 10, 0x000000);
        if (isPatched()) {
            mc.getTextureManager().bindTexture(background);
            drawModalRectWithCustomSizedTexture(x - 1, y, 250, 0, 19, 17, 512, 512);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 5, y + 7, 0);
            GlStateManager.scale(0.7F, 0.7F, 1F);
            String[] split = patch.split(":");
            int channel = Integer.parseInt(split[1]);
            String identifier = split[0];
            mc.fontRenderer.drawString(identifier + (channel + 1), 0, 0, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
        if (hovered) {
            drawRect(x, y, x + width, y + height, -2130706433);
        }

    }
}
