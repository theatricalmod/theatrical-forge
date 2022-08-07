package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ButtonSocket extends Button {

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/dimmer_rack.png");

    public final ContainerDimmerRack containerDimmerRack;
    private int channelNumber = 0;
    private SocapexPatch patch;
    private boolean isSecondSocket;
    private String patchIdentifier;

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int x, int y, int channelNumber, boolean isSecondSocket, Button.OnPress onPress) {
        super(x, y, 12, 12, new TextComponent(""), onPress);
        this.containerDimmerRack = containerDimmerRack;
        this.channelNumber = channelNumber;
        this.isSecondSocket = isSecondSocket;
    }

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int x, int y, int channelNumber, boolean isSecondSocket, SocapexPatch patch, String patchIdentifier, Button.OnPress onPress) {
        this(containerDimmerRack, x, y, channelNumber, isSecondSocket, onPress);
        this.patch = patch;
        this.patchIdentifier = patchIdentifier;
        this.isSecondSocket = isSecondSocket;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public boolean isSecondSocket() {
        return isSecondSocket;
    }

    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    public boolean isPatched() {
        return patch != null && patch.getReceiver() != null;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        Font fontrenderer = mc.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrixStack, x, y, 12, 12, 269, 0, 17, 17, 512, 512);
        if (isPatched()) {
            blit(matrixStack, x, y, 13, 13, 250, 0, 19, 17, 512, 512);
            matrixStack.pushPose();
            matrixStack.translate(x + 5, y+ 7, 0);
            matrixStack.scale(0.7f, .7f, 1);
            fontrenderer.draw(matrixStack, patchIdentifier + (patch.getReceiverSocket() + 1), 0, 0, 0xFFFFFF);
            matrixStack.popPose();
        }
        if (isHovered) {
            fill(matrixStack, x, y, x + width, y + height, -2130706433);
        }
    }


}
