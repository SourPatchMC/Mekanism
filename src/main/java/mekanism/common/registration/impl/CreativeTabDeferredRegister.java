package mekanism.common.registration.impl;

import java.util.function.UnaryOperator;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.common.block.BlockBounding;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;

//TODO - 1.20: Maybe add some things to vanilla tabs/groups? At the very least things like mek additions' spawn eggs to the spawn egg tab
public class CreativeTabDeferredRegister extends WrappedDeferredRegister<CreativeModeTab> {

    private final String modid;

    public CreativeTabDeferredRegister(String modid) {
        super(modid, Registries.CREATIVE_MODE_TAB);
        this.modid = modid;
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public CreativeTabRegistryObject registerMain(ILangEntry title, IItemProvider icon, UnaryOperator<CreativeModeTab.Builder> operator) {
        return register(modid, title, icon, operator);
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public CreativeTabRegistryObject register(String name, ILangEntry title, IItemProvider icon, UnaryOperator<CreativeModeTab.Builder> operator) {
        return register(name, () -> {
            CreativeModeTab.Builder builder = CreativeModeTab.builder()
                  .title(title.translate())
                  .icon(icon::getItemStack)
                  .withTabFactory(MekanismCreativeTab::new)
                  //TODO - 1.20: Remove this once https://github.com/MinecraftForge/MinecraftForge/pull/9612 is merged
                  .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.COLORED_BLOCKS, CreativeModeTabs.NATURAL_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                        CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.COMBAT, CreativeModeTabs.FOOD_AND_DRINKS,
                        CreativeModeTabs.INGREDIENTS, CreativeModeTabs.SPAWN_EGGS);
            return operator.apply(builder).build();
        }, CreativeTabRegistryObject::new);
    }

    private static void addToDisplay(ItemLike itemLike, CreativeModeTab.Output output) {
        if (itemLike.asItem() instanceof ICustomCreativeTabContents contents) {
            if (contents.addDefault()) {
                output.accept(itemLike);
            }
            contents.addItems(output);
        } else {
            output.accept(itemLike);
        }
    }

    public static void addToDisplay(ItemDeferredRegister register, CreativeModeTab.Output output) {
        for (IItemProvider itemProvider : register.getAllItems()) {
            addToDisplay(itemProvider, output);
        }
    }

    public static void addToDisplay(BlockDeferredRegister register, CreativeModeTab.Output output) {
        for (IBlockProvider itemProvider : register.getAllBlocks()) {
            //Don't add bounding blocks to the creative tab
            if (!(itemProvider.getBlock() instanceof BlockBounding)) {
                addToDisplay(itemProvider, output);
            }
        }
    }

    public static void addToDisplay(FluidDeferredRegister register, CreativeModeTab.Output output) {
        for (FluidRegistryObject<?, ?, ?, ?, ?> fluidRO : register.getAllFluids()) {
            addToDisplay(fluidRO.getBucket(), output);
        }
    }

    //TODO - 1.20: Re-evaluate this and maybe inline the stuff into their respective creative tabs
    public interface ICustomCreativeTabContents {

        void addItems(CreativeModeTab.Output tabOutput);

        default boolean addDefault() {
            return true;
        }
    }

    public static class MekanismCreativeTab extends CreativeModeTab {

        protected MekanismCreativeTab(CreativeModeTab.Builder builder) {
            super(builder);
        }

        @Override
        public int getLabelColor() {
            return SpecialColors.TEXT_TITLE.argb();
        }
    }
}