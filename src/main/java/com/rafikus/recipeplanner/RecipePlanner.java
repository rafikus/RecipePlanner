package com.rafikus.recipeplanner;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipePlanner.MODID)
public class RecipePlanner
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "recipeplanner";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public RecipePlanner() {
        LOGGER.info("Hello from Recipe Planner!");
    }
}
