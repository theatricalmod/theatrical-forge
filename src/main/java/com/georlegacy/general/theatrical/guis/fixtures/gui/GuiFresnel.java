package com.georlegacy.general.theatrical.guis.fixtures.gui;

import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerFresnel;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiFresnel extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
            "textures/gui/frensel.png");
    private ContainerFresnel inventoryPlayer;

    public GuiFresnel(TileEntityFresnel tileEntityFresnel, ContainerFresnel inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1,1,1,1);
        mc.getTextureManager().bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = I18n.format(TheatricalBlocks.BLOCK_FRESNEL.getUnlocalizedName() + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString("ItemGel", xSize / 2 - fontRenderer.getStringWidth("ItemGel") / 2, ySize - 143 /*(height + 11)*/, 0x404040);
        fontRenderer.drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
