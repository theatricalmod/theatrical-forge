package com.georlegacy.general.theatrical.guis.fixtures.gui;

import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerIntelligentFixture;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.UpdateDMXStartAddressPacket;
import com.georlegacy.general.theatrical.tiles.TileDMXAcceptor;
import com.georlegacy.general.theatrical.util.Reference;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiIntelligentFixture extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/blank.png");

    private ContainerIntelligentFixture inventoryPlayer;
    private TileDMXAcceptor tileDMXAcceptor;

    private GuiTextField dmxAddress;

    public GuiIntelligentFixture(TileDMXAcceptor tileDMXAcceptor, ContainerIntelligentFixture inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileDMXAcceptor = tileDMXAcceptor;

        this.xSize = 176;
        this.ySize = 203;
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
        String name = tileDMXAcceptor.getFixture().getName().getPath();
        fontRenderer
            .drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString("DMX Start Address", xSize / 2 - fontRenderer.getStringWidth("DMX Start Address") / 2,
            ySize - 183 /*(height + 11)*/, 0x404040);
        fontRenderer
            .drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(),
                8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.dmxAddress.drawTextBox();
    }

    @Override
    public void initGui() {
        super.initGui();
        int width = this.width / 2;
        int height = this.height / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - 50, height - 15, 100, 20, I18n.format("structure_block.mode.save")));
        this.dmxAddress = new GuiTextField(0, this.fontRenderer, width - 50, height - 50, 100, 20);
        this.dmxAddress.setFocused(true);
        if(tileDMXAcceptor.hasCapability(DMXReceiver.CAP, EnumFacing.SOUTH)){
            this.dmxAddress.setText(Integer.toString(tileDMXAcceptor.getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getStartPoint()));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dmxAddress.mouseClicked(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        this.dmxAddress.textboxKeyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                if(Integer.parseInt(dmxAddress.getText()) > 512 || Integer.parseInt(dmxAddress.getText()) < 0){
                    return;
                }
                TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateDMXStartAddressPacket(Integer.parseInt(dmxAddress.getText()), tileDMXAcceptor.getPos()));
            }
        }
    }
}
