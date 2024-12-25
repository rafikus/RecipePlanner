package com.rafikus.recipeplanner.client.handler;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.client.Keybindings;
import com.rafikus.recipeplanner.client.screen.PlannerScreen;
import com.rafikus.recipeplanner.jei.JEIConfig;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = RecipePlanner.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {

    @SubscribeEvent
    public static void screenKeyPressed(ScreenEvent.KeyPressed event) {
        if (event.getKeyCode() == Keybindings.INSTANCE.openRecipePlanner.getKey().getValue()) {
            if (JEIConfig.runtime == null) {
                RecipePlanner.LOGGER.error("JEI runtime is null");
                return;
            }

            MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null) {
                RecipePlanner.LOGGER.error("Screen is null");
                return;
            }

            Optional<ItemStack> recipesGUIItem = JEIConfig.runtime
                    .getRecipesGui()
                    .getIngredientUnderMouse(VanillaTypes.ITEM_STACK);

            if (recipesGUIItem.isPresent()) {
                recipesGUIItem.ifPresent(ClientForgeHandler::doSomethingWithItem);
            }

            ItemStack item = JEIConfig.runtime
                    .getIngredientListOverlay()
                    .getIngredientUnderMouse(VanillaTypes.ITEM_STACK);

            if (item != null) {
                ClientForgeHandler.doSomethingWithItem(item);
            }

            @SuppressWarnings("removal")
            List<Optional<ItemStack>> screenItems = JEIConfig.runtime.getScreenHelper()
                    .getClickableIngredientUnderMouse(screen, mouseHandler.xpos(), mouseHandler.ypos())
                    .map(IClickableIngredient::getTypedIngredient)
                    .map(ITypedIngredient::getItemStack).toList();

            for (Optional<ItemStack> i : screenItems) {
                i.ifPresent(ClientForgeHandler::doSomethingWithItem);
            }
        }
    }

    private static void doSomethingWithItem(ItemStack item) {
        Minecraft.getInstance().setScreen(new PlannerScreen(item, Minecraft.getInstance().screen));
    }
}
