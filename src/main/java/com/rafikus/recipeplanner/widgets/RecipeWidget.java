package com.rafikus.recipeplanner.widgets;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.jei.JEIConfig;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Stream;

public class RecipeWidget extends AbstractWidget {
    private int x, y;
    private final int xPad, yPad;

    private final Minecraft minecraft;

    private final List<IRecipeLayoutDrawable<?>> recipes;

    private final IJeiHelpers jeiHelpers;
    private final IFocusFactory focusFactory;
    private final IRecipeManager recipeManager;

    public RecipeWidget(int globalX, int globalY, int x, int y, ItemStack item) {
        super(x, y, 0, 0, Component.translatable("widget." + RecipePlanner.MODID + ".recipe"));
        this.x = globalX;
        this.y = globalY;
        this.xPad = x;
        this.yPad = y;
        this.minecraft = Minecraft.getInstance();
        this.recipes = new ArrayList<>();

        // Found this solution on discord
        // https://discord.com/channels/358816755646332941/358816756149518336/1242319861401653301
        this.jeiHelpers = JEIConfig.runtime.getJeiHelpers();
        this.focusFactory = jeiHelpers.getFocusFactory();
        this.recipeManager = JEIConfig.runtime.getRecipeManager();

        Collection<IFocus<ItemStack>> focus = new ArrayList<>();
        IFocus<ItemStack> recipeItem = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, item);
        focus.add(recipeItem);

        Stream<IRecipeCategory<?>> recipeCategoryStream = recipeManager.createRecipeCategoryLookup().limitFocus(focus).get();
        recipeCategoryStream.forEach(recipeCategory -> {
            Stream<?> list = recipeManager.createRecipeLookup(recipeCategory.getRecipeType()).limitFocus(focus).get();
            list.forEach(recipe -> {
                assert minecraft.screen != null;
                try {
                    if (recipe instanceof IJeiAnvilRecipe anvilRecipe) {
                        //noinspection unchecked
                        Optional<IRecipeLayoutDrawable<IJeiAnvilRecipe>> recipeLayoutDrawable = this.recipeManager
                                .createRecipeLayoutDrawable(
                                        (IRecipeCategory<IJeiAnvilRecipe>) recipeCategory,
                                        anvilRecipe,
                                        focusFactory.createFocusGroup(focus));

                        recipeLayoutDrawable.ifPresent(recipes::add);
                        return;
                    }
                    //noinspection unchecked
                    Optional<IRecipeLayoutDrawable<Recipe<?>>> recipeLayoutDrawable = this.recipeManager
                            .createRecipeLayoutDrawable(
                                    (IRecipeCategory<Recipe<?>>) recipeCategory,
                                    (Recipe<?>) recipe,
                                    focusFactory.createFocusGroup(focus));

                    recipeLayoutDrawable.ifPresent(recipes::add);

                } catch (Exception e) {
                    RecipePlanner.LOGGER.error(e.getMessage());
                }
            });
        });
    }

    public void updatePosition(double x, double y) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
    }

    private float time = 0;

    @ParametersAreNonnullByDefault
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        time += partialTicks;

        final int padding = 12;
        int nextY = y + yPad + padding;

        for (IRecipeLayoutDrawable<?> recipe : recipes) {

            Object recipe1 = recipe.getRecipe();

            recipe.drawRecipe(graphics, mouseX, mouseY);
            recipe.drawOverlays(graphics, mouseX, mouseY);
            assert minecraft.screen != null;
            recipe.setPosition(x + xPad - recipe.getRectWithBorder().getWidth() / 2,  nextY);

            if (recipe1 instanceof IJeiAnvilRecipe anvilRecipe) {
                RecipePlanner.LOGGER.info("Anvil Recipe: " + anvilRecipe.getOutputs());
            } else {
                Item item = ((Recipe<?>) recipe1)
                        .getResultItem(RegistryAccess.EMPTY)
                        .getItem();
                jeiHelpers.getGuiHelper().createDrawableItemLike(
                                item)
                        .draw(graphics, recipe.getRectWithBorder().getX() - 20, nextY - 8);
            }

            nextY += padding + recipe.getRectWithBorder().getHeight();
            if (time > 1) {
                time = 0;
                recipe.tick();
            }
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
