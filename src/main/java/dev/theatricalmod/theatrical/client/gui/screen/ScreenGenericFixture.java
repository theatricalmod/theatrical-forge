package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.container.ContainerGenericFixture;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateFixturePacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class ScreenGenericFixture extends AbstractContainerScreen<ContainerGenericFixture>  {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/blank.png");

    private ForgeSlider tiltSlider, panSlider;
    private int tilt, pan;

    public ScreenGenericFixture(ContainerGenericFixture screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 126;
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.imageWidth) / 2;
        int lvt_2_1_ = (this.height - this.imageHeight) / 2;
        this.tilt = menu.blockEntity.getTilt();
        this.pan = menu.blockEntity.getPan();
        this.tiltSlider = this.addRenderableWidget(new ForgeSlider(lvt_1_1_ + 13, lvt_2_1_ + 35, 150, 20, new TextComponent(""), new TextComponent(""), -180, 180, tilt, 1, 0, true){
            @Override
            protected void applyValue() {
                onChangeSliderValue(this);
            }
        });
        this.panSlider = this.addRenderableWidget(new ForgeSlider(lvt_1_1_ + 13, lvt_2_1_ + 65, 150, 20, new TextComponent(""), new TextComponent(""), -180, 180, pan, 1, 0, true){
            @Override
            protected void applyValue() {
                onChangeSliderValue(this);
            }
        });
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float partialTicks) {
        this.renderBackground(matrices);
        super.render(matrices, x, y, partialTicks);
        this.renderTooltip(matrices, x, y);
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
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        String name = menu.blockEntity.getDisplayName().getString();
        font.draw(matrixStack, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040);
    }
    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        return false;
    }

    public void onChangeSliderValue(ForgeSlider slider) {
        if (slider.equals(panSlider)) {
            this.pan = slider.getValueInt();
        } else if (slider.equals(tiltSlider)) {
            this.tilt = slider.getValueInt();
        }
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateFixturePacket(menu.blockEntity.getBlockPos(), this.tilt, this.pan));
    }


}
