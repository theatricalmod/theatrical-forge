package com.georlegacy.general.theatrical.guis.interfaces;

import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.packets.UpdateArtNetInterfacePacket;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtnetInterface;
import com.georlegacy.general.theatrical.util.Reference;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiArtNetInterface extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/big_blank.png");

    private ContainerArtNetInterface inventoryPlayer;
    private TileArtnetInterface tileArtnetInterface;
    private GuiTextField subnetField;
    private GuiTextField universeField;
    private GuiTextField ipField;

    public GuiArtNetInterface(TileArtnetInterface tileArtnetInterface, ContainerArtNetInterface inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileArtnetInterface = tileArtnetInterface;

        this.xSize = 176;
        this.ySize = 252;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = I18n.format(TheatricalBlocks.BLOCK_ARTNET_INTERFACE.getTranslationKey() + ".name");
        fontRenderer
            .drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString("Subnet", xSize / 2 - fontRenderer.getStringWidth("Subnet") / 2,
            ySize - 235 /*(height + 11)*/, 0x404040);
        fontRenderer.drawString("Universe", xSize / 2 - fontRenderer.getStringWidth("Universe") / 2,
            ySize - 202 /*(height + 11)*/, 0x404040);
        fontRenderer.drawString("IP", xSize / 2 - fontRenderer.getStringWidth("IP") / 2,
            ySize - 167 /*(height + 11)*/, 0x404040);
        fontRenderer
            .drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(),
                8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.subnetField.drawTextBox();
        this.universeField.drawTextBox();
        this.ipField.drawTextBox();
    }


    @Override
    public void initGui() {
        super.initGui();
        int width = this.width / 2;
        int height = this.height / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - 50, height, 100, 20, I18n.format("structure_block.mode.save")));
        this.subnetField = new GuiTextField(0, this.fontRenderer, width - 50, height - 100, 100, 20);
        this.subnetField.setFocused(true);
        this.subnetField
            .setText(Integer.toString(tileArtnetInterface.getSubnet()));
        this.universeField = new GuiTextField(1, this.fontRenderer, width - 50, height - 65, 100, 20);
        this.universeField.setFocused(true);
        this.universeField
            .setText(Integer.toString(tileArtnetInterface.getUniverse()));
        this.ipField = new GuiTextField(2, this.fontRenderer, width - 60, height - 30, 120, 20);
        this.ipField.setFocused(true);
        this.ipField
            .setText(tileArtnetInterface.getIp());
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.subnetField.mouseClicked(mouseX, mouseY, state);
        this.universeField.mouseClicked(mouseX, mouseY, state);
        this.ipField.mouseClicked(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.subnetField.textboxKeyTyped(typedChar, keyCode);
        this.universeField.textboxKeyTyped(typedChar, keyCode);
        this.ipField.textboxKeyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                if(Integer.parseInt(subnetField.getText()) > 255 || Integer.parseInt(subnetField.getText()) < 0){
                    return;
                }
                if(Integer.parseInt(universeField.getText()) > 255 || Integer.parseInt(universeField.getText()) < 0){
                    return;
                }
                if(ipField.getText().length() < 1){
                    return;
                }
                TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateArtNetInterfacePacket(Integer.parseInt(
                    subnetField.getText()), Integer.parseInt(
                    universeField.getText()), ipField.getText(), tileArtnetInterface.getPos()));
            }
        }
    }

}
