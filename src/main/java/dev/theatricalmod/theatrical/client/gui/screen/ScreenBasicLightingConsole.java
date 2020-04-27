package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.IDraggable;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonFader;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateArtNetInterfacePacket;
import dev.theatricalmod.theatrical.network.control.UpdateConsoleFaderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenBasicLightingConsole extends ContainerScreen<ContainerBasicLightingConsole> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/lighting_console.png");

    public ScreenBasicLightingConsole(ContainerBasicLightingConsole container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 176;
        this.ySize = 126;
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        byte[] faders = container.blockEntity.getFaders();
        for(int i = 0; i < faders.length; i++){
            int baseY = lvt_2_1_ + 7;
            if(i >= 6){
                baseY += (i / 6) * 61;
            }
            int faderNumber = i - ((i / 6) * 6);
            this.addButton(new ButtonFader(lvt_1_1_ + 7 + (faderNumber * 20), baseY, i, Byte.toUnsignedInt(faders[i]), this::faderDrag));
        }
        this.addButton(new ButtonFader(lvt_1_1_ + 161, lvt_2_1_ + 7, -1, Byte.toUnsignedInt(container.blockEntity.getGrandMaster()), this::faderDrag));
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        this.buttons.forEach(widget -> {
            if(widget instanceof ButtonFader) {
                if (widget.isMouseOver(p_mouseDragged_1_, p_mouseDragged_3_) && ((ButtonFader) widget).isDragging()) {
                    ((ButtonFader) widget).onDrag.onDrag(widget, p_mouseDragged_1_, p_mouseDragged_3_);
                }
            }
        });
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public void tick() {
        super.tick();
        this.buttons.stream().filter(widget -> widget instanceof ButtonFader).forEach(widget -> {
            if(((ButtonFader) widget).getChannel() != -1){
                ((ButtonFader) widget).setValue(Byte.toUnsignedInt(container.blockEntity.getFaders()[((ButtonFader) widget).getChannel()]));
            } else{
                ((ButtonFader) widget).setValue(Byte.toUnsignedInt(container.blockEntity.getGrandMaster()));
            }
        });
    }

    public void faderDrag(Widget widget, double mouseX, double mouseY){
        if(!(widget instanceof ButtonFader)){
            return;
        }
        ButtonFader fader = (ButtonFader) widget;
        int newVal = fader.calculateNewValue(mouseY);
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateConsoleFaderPacket(container.blockEntity.getPos(), fader.getChannel(), newVal));
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
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        RenderSystem.disableBlend();
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        String name = container.blockEntity.getDisplayName().getString();
//        font.drawString(name, xSize / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.8f, 0.8f, 0.8f);
        font.drawString("Step " + container.blockEntity.getCurrentStep(), 165, 5, 0x404040);
        RenderSystem.popMatrix();
    }
}
