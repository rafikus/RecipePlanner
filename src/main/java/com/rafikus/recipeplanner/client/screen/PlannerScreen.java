package com.rafikus.recipeplanner.client.screen;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.widgets.RecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class PlannerScreen extends Screen {
    private static final String SCREEN_ID = "gui." + RecipePlanner.MODID + ".planner_screen";
    private static final Component TITLE = Component.translatable(SCREEN_ID);

    private final int imageWidth, imageHeight;

    private double leftPos, topPos;

    private final Screen previousScreen;

    private final ItemStack item;

    public PlannerScreen(ItemStack item, Screen previousScreen) {
        super(TITLE);
        this.minecraft = Minecraft.getInstance();
        assert this.minecraft.screen != null;
        this.imageWidth = this.minecraft.screen.width;
        this.imageHeight = this.minecraft.screen.height;
        this.item = item;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (double) (this.width - this.imageWidth) / 2;
        this.topPos = (double) (this.height - this.imageHeight) / 2;

        int leftPos = (int) Math.floor(this.leftPos);
        int topPos = (int) Math.floor(this.topPos);

        addRenderableWidget(new RecipeWidget(leftPos + 10, topPos + 10, 100, 100, item));
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int p_94701_, double movedX, double movedY) {
        this.leftPos += movedX;
        this.topPos += movedY;

        return super.mouseDragged(mouseX, mouseY, p_94701_, movedX, movedY);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(this.previousScreen);
    }
}

























