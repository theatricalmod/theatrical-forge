package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateArtNetInterfacePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenArtNetInterface extends AbstractContainerScreen<ContainerArtNetInterface> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/blank.png");

    private EditBox dmxAddress;
    private EditBox ipAddress;

    public ScreenArtNetInterface(ContainerArtNetInterface container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 126;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            this.getMinecraft().player.closeContainer();
        }
        if(this.dmxAddress.isFocused()){
            return this.dmxAddress.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxAddress.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        } else if(this.ipAddress.isFocused()){
            return this.ipAddress.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.ipAddress.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(this.dmxAddress.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)){
            dmxAddress.setFocus(true);
            ipAddress.setFocus(false);
        } else if(this.ipAddress.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)) {
            ipAddress.setFocus(true);
            dmxAddress.setFocus(false);
        } else{
            ipAddress.setFocus(false);
            dmxAddress.setFocus(false);
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String currentDMX = this.dmxAddress.getValue();
        String currentIP = this.ipAddress.getValue();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.dmxAddress.setValue(currentDMX);
        this.ipAddress.setValue(currentIP);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.imageWidth) / 2;
        int lvt_2_1_ = (this.height - this.imageHeight) / 2;
        this.dmxAddress = new EditBox(this.font, lvt_1_1_ + 62, lvt_2_1_ + 25, 50, 10, new TextComponent(""));
        this.dmxAddress.setValue(Integer.toString(menu.blockEntity.getUniverse()));
        this.dmxAddress.setCanLoseFocus(false);
        this.dmxAddress.changeFocus(true);
        this.dmxAddress.setTextColor(-1);
        this.dmxAddress.setTextColorUneditable(-1);
        this.dmxAddress.setBordered(true);
        this.dmxAddress.setMaxLength(35);
        this.dmxAddress.setFilter(s -> {
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
        this.addWidget(this.dmxAddress);
        this.ipAddress = new EditBox(this.font, lvt_1_1_ + 40, lvt_2_1_ + 50, 100, 20, new TextComponent(""));
        this.ipAddress.setValue(menu.blockEntity.getIp());
        this.ipAddress.setCanLoseFocus(false);
        this.ipAddress.changeFocus(true);
        this.ipAddress.setTextColor(-1);
        this.ipAddress.setTextColorUneditable(-1);
        this.ipAddress.setBordered(true);
        this.ipAddress.setMaxLength(35);
        this.addWidget(this.ipAddress);
        this.addWidget(new Button(lvt_1_1_ + 40, lvt_2_1_ + 90, 100, 20, new TextComponent("Save"), p_onPress_1_ -> {
            try {
                int dmx = Integer.parseInt(this.dmxAddress.getValue());
                if (dmx > 512 || dmx < 0) {
                    return;
                }
                TheatricalNetworkHandler.MAIN.sendToServer(
                    new UpdateArtNetInterfacePacket(menu.blockEntity.getBlockPos(), dmx,
                        ipAddress.getValue()));
            } catch(NumberFormatException ignored) {
                //We need a nicer way to show that this is invalid?
            }
        }));
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

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.dmxAddress.render(p_230430_1_,p_230430_2_, p_230430_3_,p_230430_4_);
        this.ipAddress.render(p_230430_1_,p_230430_2_, p_230430_3_,p_230430_4_);
        renderTooltip(p_230430_1_, p_230430_2_, p_230430_2_);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        String name = menu.blockEntity.getDisplayName().getString();
        font.draw(matrixStack, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040);
        font.draw(matrixStack, "DMX Universe", imageWidth / 2 - font.width("DMX Universe") / 2, 16, 0x404040);
        font.draw(matrixStack, "ArtNet IP", imageWidth / 2 - font.width("ArtNet IP") / 2, 40, 0x404040);
    }
}
