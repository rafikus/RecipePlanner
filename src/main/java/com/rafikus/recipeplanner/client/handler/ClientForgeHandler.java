package com.rafikus.recipeplanner.client.handler;

import com.rafikus.recipeplanner.RecipePlanner;
import com.rafikus.recipeplanner.client.Keybindings;
import com.rafikus.recipeplanner.client.screen.PlannerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RecipePlanner.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {


    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (Keybindings.INSTANCE.openRecipePlanner.consumeClick()) {
            // Open the recipe planner
            Minecraft.getInstance().setScreen(new PlannerScreen());
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            player.displayClientMessage(Component.translatable("KEK"), true);
        }
    }
}
