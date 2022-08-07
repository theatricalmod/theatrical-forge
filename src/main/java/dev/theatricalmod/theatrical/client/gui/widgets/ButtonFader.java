package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.IDraggable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ButtonFader extends AbstractWidget{

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/lighting_console.png");

    private final int channel;
    private int value;

    private boolean dragging = false;

    public final IDraggable onDrag;

    public ButtonFader(int x, int y, int channel, int value, IDraggable draggable) {
        super(x, y, 10, 51, new TextComponent(""));
        this.channel = channel;
        this.value = value;
        this.onDrag = draggable;
    }

    @Override
    public void renderButton(PoseStack p_230431_1_, int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.disableDepthTest();
        blit(p_230431_1_, x, y, width, height, 0, 126, 10, 51, 256, 256);
        blit(p_230431_1_,x + 1, (y + (height - 7)) - (int) ((this.value / 255f) * 50), 8, 11, 10, 126, 8, 11, 256, 256);
        RenderSystem.enableDepthTest();
    }

    public int getChannel() {
        return channel;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.value = (int) (((this.height - (mouseY - this.y)) / this.height) * 255f);
        this.dragging = true;
    }

    @Override
    public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
        this.dragging = false;
    }

    public int calculateNewValue(double mouseY){
        return (int) (((this.height - (mouseY - this.y)) / this.height) * 255f);
    }

    public boolean isDragging(){
        return this.dragging;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}
