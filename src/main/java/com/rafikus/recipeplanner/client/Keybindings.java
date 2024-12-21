package com.rafikus.recipeplanner.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.rafikus.recipeplanner.RecipePlanner;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();

    private static final String CATEGORY = "key.categories." + RecipePlanner.MODID;

    private Keybindings() {}

    public final KeyMapping openRecipePlanner = new KeyMapping(
            "key."+ RecipePlanner.MODID + ".plan_recipe",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_P, -1),
            CATEGORY
    );


}
