package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonPlug extends Button {

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/dimmer_rack.png");

    private final int plugNumber;
    private final String identifier;
    private boolean active;

    public ButtonPlug(int x, int y, int plugNumber, String identifier, boolean active, Button.OnPress onPress) {
        super(x, y, 14, 12, new TextComponent(""), onPress);
        this.plugNumber = plugNumber;
        this.identifier = identifier;
        this.active = active;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableDepthTest();
        blit(matrixStack, x, y, width, height, 250, 0, 19, 17, 512, 512);
        matrixStack.pushPose();
        matrixStack.translate(x + 4, y + 5, 0);
        matrixStack.scale(0.5F, 0.5F, 1);
        mc.font.draw(matrixStack, identifier + plugNumber, 0, 0, 0xFFFFFF);
        matrixStack.popPose();
        if (isHovered) {
            fill(matrixStack,  x, y, x + width, y + height, -2130706433);
        }
        if (active) {
            fill(matrixStack, x, y, x + width, y + height, 0x6666FF66);
        }
        RenderSystem.enableDepthTest();
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
