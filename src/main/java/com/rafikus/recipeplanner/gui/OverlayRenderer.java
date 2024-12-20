package com.rafikus.recipeplanner.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import com.rafikus.recipeplanner.RecipePlanner;

@Mod.EventBusSubscriber(modid = RecipePlanner.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class OverlayRenderer {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        renderBoxElement(event.getGuiGraphics(), event.getPartialTick());
    }

    private static void renderBoxElement(GuiGraphics guiGraphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) { // Ensure the rendering only happens in-game, not in menus
            Tooltip tooltip = createTooltip();
            BoxElement boxElement = new BoxElement(tooltip);

            // Set the position to render the BoxElement
            float x = (float) mc.getWindow().getGuiScaledWidth() / 2;
            float y = (float) mc.getWindow().getGuiScaledHeight() / 2;
            boxElement.render(guiGraphics, x, y, x + 30, y + 20); // Adjust width and height as needed
        }
    }

    private static Tooltip createTooltip() {
        // Create a tooltip with the desired text
        Component title = Component.literal("Test Title");
        Component description = Component.literal("Test Description");
        return Tooltip.create(title, description);
    }
}