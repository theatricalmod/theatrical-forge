package com.georlegacy.general.theatrical.guis.fixtures.gui;

    import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerFresnel;
    import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
    import com.georlegacy.general.theatrical.init.TheatricalBlocks;
    import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
    import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
    import com.georlegacy.general.theatrical.util.Reference;
    import net.minecraft.client.gui.inventory.GuiContainer;
    import net.minecraft.client.renderer.GlStateManager;
    import net.minecraft.client.resources.I18n;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiFresnel extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/frensel.png");
    private ContainerFresnel inventoryPlayer;
    private TileEntityFresnel tileEntityFresnel;

    private GuiSlider tiltSlider;
    private GuiSlider panSlider;

    private int pan, tilt = 0;

    public GuiFresnel(TileEntityFresnel tileEntityFresnel, ContainerFresnel inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileEntityFresnel = tileEntityFresnel;

        this.xSize = 176;
        this.ySize = 203;
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
        String name = I18n.format(TheatricalBlocks.BLOCK_FRESNEL.getTranslationKey() + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString("Gel", xSize / 2 - fontRenderer.getStringWidth("Gel") / 2, ySize - 183 /*(height + 11)*/, 0x404040);
        fontRenderer.drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
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
        this.pan = tileEntityFresnel.getPan();
        this.tilt = tileEntityFresnel.getTilt();
        this.tiltSlider = this.addButton(new GuiSlider(15, centerX + 53, centerY + 35, 150, 20, "", "", -180, 180, tilt, false, true,  (guiSlider -> this.tilt = guiSlider.getValueInt())));
        this.panSlider = this.addButton(new GuiSlider(16, centerX + 53, centerY + 65, 150, 20, "", "", -180, 180, pan, false, true,  (guiSlider -> this.pan = guiSlider.getValueInt())));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateLightPacket(tilt,
            pan, tileEntityFresnel.getPower(), tileEntityFresnel.getPos()));
    }
}
