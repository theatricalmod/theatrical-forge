package com.georlegacy.general.theatrical.guis.widgets;

import com.georlegacy.general.theatrical.guis.dimming.ContainerDimmerRack;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonSocket extends GuiButton {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    public final ContainerDimmerRack containerDimmerRack;
    private String patch;

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y) {
        super(buttonId, x, y, 19, 17, "");
        this.containerDimmerRack = containerDimmerRack;
    }

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int buttonId, int x, int y, String patch) {
        this(containerDimmerRack, buttonId, x, y);
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
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(x, y, 269, 0, 17, 17, 512, 512);
        if (isPatched()) {
            drawModalRectWithCustomSizedTexture(x, y, 250, 0, 19, 17, 512, 512);
            mc.fontRenderer.drawStringWithShadow(patch, -mc.fontRenderer.getStringWidth(patch), 0, 16777215);
        }
    }
}
