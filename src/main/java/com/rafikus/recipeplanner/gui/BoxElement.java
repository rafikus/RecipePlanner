package com.rafikus.recipeplanner.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.world.phys.Vec2;

import java.awt.*;
import java.util.Objects;

public class BoxElement extends Element implements IBoxElement {
    private final Tooltip tooltip;
    private int[] padding;

    public BoxElement(Tooltip tooltip) {
        this.tooltip = Objects.requireNonNull(tooltip);
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(10, 10);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, "Test String", 0, 0, 0xFFFFFF);
        guiGraphics.pose().popPose();
    }

    @Override
    public int padding(ScreenDirection direction) {
        return padding[direction.ordinal()];
    }

    @Override
    public void setPadding(ScreenDirection direction, int value) {
        padding[direction.ordinal()] = value;
    }
}
