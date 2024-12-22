package com.rafikus.recipeplanner.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIConfig implements IModPlugin {
    public static IJeiRuntime runtime;

    @NotNull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("recipeplanner", "jei");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEIConfig.runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        JEIConfig.runtime = null;
    }

}
