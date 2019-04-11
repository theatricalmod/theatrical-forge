package com.georlegacy.general.theatrical.guis.widgets;

import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexPatch;
import com.georlegacy.general.theatrical.guis.dimming.ContainerDimmerRack;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonSocket extends GuiButton {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    public final ContainerDimmerRack containerDimmerRack;
    private int channelNumber = 0;
    private SocapexPatch patch;
    private String patchIdentifier;

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y, int channelNumber) {
        super(buttonId, x, y, 12, 12, "");
        this.containerDimmerRack = containerDimmerRack;
        this.channelNumber = channelNumber;
    }

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y, int channelNumber, SocapexPatch patch, String patchIdentifier) {
        this(containerDimmerRack, buttonId, x, y, channelNumber);
        this.patch = patch;
        this.patchIdentifier = patchIdentifier;
    }


    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    public boolean isPatched() {
        return patch != null && patch.getReceiver() != null;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(background);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        drawScaledCustomSizeModalRect(x, y, 269, 0, 17, 17, 12, 12, 512, 512);
        if (isPatched()) {
            drawScaledCustomSizeModalRect(x, y, 250, 0, 19, 17, 13, 13, 512, 512);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 5, y + 7, 0);
            GlStateManager.scale(0.7F, 0.7F, 1F);
            fontrenderer.drawString(patchIdentifier + (patch.getReceiverSocket() + 1), 0, 0, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
        if (hovered) {
            drawRect(x, y, x + width, y + height, -2130706433);
        }
    }
}
