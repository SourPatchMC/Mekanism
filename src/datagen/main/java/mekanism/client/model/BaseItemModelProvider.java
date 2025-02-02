package mekanism.client.model;

import mekanism.api.providers.IItemProvider;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.RegistryUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemModelProvider extends ItemModelProvider {

    protected BaseItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Item model provider: " + modid;
    }

    public boolean textureExists(ResourceLocation texture) {
        return existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures");
    }

    protected ResourceLocation itemTexture(IItemProvider itemProvider) {
        return modLoc("item/" + itemProvider.getName());
    }

    protected void registerGenerated(IItemProvider... itemProviders) {
        for (IItemProvider itemProvider : itemProviders) {
            generated(itemProvider);
        }
    }

    protected void registerModules(ItemDeferredRegister register) {
        for (IItemProvider itemProvider : register.getAllItems()) {
            Item item = itemProvider.asItem();
            if (item instanceof ItemModule) {
                generated(itemProvider);
            }
        }
    }

    protected void registerBuckets(FluidDeferredRegister register) {
        for (FluidRegistryObject<?, ?, ?, ?, ?> fluidRegistryObject : register.getAllFluids()) {
            registerBucket(fluidRegistryObject);
        }
    }

    protected ItemModelBuilder generated(IItemProvider itemProvider) {
        return generated(itemProvider, itemTexture(itemProvider));
    }

    protected ItemModelBuilder generated(IItemProvider itemProvider, ResourceLocation texture) {
        return withExistingParent(itemProvider.getName(), "item/generated").texture("layer0", texture);
    }

    protected ItemModelBuilder resource(IItemProvider itemProvider, String type) {
        //TODO: Try to come up with a better solution to this. Currently we have an empty texture for layer zero so that we can set
        // the tint only on layer one so that we only end up having the tint show for this fallback texture
        ItemModelBuilder modelBuilder = generated(itemProvider, modLoc("item/empty")).texture("layer1", modLoc("item/" + type));
        ResourceLocation overlay = modLoc("item/" + type + "_overlay");
        if (textureExists(overlay)) {
            //If we have an overlay type for that resource type then add that as another layer
            modelBuilder = modelBuilder.texture("layer2", overlay);
        }
        return modelBuilder;
    }

    protected void registerHandheld(IItemProvider... itemProviders) {
        for (IItemProvider itemProvider : itemProviders) {
            handheld(itemProvider);
        }
    }

    protected ItemModelBuilder handheld(IItemProvider itemProvider) {
        return handheld(itemProvider, itemTexture(itemProvider));
    }

    protected ItemModelBuilder handheld(IItemProvider itemProvider, ResourceLocation texture) {
        return withExistingParent(itemProvider.getName(), "item/handheld").texture("layer0", texture);
    }

    //Note: This isn't the best way to do this in terms of model file validation, but it works
    protected void registerBucket(FluidRegistryObject<?, ?, ?, ?, ?> fluidRO) {
        withExistingParent(RegistryUtils.getPath(fluidRO.getBucket()), new ResourceLocation("forge", "item/bucket"))
              .customLoader(DynamicFluidContainerModelBuilder::begin)
              .fluid(fluidRO.getStillFluid());
    }
}