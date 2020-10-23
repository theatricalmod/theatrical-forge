package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDMXRedstoneInterface;
import dev.theatricalmod.theatrical.client.gui.container.ContainerIntelligentFixture;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateDMXAddressPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScreenDMXRedstoneInterface extends ContainerScreen<ContainerDMXRedstoneInterface> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/blank.png");

    private TextFieldWidget dmxAddress;

    public ScreenDMXRedstoneInterface(ContainerDMXRedstoneInterface container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 176;
        this.ySize = 126;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256) {
            this.minecraft.player.closeScreen();
        }
        return this.dmxAddress.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxAddress.canWrite() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }


    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String lvt_4_1_ = this.dmxAddress.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.dmxAddress.setText(lvt_4_1_);
    }

    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        this.dmxAddress = new TextFieldWidget(this.font, lvt_1_1_ + 40, lvt_2_1_ + 50, 100, 20, new StringTextComponent(""));
        container.blockEntity.getCapability(DMXReceiver.CAP).ifPresent(idmxReceiver -> this.dmxAddress.setText(Integer.toString(idmxReceiver.getStartPoint())));
        this.dmxAddress.setCanLoseFocus(false);
        this.dmxAddress.changeFocus(true);
        this.dmxAddress.setTextColor(-1);
        this.dmxAddress.setDisabledTextColour(-1);
        this.dmxAddress.setEnableBackgroundDrawing(true);
        this.dmxAddress.setMaxStringLength(35);
        this.dmxAddress.setValidator(s -> {
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
        this.children.add(this.dmxAddress);
        this.setFocusedDefault(this.dmxAddress);
        this.addButton(new Button(lvt_1_1_ + 40, lvt_2_1_ + 90, 100, 20, new StringTextComponent("Save"), p_onPress_1_ -> {
            int dmx = Integer.parseInt(this.dmxAddress.getText());
            if (dmx > 512 || dmx < 0) {
                return;
            }
            TheatricalNetworkHandler.MAIN.sendToServer(new UpdateDMXAddressPacket(container.blockEntity.getPos(), dmx));
        }));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.blit(matrixStack, lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.dmxAddress.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderHoveredTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        String name = container.blockEntity.getDisplayName().getString();
        font.drawString(matrixStack, name, xSize / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        font.drawString(matrixStack, "DMX Start Address", xSize / 2 - font.getStringWidth("DMX Start Address") / 2, 16, 0x404040);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.dmxAddress.isMouseOver(mouseX, mouseY)){
            dmxAddress.setFocused2(true);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
