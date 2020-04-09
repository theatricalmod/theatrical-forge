package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.container.ContainerGenericFixture;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateFixturePacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.client.gui.widget.Slider.ISlider;

public class ScreenGenericFixture extends ContainerScreen<ContainerGenericFixture> implements ISlider {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/blank.png");

    private Slider tiltSlider, panSlider;
    private int tilt, pan;

    public ScreenGenericFixture(ContainerGenericFixture screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 176;
        this.ySize = 126;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        this.tilt = container.blockEntity.getTilt();
        this.pan = container.blockEntity.getPan();
        this.tiltSlider = this.addButton(new Slider(lvt_1_1_ + 13, lvt_2_1_ + 35, 150, 20, "", "", -180, 180, tilt, false, true, (button) -> {
        }, this));
        this.panSlider = this.addButton(new Slider(lvt_1_1_ + 13, lvt_2_1_ + 65, 150, 20, "", "", -180, 180, pan, false, true, (button) -> {
        }, this));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        String name = container.blockEntity.getDisplayName().getString();
        font.drawString(name, xSize / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
//        font.drawString("DMX Universe", xSize / 2 - font.getStringWidth("DMX Universe") / 2, 16, 0x404040);
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        this.tiltSlider.dragging = false;
        this.panSlider.dragging = false;
        return false;
    }

    @Override
    public void onChangeSliderValue(Slider slider) {
        if (slider.equals(panSlider)) {
            this.pan = slider.getValueInt();
        } else if (slider.equals(tiltSlider)) {
            this.tilt = slider.getValueInt();
        }
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateFixturePacket(container.blockEntity.getPos(), this.tilt, this.pan));
    }


}
