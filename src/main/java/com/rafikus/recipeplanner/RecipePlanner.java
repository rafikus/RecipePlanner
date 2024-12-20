package com.rafikus.recipeplanner;

import com.mojang.logging.LogUtils;
import com.rafikus.recipeplanner.gui.OverlayRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipePlanner.MODID)
public class RecipePlanner
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "recipeplanner";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public RecipePlanner() {

        // Register the event bus
        MinecraftForge.EVENT_BUS.register(this);
        // Register the client-side event bus
        MinecraftForge.EVENT_BUS.register(OverlayRenderer.class);
        LOGGER.info("Hello from Recipe Planner!"
        );
    }
}
