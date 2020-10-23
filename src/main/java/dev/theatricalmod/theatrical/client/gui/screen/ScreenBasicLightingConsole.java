package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.IDraggable;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonFader;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateArtNetInterfacePacket;
import dev.theatricalmod.theatrical.network.control.ConsoleGoPacket;
import dev.theatricalmod.theatrical.network.control.MoveStepPacket;
import dev.theatricalmod.theatrical.network.control.ToggleModePacket;
import dev.theatricalmod.theatrical.network.control.UpdateConsoleFaderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class ScreenBasicLightingConsole extends ContainerScreen<ContainerBasicLightingConsole> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/lighting_console.png");

    private TextFieldWidget fadeInTime, fadeOutTime;

    public ScreenBasicLightingConsole(ContainerBasicLightingConsole container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 244;
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
        this.addButton(new ButtonFader(lvt_1_1_ + 184, lvt_2_1_ + 7, -1, Byte.toUnsignedInt(container.blockEntity.getGrandMaster()), this::faderDrag));
        this.addButton(new Button(lvt_1_1_ + 130, lvt_2_1_ + 20,15, 20, new StringTextComponent("<-"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new MoveStepPacket(container.blockEntity.getPos(), false));
        }));
        this.addButton(new Button(lvt_1_1_ + 145, lvt_2_1_ + 20,15, 20, new StringTextComponent("->"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new MoveStepPacket(container.blockEntity.getPos(), true));
        }));
        this.addButton(new Button(lvt_1_1_ + 130, lvt_2_1_ + 100,20, 20, new StringTextComponent("Go"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new ConsoleGoPacket(container.blockEntity.getPos(), Integer.parseInt(fadeInTime.getText()), Integer.parseInt(fadeOutTime.getText())));
        }));
        this.addButton(new Button(lvt_1_1_ + 155, lvt_2_1_ + 100,30, 20, new StringTextComponent("Mode"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new ToggleModePacket(container.blockEntity.getPos()));
        }));

        this.fadeInTime = new TextFieldWidget(this.font, lvt_1_1_ + 170, lvt_2_1_ + 60, 20, 10, new StringTextComponent("0"));
        this.fadeOutTime = new TextFieldWidget(this.font, lvt_1_1_ + 170, lvt_2_1_ + 75, 20, 10, new StringTextComponent("0"));
        setupTextField(fadeInTime, Integer.toString(container.blockEntity.getFadeInTicks()));
        setupTextField(fadeOutTime, Integer.toString(container.blockEntity.getFadeOutTicks()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.blit(matrixStack, lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    }

    public void setupTextField(TextFieldWidget widget, String value){
        widget.setText(value);
        widget.setCanLoseFocus(false);
        widget.setTextColor(-1);
        widget.setDisabledTextColour(-1);
        widget.setEnableBackgroundDrawing(true);
        widget.setMaxStringLength(11);
        widget.setValidator(s -> {
            if (s.length() == 0) {
                return true;
            }
            try {
                Integer.parseInt(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
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
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.fadeInTime.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.fadeOutTime.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderHoveredTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        String name = container.blockEntity.getDisplayName().getString();
//        font.drawString(name, xSize / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.8f, 0.8f, 0.8f);
        font.drawString(matrixStack, "Step " + container.blockEntity.getCurrentStep(), 165, 15, 0x404040);
        font.drawString(matrixStack, container.blockEntity.isRunMode() ? "Run Mode"  : "Program Mode", 165, 5, 0x404040);
        font.drawString(matrixStack, "Cues", 265, 5, 0x404040);
        for(int key : container.blockEntity.getStoredSteps().keySet()){
            font.drawString(matrixStack, "Cue - " + key, 260, 15 + (10 * key), 0x404040);
        }
        font.drawString(matrixStack, "Fade In", 165, 77, 0x404040);
        font.drawString(matrixStack, "Fade Out", 165, 95, 0x404040);
        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(this.fadeOutTime.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)){
            fadeOutTime.setFocused2(true);
            fadeInTime.setFocused2(false);
            return true;
        } else if(this.fadeInTime.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)) {
            fadeInTime.setFocused2(true);
            fadeOutTime.setFocused2(false);
            return true;
        } else{
            fadeInTime.setFocused2(false);
            fadeOutTime.setFocused2(false);
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }
}
