package com.rafikus.recipeplanner.widgets;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.jei.JEIConfig;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.phys.Vec2;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Stream;

public class RecipeWidget extends AbstractWidget {
    private int x, y;
    private final int xPad, yPad;

    private final Minecraft minecraft;

    @SuppressWarnings("FieldCanBeLocal")
    private final IJeiHelpers jeiHelpers;
    private final IFocusFactory focusFactory;
    private final IRecipeManager recipeManager;
    private final IGuiHelper guiHelper;

    private final Map<IRecipeCategory<?>, List<Recipe<?>>> recipeMap = new HashMap<>();
    private final Map<Recipe<?>, IRecipeLayoutDrawable<Recipe<?>>> recipeLayoutMap = new HashMap<>();
    private int currentRecipeCategory = 0;
    private int currentRecipe = 0;

    private final Map<Button, Vec2> buttons = new HashMap<>();

    public RecipeWidget(int globalX, int globalY, int x, int y, ItemStack item) {
        super(x, y, 0, 0, Component.translatable("widget." + RecipePlanner.MODID + ".recipe"));
        this.x = globalX;
        this.y = globalY;
        this.xPad = x;
        this.yPad = y;
        this.minecraft = Minecraft.getInstance();

        // Found this solution on discord
        // https://discord.com/channels/358816755646332941/358816756149518336/1242319861401653301
        this.jeiHelpers = JEIConfig.runtime.getJeiHelpers();
        this.focusFactory = jeiHelpers.getFocusFactory();
        this.recipeManager = JEIConfig.runtime.getRecipeManager();
        this.guiHelper = jeiHelpers.getGuiHelper();

        Collection<IFocus<ItemStack>> focus = new ArrayList<>();
        IFocus<ItemStack> recipeItem = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, item);
        focus.add(recipeItem);

        Stream<IRecipeCategory<?>> recipeCategoryStream = recipeManager.createRecipeCategoryLookup().limitFocus(focus).get();
        recipeCategoryStream.forEach(recipeCategory -> {
            Stream<?> list = recipeManager.createRecipeLookup(recipeCategory.getRecipeType()).limitFocus(focus).get();
            list.forEach(recipe -> {
                assert minecraft.screen != null;
                try {
                    if (recipe instanceof IJeiAnvilRecipe) {
                        return;
                    }
                    if (recipe instanceof Recipe) {
                        try {
                            if (!recipeMap.containsKey(recipeCategory)) {
                                recipeMap.put(recipeCategory, new ArrayList<>());
                            }

                            recipeMap.get(recipeCategory).add((Recipe<?>) recipe);
                            //noinspection unchecked
                            Optional<IRecipeLayoutDrawable<Recipe<?>>> recipeLayoutDrawable = this.recipeManager
                                    .createRecipeLayoutDrawable(
                                            (IRecipeCategory<Recipe<?>>) recipeCategory,
                                            (Recipe<?>) recipe,
                                            focusFactory.createFocusGroup(focus));

                            recipeLayoutDrawable.ifPresent(r -> recipeLayoutMap.put((Recipe<?>) recipe, r));
                        } catch (Exception e) {
                            RecipePlanner.LOGGER.error(e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    RecipePlanner.LOGGER.error(e.getMessage());
                }
            });
        });
        for (IRecipeCategory<?> category : recipeMap.keySet()) {
            if (recipeMap.get(category).isEmpty()) {
                recipeMap.remove(category);
            }
        }

        final int BUTTON_SPACE = 64;
        final int BUTTON_SIZE = 16;

        if (buttons.isEmpty()) {
            buttons.put(Button
                    .builder(
                            Component.translatable("<"),
                            button -> changeCategory(-1))
                    .pos(
                            xPad - BUTTON_SPACE - BUTTON_SIZE,
                            yPad + 8)
                    .size(
                            BUTTON_SIZE,
                            BUTTON_SIZE)
                    .build(), new Vec2(-BUTTON_SPACE - BUTTON_SIZE, 8));
            buttons.put(Button
                    .builder(
                            Component.translatable(">"),
                            button -> changeCategory(1))
                    .pos(
                            xPad + BUTTON_SPACE,
                            yPad + 8)
                    .size(
                            BUTTON_SIZE,
                            BUTTON_SIZE)
                    .build(), new Vec2(BUTTON_SPACE, 8));

            buttons.put(Button
                    .builder(
                            Component.translatable("<<"),
                            button -> changeRecipe(-1))
                    .pos(
                            xPad - BUTTON_SPACE - BUTTON_SIZE,
                            yPad + 32)
                    .size(
                            BUTTON_SIZE,
                            BUTTON_SIZE)
                    .build(), new Vec2(-BUTTON_SPACE - BUTTON_SIZE, 32));
            buttons.put(Button
                    .builder(
                            Component.translatable(">>"),
                            button -> changeRecipe(1))
                    .pos(
                            xPad + BUTTON_SPACE,
                            yPad + 32)
                    .size(
                            16,
                            16)
                    .build(), new Vec2(BUTTON_SPACE, 32));
        }
    }

    public Set<Button> getButtons() {
        return buttons.keySet();
    }

    public void changeCategory(int direction) {
        currentRecipeCategory += direction;
        if (currentRecipeCategory < 0) {
            currentRecipeCategory = recipeMap.size() - 1;
        }
        if (currentRecipeCategory >= recipeMap.size()) {
            currentRecipeCategory = 0;
        }
        currentRecipe = 0;
    }

    public void changeRecipe(int direction) {
        currentRecipe += direction;
        IRecipeCategory<?> currentRecipeCategory = (IRecipeCategory<?>) recipeMap.keySet().toArray()[this.currentRecipeCategory];
        List<Recipe<?>> recipeList = recipeMap.get(currentRecipeCategory);
        if (currentRecipe < 0) {
            currentRecipe = recipeList.size() - 1;
        }
        if (currentRecipe >= recipeList.size()) {
            currentRecipe = 0;
        }
    }

    public void updatePosition(double x, double y) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);

        buttons.keySet().forEach(button -> {
            Vec2 offset = buttons.get(button);
            button.setPosition(this.x + xPad + (int) Math.floor(offset.x), this.y + yPad + (int) Math.floor(offset.y));
        });
    }

