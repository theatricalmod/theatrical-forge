package dev.theatricalmod.theatrical.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

public interface IDraggable {

    void onDrag(AbstractWidget widget, double mouseX, double mouseY);

}
