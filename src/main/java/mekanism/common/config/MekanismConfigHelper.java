package mekanism.common.config;

import java.nio.file.Path;

import org.quiltmc.loader.api.ModContainer;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import mekanism.common.Mekanism;

public class MekanismConfigHelper {

    private MekanismConfigHelper() {
    }

    public static final Path CONFIG_DIR = ForgeConfigPaths.INSTANCE.getCommonConfigDirectory().resolve(Mekanism.MOD_NAME);

    /**
     * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server configs from server to client.
     */
    public static void registerConfig(ModContainer modContainer, IMekanismConfig config) {
        MekanismModConfig modConfig = new MekanismModConfig(modContainer, config);
        // TODO: add to container?
        // if (config.addToContainer()) {
        //     modContainer.add(modConfig);
        // }
    }
}