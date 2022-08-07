package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonPlug;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonSocket;
import dev.theatricalmod.theatrical.network.ChangeDimmerPatchPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateDMXAddressPacket;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ScreenDimmerRack extends AbstractContainerScreen<ContainerDimmerRack> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/dimmer_rack.png");

    private final ContainerDimmerRack inventoryPlayer;
    private final TileEntityDimmerRack tileDimmerRack;
    private final List<ISocapexReceiver> receivers;
    private EditBox dmxStartField;
    private final List<ButtonSocket> sockets;
    private final List<ButtonPlug> plugs;
    private int currentPage = 0;

    private int activePlug = -1;

    public ScreenDimmerRack(ContainerDimmerRack container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.inventoryPlayer = container;
        this.tileDimmerRack = container.dimmerRack;
        this.imageWidth = 250;
        this.imageHeight = 131;

        sockets = new ArrayList<>();
        plugs = new ArrayList<>();
        receivers = inventoryPlayer.getDevices();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_E || p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            this.removed();
            this.getMinecraft().player.closeContainer();
            return true;
        }
        return this.dmxStartField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxStartField.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        generateButtons();
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    public void generateButtons() {
        sockets.forEach(this::removeWidget);
        plugs.forEach(this::removeWidget);
        this.plugs.clear();
        this.sockets.clear();
        int width = this.width / 2;
        int height = (this.height - this.imageHeight) / 2;
        for (int i = 0; i < 6; i++) {
            SocapexPatch[] patch = inventoryPlayer.getPatch(i);
            int x = (width - 95) + 46 * (i < 3 ? i : i - 3);
            int y = height + (i < 3 ? 25 : 70);
            for (int j = 0; j < 2; j++) {
                ButtonSocket buttonSocket;
                if (patch == null || j >= patch.length) {
                    buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j), i, j == 1, this::handleSocket);
                } else {
                    SocapexPatch patch1 = patch[j];
                    if (patch1 != null && patch1.getReceiver() != null) {
                        String identifier = inventoryPlayer.getIdentifier(patch1.getReceiver()).toUpperCase().substring(0, 1);
                        buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j), i, j == 1, patch1, identifier, this::handleSocket);
                    } else {
                        buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j),  i, j == 1, this::handleSocket);
                    }
                }
                this.addWidget(buttonSocket);
                sockets.add(buttonSocket);
            }
        }
        if (receivers.size() > 0) {
            ISocapexReceiver iSocapexReceiver = receivers.get(currentPage);
            inventoryPlayer.getChannelsForReceiver(iSocapexReceiver).ifPresent(channels -> {
                for (int i = 0; i < channels.length; i++) {
                    int x = width + 45 + (20 * (i < 3 ? i : i - 3));
                    int y = height + (i < 3 ? 45 : 65);
                    if (channels[i] != 1) {
                        int finalI = i;
                        ButtonPlug buttonPlug = new ButtonPlug(x, y, i + 1, "", activePlug == i, (button) -> {
                            if (button instanceof ButtonPlug) {
                                ButtonPlug plug = (ButtonPlug) button;
                                if (activePlug == finalI) {
                                    plug.setActive(false);
                                    activePlug = -1;
                                } else {
                                    plug.setActive(true);
                                    activePlug = finalI;
                                }
                            }
                        });
                        this.addWidget(buttonPlug);
                        plugs.add(buttonPlug);
                    }
                }
            });
        }
    }


    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.imageWidth) / 2;
        int lvt_2_1_ = (this.height - this.imageHeight) / 2;
        this.addWidget(new Button(lvt_1_1_ + 172, lvt_2_1_ + 5, 15, 20, new TextComponent("<"), (button) -> {
            if (currentPage - 1 < 0) {
                currentPage = receivers.size() - 1;
            } else {
                currentPage--;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.addWidget(new Button(lvt_1_1_ + 165 + 60, lvt_2_1_ + 5, 15, 20, new TextComponent(">"), (button) -> {
            if (currentPage + 1 > receivers.size() - 1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.dmxStartField = new EditBox(this.font, lvt_1_1_ + 172, lvt_2_1_ + 100, 50, 10, new TextComponent(""));
        if (tileDimmerRack.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).isPresent()) {
            this.dmxStartField.setValue(Integer.toString(tileDimmerRack.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).orElse(null).getStartPoint()));
        }
        this.dmxStartField.setCanLoseFocus(false);
        this.dmxStartField.changeFocus(true);
        this.dmxStartField.setTextColor(-1);
        this.dmxStartField.setTextColorUneditable(-1);
        this.dmxStartField.setBordered(true);
        this.dmxStartField.setMaxLength(35);
        this.dmxStartField.setFilter(s -> {
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
        this.addWidget(this.dmxStartField);
        this.setInitialFocus(this.dmxStartField);
        generateButtons();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.dmxStartField.isMouseOver(mouseX, mouseY)){
            this.dmxStartField.setFocus(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.leftPos;
        int lvt_5_1_ = (this.height - this.imageHeight) / 2;
        blit(matrixStack, lvt_4_1_, lvt_5_1_, 0, 0, imageWidth, imageHeight, 512, 512);
    }

    @Override
    public void render(PoseStack ms, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(ms);
        super.render(ms, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.dmxStartField.render(ms, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(ms,  p_230430_2_, p_230430_3_);
    }


    @Override
    public void removed() {
        super.removed();
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateDMXAddressPacket(this.menu.dimmerRack.getBlockPos(), Integer.parseInt(this.dmxStartField.getValue())));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        String name = menu.dimmerRack.getDisplayName().getString();
        font.draw(matrixStack, name, 176 / 2 - font.width(name) / 2, 6, 0x404040);
        font.draw(matrixStack, "Plugs", 180 + font.width("Plugs") / 2, 6, 0x404040);
        for (int i = 0; i < 6; i++) {
            int x1 = 33 + 46 * (i < 3 ? i : i - 3);
            int y1 = (i < 3 ? 15 : 62);
            font.draw(matrixStack, "" + (i + 1), x1, y1, 0x000000);
        }
        if (receivers.size() > 0) {
            String pageName = "Panel " + inventoryPlayer.getIdentifier(receivers.get(currentPage).getReceiverPos());
            font
                    .draw(matrixStack, pageName, 150 + font.width(
                            pageName
                    ) / 2, 30, 0x404040);
        }
        if (activePlug != -1) {
            int width = this.width / 2;
            int height = (this.height - this.imageHeight) / 2;
            int plugX = width + 45 + (20 * (activePlug < 3 ? activePlug : activePlug - 3));
            int plugY = height + (activePlug < 3 ? 45 : 65);
            int xDist = plugX - x;
            int yDist = plugY - y;
            if (Minecraft.getInstance().screen != null) {
                long distanceSq = xDist * xDist + yDist * yDist;
                int screenDim = Minecraft.getInstance().screen.width * Minecraft.getInstance().screen.height;
                float percentOfDim = Math.min(1, distanceSq / (float) screenDim);
            }
            final int color = 0x13C90A;
            int red = (color >> 16) & 255;
            int green = (color >> 8) & 255;
            int blue = (color) & 255;
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.lineWidth(3);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(plugX - leftPos, plugY - topPos, 0).color(red, green, blue, 255).endVertex();
            bufferBuilder.vertex(x - leftPos, y - topPos, 0).color(red, green, blue, 255).endVertex();
            tessellator.end();
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
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getBlockPos(), channel, socketNumber, new SocapexPatch()));
                    generateButtons();
                }
            } else {
                if (!socket.isPatched()) {
                    SocapexPatch patch1 = new SocapexPatch(receivers.get(currentPage).getReceiverPos(), activePlug);
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getBlockPos(), channel, socketNumber, patch1));
                    activePlug = -1;
                    generateButtons();
                }
            }
        }
    }
}
