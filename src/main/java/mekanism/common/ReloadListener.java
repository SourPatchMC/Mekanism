package mekanism.common;

import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

public class ReloadListener implements SimpleSynchronousResourceReloader {

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        CommonWorldTickHandler.flushTagAndRecipeCaches = true;
        MekanismRecipeType.clearCache();
    }

    @Override
    public @NotNull ResourceLocation getQuiltId() {
        return Mekanism.rl("recipe_cache_manager");
    }
}