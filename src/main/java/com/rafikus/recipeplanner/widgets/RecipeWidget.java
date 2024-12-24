package com.rafikus.recipeplanner.widgets;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.client.handler.ClientForgeHandler;
import com.rafikus.recipeplanner.jei.JEIConfig;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Stream;

public class RecipeWidget extends AbstractWidget {
    private final int x, y;

    private final Minecraft minecraft;

    private final List<IRecipeLayoutDrawable<?>> recipes;

    private final IFocusFactory focusFactory;
    private final IRecipeManager recipeManager;

    public RecipeWidget(int x, int y, int width, int height, ItemStack item) {
        super(x, y, width, height, Component.translatable("widget." + RecipePlanner.MODID + ".recipe"));
        this.x = x;
        this.y = y;
        this.minecraft = Minecraft.getInstance();
        this.recipes = new ArrayList<>();

        // Found this solution on discord
        // https://discord.com/channels/358816755646332941/358816756149518336/1242319861401653301
        IJeiHelpers jeiHelpers = JEIConfig.runtime.getJeiHelpers();
        this.focusFactory = jeiHelpers.getFocusFactory();
        this.recipeManager = JEIConfig.runtime.getRecipeManager();

        Collection<IFocus<ItemStack>> focus = new ArrayList<>();
        IFocus<ItemStack> recipeItem = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, item);
        focus.add(recipeItem);

        Stream<IRecipeCategory<?>> recipeCategoryStream = recipeManager.createRecipeCategoryLookup().limitFocus(focus).get();
        recipeCategoryStream.forEach(c -> {
            Stream<?> list = recipeManager.createRecipeLookup(c.getRecipeType()).limitFocus(focus).get();
            list.forEach(recipe -> {
                if (recipe instanceof CraftingRecipe craftingRecipe) {
                    assert minecraft.screen != null;
                    try {
                        //noinspection unchecked
                        Optional<IRecipeLayoutDrawable<Recipe<?>>> recipeLayoutDrawable = this.recipeManager
                                .createRecipeLayoutDrawable(
                                        (IRecipeCategory<Recipe<?>>) c,
                                        craftingRecipe,
                                        focusFactory.createFocusGroup(focus));
                        recipeLayoutDrawable.ifPresent(r -> {
                            recipes.add(r);
                            ClientForgeHandler.registerMethod(r::tick);
                        });

                    } catch (Exception e) {
                        RecipePlanner.LOGGER.error(e.getMessage());
                    }
                }
            });
        });
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        for (IRecipeLayoutDrawable<?> recipe : recipes) {
            recipe.drawRecipe(graphics, mouseX, mouseY);
            assert minecraft.screen != null;
            recipe.setPosition(x + minecraft.screen.width / 2 - recipe.getRect().getWidth() / 2, y + minecraft.screen.height / 2 - recipe.getRect().getHeight() / 2);
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
