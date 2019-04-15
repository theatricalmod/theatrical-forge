package dev.theatricalmod.theatrical.guis.fixtures.gui;

import dev.theatricalmod.theatrical.guis.fixtures.containers.ContainerFresnel;
import dev.theatricalmod.theatrical.handlers.TheatricalPacketHandler;
import dev.theatricalmod.theatrical.packets.UpdateLightPacket;
import dev.theatricalmod.theatrical.tiles.TileTungstenFixture;
import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiFresnel extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/frensel.png");
    private ContainerFresnel inventoryPlayer;
    private TileTungstenFixture tileFresnel;

    private GuiSlider tiltSlider;
    private GuiSlider panSlider;

    private int pan, tilt = 0;

    public GuiFresnel(TileTungstenFixture tileFresnel, ContainerFresnel inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileFresnel = tileFresnel;

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
        String name = tileFresnel.getFixture().getName().getPath();
        fontRenderer
            .drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString("Gel", xSize / 2 - fontRenderer.getStringWidth("Gel") / 2,
            ySize - 183 /*(height + 11)*/, 0x404040);
        fontRenderer
            .drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(),
                8, ySize - 94, 0x404040);
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
        int centerX = (this.width / 2) - 256 / 2;
        int centerY = (this.height / 2) - 158 / 2;
        this.pan = tileFresnel.getPan();
        this.tilt = tileFresnel.getTilt();
        this.tiltSlider = this.addButton(
            new GuiSlider(15, centerX + 53, centerY + 35, 150, 20, "", "", -180, 180, tilt, false,
                true, (guiSlider -> this.tilt = guiSlider.getValueInt())));
        this.panSlider = this.addButton(
            new GuiSlider(16, centerX + 53, centerY + 65, 150, 20, "", "", -180, 180, pan, false,
                true, (guiSlider -> this.pan = guiSlider.getValueInt())));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateLightPacket(tilt,
            pan, tileFresnel.getIntensity(), tileFresnel.getPos()));
    }
}
