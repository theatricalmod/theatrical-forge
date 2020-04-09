package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonSocket extends Button {

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/dimmer_rack.png");

    public final ContainerDimmerRack containerDimmerRack;
    private int channelNumber = 0;
    private SocapexPatch patch;
    private boolean isSecondSocket;
    private String patchIdentifier;

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int x, int y, int channelNumber, boolean isSecondSocket, Button.IPressable onPress) {
        super(x, y, 12, 12, "", onPress);
        this.containerDimmerRack = containerDimmerRack;
        this.channelNumber = channelNumber;
        this.isSecondSocket = isSecondSocket;
    }

    public ButtonSocket(ContainerDimmerRack containerDimmerRack, int x, int y, int channelNumber, boolean isSecondSocket, SocapexPatch patch, String patchIdentifier, Button.IPressable onPress) {
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
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(background);
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(x, y, 12, 12, 269, 0, 17, 17, 512, 512);
        if (isPatched()) {
            blit(x, y, 13, 13, 250, 0, 19, 17, 512, 512);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(x + 5, y + 7, 0);
            RenderSystem.scalef(0.7F, 0.7F, 1F);
            fontrenderer.drawString(patchIdentifier + (patch.getReceiverSocket() + 1), 0, 0, 0xFFFFFF);
            RenderSystem.popMatrix();
        }
        if (isHovered) {
            fill(x, y, x + width, y + height, -2130706433);
        }
    }

}