    private float time = 0;

    @ParametersAreNonnullByDefault
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float timePassed) {
        time += timePassed;

        int topPad = y + yPad + 64;

        IRecipeCategory<?> recipeCategory = (IRecipeCategory<?>) recipeMap.keySet().toArray()[currentRecipeCategory];
        List<Recipe<?>> recipes = recipeMap.get(recipeCategory);
        Recipe<?> recipe = recipes.get(currentRecipe);

        IRecipeLayoutDrawable<Recipe<?>> recipeLayoutDrawable = recipeLayoutMap.get(recipe);
        recipeLayoutDrawable.setPosition(x + xPad - recipeLayoutDrawable.getRectWithBorder().getWidth() / 2, topPad);
        recipeLayoutDrawable.drawRecipe(graphics, mouseX, mouseY);
        recipeLayoutDrawable.drawOverlays(graphics, mouseX, mouseY);

        graphics.drawCenteredString(this.minecraft.font, recipeCategory.getTitle(), x + xPad, y + yPad + 12, 0xFFFFFF);
        graphics.drawCenteredString(this.minecraft.font, currentRecipe + 1 + "/" + recipes.size(), x + xPad, y + yPad + 36, 0xFFFFFF);


        List<ITypedIngredient<?>> catalysts =
                recipeManager
                        .createRecipeCatalystLookup(
                                recipeLayoutDrawable
                                        .getRecipeCategory()
                                        .getRecipeType())
                        .get().toList();
        int xOffset = 1;
        int yOffset = 0;
        for (ITypedIngredient<?> ingredient : catalysts) {
            if (ingredient.getItemStack().isPresent()) {
                guiHelper.getSlotDrawable().draw(graphics, -2 + recipeLayoutDrawable.getRectWithBorder().getX() - 18 * xOffset, -2 + topPad + yOffset * 18);
                IRecipeSlotDrawable slotDrawable = recipeManager.createRecipeSlotDrawable(
                        RecipeIngredientRole.CATALYST, List.of(Optional.of(ingredient)), IntSet.of(0), 0);
                slotDrawable.setPosition(-1 + recipeLayoutDrawable.getRectWithBorder().getX() - 18 * xOffset, -1 + topPad + yOffset * 18);
                slotDrawable.draw(graphics);
                if (slotDrawable.isMouseOver(mouseX, mouseY)) {
                    slotDrawable.drawHoverOverlays(graphics);
                }
                yOffset++;
                if (yOffset * 20 + 18 > recipeLayoutDrawable.getRectWithBorder().getHeight()) {
                    xOffset++;
                    yOffset = 0;
                }
            }
        }

        if (time > 1) {
            time = 0;
            recipeLayoutDrawable.tick();
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
