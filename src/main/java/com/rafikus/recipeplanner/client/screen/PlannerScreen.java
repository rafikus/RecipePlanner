package com.rafikus.recipeplanner.client.screen;

import com.rafikus.recipeplanner.RecipePlanner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

public class PlannerScreen extends Screen {
    private static final String SCREEN_ID = "gui." + RecipePlanner.MODID + ".planner_screen";
    private static final Component TITLE = Component.translatable(SCREEN_ID);

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    public PlannerScreen() {
        super(TITLE);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if(this.minecraft == null) return;

        addRenderableWidget(
                Button.builder(
                        Component.translatable(SCREEN_ID + ".example_button"),
                        this::handleButtonPress
                        )
                        .bounds(leftPos + 8, topPos + 20, 20, 20)
                        .tooltip(Tooltip.create(TITLE))
                        .build()
        );
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(graphics);

        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(this.font, TITLE, this.leftPos + 8, this.topPos + 6, 0x404040, false);
    }

    private void handleButtonPress(Button button) {
        // Handle button press
    }
}
