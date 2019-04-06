package com.georlegacy.general.theatrical.guis.dimming;

import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexReceiver;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexProvider;
import com.georlegacy.general.theatrical.guis.widgets.ButtonSocket;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.tiles.TileDimmerRack;
import com.georlegacy.general.theatrical.util.Reference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiDimmerRack extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    private ContainerDimmerRack inventoryPlayer;
    private TileDimmerRack tileDimmerRack;
    private List<ButtonSocket> sockets;
    private List<ISocapexReceiver> receivers;
    private int currentPage = 0;

    public GuiDimmerRack(TileDimmerRack tileDimmerRack, ContainerDimmerRack inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileDimmerRack = tileDimmerRack;

        this.xSize = 250;
        this.ySize = 203;
        sockets = new ArrayList<>();
        receivers = inventoryPlayer.getDevices();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, 512, 512);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = I18n.format(TheatricalBlocks.BLOCK_DIMMER_RACK.getTranslationKey() + ".name");
        fontRenderer
            .drawString(name, 176 / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer
            .drawString("Plugs", 180 + fontRenderer.getStringWidth("Plugs") / 2, 6, 0x404040);
        fontRenderer
            .drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(),
                8, ySize - 94, 0x404040);
        String pageName = "Panel " + receivers.get(currentPage).getIdentifier();
        fontRenderer
            .drawString(pageName, 170 + fontRenderer.getStringWidth(
                pageName
            ) / 2, 17, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }


    @Override
    public void initGui() {
        super.initGui();
        int width = this.width / 2;
        int height = this.height / 2;
        this.buttonList.clear();
        for (int i = 0; i < 6; i++) {
            String patch = tileDimmerRack.getCapability(SocapexProvider.CAP, null).getPatch(i);
            int x = (width - 95) + 46 * (i < 3 ? i : i - 3);
            int y = height - (i < 3 ? 30 : 75);
            if (patch != null) {
                sockets.add(new ButtonSocket(inventoryPlayer, i, x, y, patch));
            } else {
                sockets.add(new ButtonSocket(inventoryPlayer, i, x, y));
            }
        }
        buttonList.addAll(sockets);
        this.buttonList.add(new GuiButton(101, width + 45, height - 90, 15, 20, "<"));
        this.buttonList.add(new GuiButton(102, width + 43 + 60, height - 90, 15, 20, ">"));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 101) {
                if (currentPage - 1 < 0) {
                    currentPage = receivers.size() - 1;
                } else {
                    currentPage--;
                }
            } else if (button.id == 102) {
                if (currentPage + 1 > receivers.size() - 1) {
                    currentPage = 0;
                } else {
                    currentPage++;
                }
            }
        }
    }

}
