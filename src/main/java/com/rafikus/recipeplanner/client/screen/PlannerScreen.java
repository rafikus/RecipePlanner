package com.rafikus.recipeplanner.client.screen;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.client.handler.ClientForgeHandler;
import com.rafikus.recipeplanner.jei.JEIConfig;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class PlannerScreen extends Screen {
    private static final String SCREEN_ID = "gui." + RecipePlanner.MODID + ".planner_screen";
    private static final Component TITLE = Component.translatable(SCREEN_ID);

    private final int imageWidth, imageHeight;

    private double leftPos, topPos;

    private final Screen previousScreen;

    private final ItemStack item;
    private final List<Recipe<?>> recipe;

    public PlannerScreen(ItemStack item, Screen previousScreen) {
        super(TITLE);
        this.minecraft = Minecraft.getInstance();
        assert this.minecraft.screen != null;
        this.imageWidth = this.minecraft.screen.width;
        this.imageHeight = this.minecraft.screen.height;
        this.item = item;
        this.previousScreen = previousScreen;
        this.recipe = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (double) (this.width - this.imageWidth) / 2;
        this.topPos = (double) (this.height - this.imageHeight) / 2;


        List<IRecipeCategory<?>> categories = JEIConfig.runtime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get().toList();
        for (IRecipeCategory<?> c : categories) {
            RecipeType<?> type = c.getRecipeType();
            List<?> recipes = JEIConfig.runtime.getRecipeManager().createRecipeLookup(type).get().toList();
            for (Object r : recipes) {
                if (r instanceof Recipe<?> queriedRecipe) {
                    try {
                        assert minecraft != null;
                        assert minecraft.level != null;
                        if (queriedRecipe.getResultItem(minecraft.level.registryAccess()).is(item.getItem())) {
                            RecipePlanner.LOGGER.info("Found recipe: " + queriedRecipe);
                            this.recipe.add(queriedRecipe);
                        }
                    } catch (Exception e) {
                        RecipePlanner.LOGGER.error("Error: " + e);
                    }
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(graphics);

        int leftPos = (int) Math.floor(this.leftPos);
        int topPos = (int) Math.floor(this.topPos);

        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.renderItem(item, leftPos + 8, topPos + 20);

        for (int i = 0; i < recipe.size(); i++) {
            Recipe<?> r = recipe.get(i);
            assert minecraft != null;
            assert minecraft.level != null;
            NonNullList<Ingredient> ingredients = r.getIngredients();
            Map<Ingredient, Integer> countedIngredients = sumIngredients(ingredients);
            Set<Ingredient> ingredientSet = countedIngredients.keySet();

            int j = 0;
            for (Ingredient ingredient : ingredientSet) {
                ItemStack[] itemStacks = ingredient.getItems();
                if (itemStacks.length == 0) {
                    continue;
                }
                int current = ClientForgeHandler.secondsPassed % itemStacks.length;

                if (itemStacks[current].isEmpty()) {
                    continue;
                }

                itemStacks[current].setCount(countedIngredients.get(ingredient));

                graphics.renderItem(itemStacks[current], leftPos + 8 + 20 * j, topPos + 20 + 20 * (i + 1));
                graphics.renderItemDecorations(this.font, itemStacks[current], leftPos + 8 + 20 * j, topPos + 20 + 20 * (i + 1));
                j++;
            }
        }
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

    private Map<Ingredient, Integer> sumIngredients(NonNullList<Ingredient> ingredients) {
        Map<Ingredient, Integer> ingredientCountMap = new HashMap<>();

        for (Ingredient ingredient : ingredients) {
            ingredientCountMap.merge(ingredient, 1, Integer::sum);
        }

        return ingredientCountMap;
    }
}

























