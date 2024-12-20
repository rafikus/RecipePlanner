package com.rafikus.recipeplanner.jei;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

@JeiPlugin
public class JEIConfig implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<RecipeCategory, IRecipeCategory<?>> CATEGORY_MAP = Maps.newHashMap();
    private static ISubtypeManager subtypeManager;
    public static IJeiRuntime runtime;
    public static BiPredicate<IIngredientTypeWithSubtypes<? extends Object, ? extends Object>, Object> hasSubtype = (a, b) -> true;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("recipeplanner", "jei");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEIConfig.runtime = jeiRuntime;

        List<IRecipeCategory<?>> categories = runtime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get().toList();
        Set<String> recipeCategories = new HashSet<>();
        for (IRecipeCategory<?> c : categories) {
            LOGGER.info("Collecting data for " + c.getTitle().getString());
            try {
                RecipeType type = c.getRecipeType();
                ResourceLocation id = type.getUid();
                List<?> recipes = runtime.getRecipeManager().createRecipeLookup(type).includeHidden().get().toList();
                for (Object r : recipes) {
                    if (r instanceof ShapelessRecipe) {
                        ShapelessRecipe recipe = (ShapelessRecipe) r;
                        LOGGER.info("Shapeless Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                        List<Ingredient> ingredients = recipe.getIngredients();
                        for (Ingredient i : ingredients) {
                            ItemStack[] stacks = i.getItems();
                            for (ItemStack s : stacks) {
                                LOGGER.info("Ingredient: {}x{}", s.getCount(), s.getDisplayName().getString());
                            }
                        }

                    } else if (r instanceof ShapedRecipe) {
                        ShapedRecipe recipe = (ShapedRecipe) r;
                        LOGGER.info("Shaped Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                        List<Ingredient> ingredients = recipe.getIngredients();
                        for (Ingredient i : ingredients) {
                            ItemStack[] stacks = i.getItems();
                            for (ItemStack s : stacks) {
                                LOGGER.info("Ingredient: {}x{}", s.getCount(), s.getDisplayName().getString());
                            }
                        }
                    } else if (r instanceof SmithingRecipe) {
                        SmithingRecipe recipe = (SmithingRecipe) r;
                        LOGGER.info("Smithing Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else if (r instanceof SmithingTrimRecipe) {
                        SmithingTrimRecipe recipe = (SmithingTrimRecipe) r;
                        LOGGER.info("Smithing Trim Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else if (r instanceof BlastingRecipe) {
                        BlastingRecipe recipe = (BlastingRecipe) r;
                        LOGGER.info("Blasting Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else if (r instanceof CampfireCookingRecipe) {
                        CampfireCookingRecipe recipe = (CampfireCookingRecipe) r;
                        LOGGER.info("Campfire Cooking Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else if (r instanceof SmeltingRecipe) {
                        SmeltingRecipe recipe = (SmeltingRecipe) r;
                        LOGGER.info("Smelting Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else if (r instanceof SmokingRecipe) {
                        SmokingRecipe recipe = (SmokingRecipe) r;
                        LOGGER.info("Smoking Recipe: " + recipe.getResultItem(RegistryAccess.EMPTY).getDisplayName().getString());
                    } else {
                        LOGGER.info("Unknown Recipe Type: " + r.getClass().getName());
                        String[] parts = r.getClass().getName().split("\\.");
                        recipeCategories.add(parts[parts.length - 1]);
                    }
                }
            } catch(Throwable t) {
                t.printStackTrace();
            }
            LOGGER.info("Done collecting data for " + c.getTitle().getString());
        }
        for (String s : recipeCategories) {
            LOGGER.info("Recipe Category: " + s);
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        JEIConfig.runtime = null;
    }

}
