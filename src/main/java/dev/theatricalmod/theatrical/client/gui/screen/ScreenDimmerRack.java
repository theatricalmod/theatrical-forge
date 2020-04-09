package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonPlug;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonSocket;
import dev.theatricalmod.theatrical.network.ChangeDimmerPatchPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TileEntityDimmerRack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ScreenDimmerRack extends ContainerScreen<ContainerDimmerRack> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/dimmer_rack.png");

    private ContainerDimmerRack inventoryPlayer;
    private TileEntityDimmerRack tileDimmerRack;
    private List<ISocapexReceiver> receivers;
    private TextFieldWidget dmxStartField;
    private int currentPage = 0;

    private int activePlug = -1;

    public ScreenDimmerRack(ContainerDimmerRack container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.inventoryPlayer = container;
        this.tileDimmerRack = container.dimmerRack;
        this.xSize = 250;
        this.ySize = 131;

        receivers = inventoryPlayer.getDevices();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_E || p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeScreen();
        }
        return this.dmxStartField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxStartField.canWrite() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }


    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    public void generateButtons() {
        int width = this.width / 2;
        int height = (this.height - this.ySize) / 2;
        for (int i = 0; i < 6; i++) {
            SocapexPatch[] patch = inventoryPlayer.getPatch(i);
            int x = (width - 95) + 46 * (i < 3 ? i : i - 3);
            int y = height + (i < 3 ? 70 : 25);
            for (int j = 0; j < 2; j++) {
                if (patch == null || j >= patch.length) {
                    ButtonSocket buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j), tileDimmerRack.getDmxStart() + i, j == 1, this::handleSocket);
                    this.addButton(buttonSocket);
                } else {
                    SocapexPatch patch1 = patch[j];
                    if (patch1 != null && patch1.getReceiver() != null) {
                        String identifier = inventoryPlayer.getIdentifier(patch1.getReceiver());
                        this.addButton(new ButtonSocket(inventoryPlayer, x, y + (20 * j), tileDimmerRack.getDmxStart() + i, j == 1, patch1, identifier, this::handleSocket));
                    } else {
                        this.addButton(new ButtonSocket(inventoryPlayer, x, y + (20 * j), tileDimmerRack.getDmxStart() + i, j == 1, this::handleSocket));
                    }
                }
            }
        }
        if (receivers.size() > 0) {
            ISocapexReceiver iSocapexReceiver = receivers.get(currentPage);
            int[] channels = inventoryPlayer.getChannelsForReceiver(iSocapexReceiver);
            for (int i = 0; i < channels.length; i++) {
                int x = width + 45 + (20 * (i < 4 ? i : i - 4));
                int y = height - (i < 4 ? 65 : 45);
                if (channels[i] != 1) {
                    int finalI = i;
                    ButtonPlug buttonPlug = new ButtonPlug(x, y, i + 1, iSocapexReceiver.getIdentifier(), activePlug == i, (button) -> {
                        if (button instanceof ButtonPlug) {
                            ButtonPlug plug = (ButtonPlug) button;
                            if (activePlug == finalI + 1) {
                                plug.setActive(false);
                                activePlug = -1;
                            } else {
                                plug.setActive(true);
                                activePlug = finalI + 1;
                            }
                        }
                    });
                    this.addButton(buttonPlug);
                }
            }
        }
    }


    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        this.addButton(new Button(lvt_1_1_ + 172, lvt_2_1_ + 5, 15, 20, "<", (button) -> {
            if (currentPage - 1 < 0) {
                currentPage = receivers.size() - 1;
            } else {
                currentPage--;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.addButton(new Button(lvt_1_1_ + 165 + 60, lvt_2_1_ + 5, 15, 20, ">", (button) -> {
            if (currentPage + 1 > receivers.size() - 1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.dmxStartField = new TextFieldWidget(font, width + 43, height - 20, 50, 10, "");
        this.dmxStartField.setFocused2(true);
        if (tileDimmerRack.getCapability(DMXReceiver.CAP, Direction.SOUTH).isPresent()) {
            this.dmxStartField.setText(Integer.toString(tileDimmerRack.getCapability(DMXReceiver.CAP, Direction.SOUTH).orElse(null).getStartPoint()));
        }
        generateButtons();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.blit(lvt_4_1_, lvt_5_1_, 0, 0, xSize, ySize, 512, 512);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        RenderSystem.disableBlend();
        this.dmxStartField.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = container.dimmerRack.getDisplayName().getString();
        font
            .drawString(name, 176 / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        font
            .drawString("Plugs", 180 + font.getStringWidth("Plugs") / 2, 6, 0x404040);
        for (int i = 0; i < 6; i++) {
            int x = 33 + 46 * (i < 3 ? i : i - 3);
            int y = (i < 3 ? 15 : 62);
            font.drawString("" + (i + 1), x, y, 0x000000);
        }
        if (receivers.size() > 0) {
            String pageName = "Panel " + receivers.get(currentPage).getIdentifier();
            font
                .drawString(pageName, 170 + font.getStringWidth(
                    pageName
                ) / 2, 17, 0x404040);
        }
        if (activePlug != -1) {
            int width = this.width / 2;
            int height = this.height / 2;
            int plugX = width + 45 + (20 * (activePlug < 4 ? activePlug : activePlug - 4));
            int plugY = height - (activePlug < 4 ? 65 : 45);
            int xDist = plugX - mouseX;
            int yDist = plugY - mouseY;
            float lineWidth = 2;
            if (Minecraft.getInstance().currentScreen != null) {
                long distanceSq = xDist * xDist + yDist * yDist;
                int screenDim = Minecraft.getInstance().currentScreen.width * Minecraft.getInstance().currentScreen.height;
                float percentOfDim = Math.min(1, distanceSq / (float) screenDim);
                lineWidth = 1 + ((1 - (percentOfDim)) * 3);
            }
            final int color = 0x13C90A;
            int red = (color >> 16) & 255;
            int green = (color >> 8) & 255;
            int blue = (color) & 255;
            int alpha = (color >> 24) & 255;
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.lineWidth(3);
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(plugX - guiLeft, plugY - guiTop, 0).color(red, green, blue, 255).endVertex();
            bufferBuilder.pos(mouseX - guiLeft, mouseY - guiTop, 0).color(red, green, blue, 255).endVertex();
            tessellator.draw();
            RenderSystem.enableTexture();
            RenderSystem.enableCull();
        }
    }

    private void handleSocket(Button button) {
        if (button instanceof ButtonSocket) {
            ButtonSocket socket = (ButtonSocket) button;
            int channel = socket.getChannelNumber();
            int socketNumber = socket.isSecondSocket() ? 2 : 1;
            if (activePlug == -1) {
                if (socket.isPatched()) {
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, socketNumber, new SocapexPatch()));
                    generateButtons();
                }
            } else {
                if (!socket.isPatched()) {
                    SocapexPatch patch1 = new SocapexPatch(receivers.get(currentPage).getPos(), activePlug);
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, socketNumber, patch1));
                    activePlug = -1;
                    generateButtons();
                }
            }
        }
    }
}
