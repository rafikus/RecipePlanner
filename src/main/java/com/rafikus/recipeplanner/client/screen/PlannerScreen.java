package com.rafikus.recipeplanner.client.screen;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.widgets.RecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class PlannerScreen extends Screen {
    private static final String SCREEN_ID = "gui." + RecipePlanner.MODID + ".planner_screen";
    private static final Component TITLE = Component.translatable(SCREEN_ID);

    private double x, y;

    private final Screen previousScreen;

    private final ItemStack item;

    private final List<RecipeWidget> widgets = new ArrayList<>();

    public PlannerScreen(ItemStack item, Screen previousScreen) {
        super(TITLE);
        this.minecraft = Minecraft.getInstance();
        assert this.minecraft.screen != null;
        this.width = this.minecraft.screen.width;
        this.height = this.minecraft.screen.height;
        this.item = item;
        this.previousScreen = previousScreen;

        addRenderableWidget(Button
                .builder(
                        Component.translatable("gui." + RecipePlanner.MODID + ".button.center"),
                        button -> setPos(0, 0))
                .pos(
                        this.width - 50,
                        this.height - 20)
                .size(
                        50,
                        20)
                .build());

    }

    @Override
    protected void init() {
        super.init();

        this.x = 0;
        this.y = 0;

        int x = (int) Math.floor(this.x);
        int y = (int) Math.floor(this.y);

        widgets.add(new RecipeWidget(x, y, x + this.width / 2, y + 10, item));

        widgets.forEach(this::addRenderableWidget);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        widgets.forEach(widget -> widget.updatePosition(this.x, this.y));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int p_94701_, double movedX, double movedY) {
        setPos(this.x + movedX, this.y + movedY);

        return super.mouseDragged(mouseX, mouseY, p_94701_, movedX, movedY);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(this.previousScreen);
    }
}

























