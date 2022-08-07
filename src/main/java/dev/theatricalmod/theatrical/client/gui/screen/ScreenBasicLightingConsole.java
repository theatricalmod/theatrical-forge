package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonFader;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.control.ConsoleGoPacket;
import dev.theatricalmod.theatrical.network.control.MoveStepPacket;
import dev.theatricalmod.theatrical.network.control.ToggleModePacket;
import dev.theatricalmod.theatrical.network.control.UpdateConsoleFaderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenBasicLightingConsole extends AbstractContainerScreen<ContainerBasicLightingConsole> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/lighting_console.png");

    private EditBox fadeInTime, fadeOutTime;

    public ScreenBasicLightingConsole(ContainerBasicLightingConsole container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 244;
        this.imageHeight = 126;
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.imageWidth) / 2;
        int lvt_2_1_ = (this.height - this.imageHeight) / 2;
        byte[] faders = menu.blockEntity.getFaders();
        for(int i = 0; i < faders.length; i++){
            int baseY = lvt_2_1_ + 7;
            if(i >= 6){
                baseY += (i / 6) * 61;
            }
            int faderNumber = i - ((i / 6) * 6);
            this.addWidget(new ButtonFader(lvt_1_1_ + 7 + (faderNumber * 20), baseY, i, Byte.toUnsignedInt(faders[i]), this::faderDrag));
        }
        this.addWidget(new ButtonFader(lvt_1_1_ + 184, lvt_2_1_ + 7, -1, Byte.toUnsignedInt(menu.blockEntity.getGrandMaster()), this::faderDrag));
        this.addWidget(new Button(lvt_1_1_ + 130, lvt_2_1_ + 20,15, 20, new TextComponent("<-"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new MoveStepPacket(menu.blockEntity.getBlockPos(), false));
        }));
        this.addWidget(new Button(lvt_1_1_ + 145, lvt_2_1_ + 20,15, 20, new TextComponent("->"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new MoveStepPacket(menu.blockEntity.getBlockPos(), true));
        }));
        this.addWidget(new Button(lvt_1_1_ + 130, lvt_2_1_ + 100,20, 20, new TextComponent("Go"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new ConsoleGoPacket(menu.blockEntity.getBlockPos(), Integer.parseInt(fadeInTime.getValue()), Integer.parseInt(fadeOutTime.getValue())));
        }));
        this.addWidget(new Button(lvt_1_1_ + 155, lvt_2_1_ + 100,30, 20, new TextComponent("Mode"), (button) -> {
            TheatricalNetworkHandler.MAIN.sendToServer(new ToggleModePacket(menu.blockEntity.getBlockPos()));
        }));

        this.fadeInTime = new EditBox(this.font, lvt_1_1_ + 170, lvt_2_1_ + 60, 20, 10, new TextComponent("0"));
        this.fadeOutTime = new EditBox(this.font, lvt_1_1_ + 170, lvt_2_1_ + 75, 20, 10, new TextComponent("0"));
        setupTextField(fadeInTime, Integer.toString(menu.blockEntity.getFadeInTicks()));
        setupTextField(fadeOutTime, Integer.toString(menu.blockEntity.getFadeOutTicks()));
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.leftPos;
        int lvt_5_1_ = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, lvt_4_1_, lvt_5_1_, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void setupTextField(EditBox widget, String value){
        widget.setValue(value);
        widget.setCanLoseFocus(false);
        widget.setTextColor(-1);
        widget.setTextColorUneditable(-1);
        widget.setBordered(true);
        widget.setMaxLength(11);
        widget.setFilter(s -> {
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
        this.children().forEach(widget -> {
            if(widget instanceof ButtonFader) {
                if (widget.isMouseOver(p_mouseDragged_1_, p_mouseDragged_3_) && ((ButtonFader) widget).isDragging()) {
                    ((ButtonFader) widget).onDrag.onDrag((AbstractWidget) widget, p_mouseDragged_1_, p_mouseDragged_3_);
                }
            }
        });
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.children().stream().filter(widget -> widget instanceof ButtonFader).forEach(widget -> {
            if(((ButtonFader) widget).getChannel() != -1){
                ((ButtonFader) widget).setValue(Byte.toUnsignedInt(menu.blockEntity.getFaders()[((ButtonFader) widget).getChannel()]));
            } else{
                ((ButtonFader) widget).setValue(Byte.toUnsignedInt(menu.blockEntity.getGrandMaster()));
            }
        });
    }

    public void faderDrag(AbstractWidget widget, double mouseX, double mouseY){
        if(!(widget instanceof ButtonFader)){
            return;
        }
        ButtonFader fader = (ButtonFader) widget;
        int newVal = fader.calculateNewValue(mouseY);
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateConsoleFaderPacket(menu.blockEntity.getBlockPos(), fader.getChannel(), newVal));
    }

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.fadeInTime.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.fadeOutTime.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        String name = menu.blockEntity.getDisplayName().getString();
//        font.drawString(name, xSize / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        matrixStack.pushPose();
        matrixStack.scale(0.8f, 0.8f, 0.8f);
        font.draw(matrixStack, "Step " + menu.blockEntity.getCurrentStep(), 165, 15, 0x404040);
        font.draw(matrixStack, menu.blockEntity.isRunMode() ? "Run Mode"  : "Program Mode", 165, 5, 0x404040);
        font.draw(matrixStack, "Cues", 265, 5, 0x404040);
        for(int key : menu.blockEntity.getStoredSteps().keySet()){
            font.draw(matrixStack, "Cue - " + key, 260, 15 + (10 * key), 0x404040);
        }
        font.draw(matrixStack, "Fade In", 165, 77, 0x404040);
        font.draw(matrixStack, "Fade Out", 165, 95, 0x404040);
        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(this.fadeOutTime.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)){
            fadeOutTime.setFocus(true);
            fadeInTime.setFocus(false);
            return true;
        } else if(this.fadeInTime.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)) {
            fadeInTime.setFocus(true);
            fadeOutTime.setFocus(false);
            return true;
        } else{
            fadeInTime.setFocus(false);
            fadeOutTime.setFocus(false);
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }
}
