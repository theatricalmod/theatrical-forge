package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerIntelligentFixture;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateDMXAddressPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenIntelligentFixture extends AbstractContainerScreen<ContainerIntelligentFixture> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/blank.png");

    private EditBox dmxAddress;

    public ScreenIntelligentFixture(ContainerIntelligentFixture container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 126;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            this.minecraft.player.closeContainer();
        }
        return this.dmxAddress.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxAddress.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }


    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String lvt_4_1_ = this.dmxAddress.getValue();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.dmxAddress.setValue(lvt_4_1_);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.imageWidth) / 2;
        int lvt_2_1_ = (this.height - this.imageHeight) / 2;
        this.dmxAddress = new EditBox(this.font, lvt_1_1_ + 40, lvt_2_1_ + 50, 100, 20, new TextComponent(""));
        menu.blockEntity.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER).ifPresent(idmxReceiver -> this.dmxAddress.setValue(Integer.toString(idmxReceiver.getStartPoint())));
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
        this.setInitialFocus(this.dmxAddress);
        this.addWidget(new Button(lvt_1_1_ + 40, lvt_2_1_ + 90, 100, 20, new TextComponent("Save"), p_onPress_1_ -> {
            int dmx = Integer.parseInt(this.dmxAddress.getValue());
            if (dmx > 512 || dmx < 0) {
                return;
            }
            TheatricalNetworkHandler.MAIN.sendToServer(new UpdateDMXAddressPacket(menu.blockEntity.getBlockPos(), dmx));
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
        this.dmxAddress.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        String name = menu.blockEntity.getDisplayName().getString();
        font.draw(matrixStack, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040);
        font.draw(matrixStack, "DMX Start Address", imageWidth / 2 - font.width("DMX Start Address") / 2, 16, 0x404040);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.dmxAddress.isMouseOver(mouseX, mouseY)){
            dmxAddress.setFocus(true);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
