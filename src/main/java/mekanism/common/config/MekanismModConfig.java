package mekanism.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;

import java.nio.file.Path;
import java.util.function.Function;

import org.quiltmc.loader.api.ModContainer;

import mekanism.common.Mekanism;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Custom {@link ModConfig} implementation that allows for rerouting the server config from being in the world's folder to being in the normal config folder. This allows
 * for us to use the builtin sync support, without the extra hassle of having to explain to people where the config file is, or require people in single player to edit
 * the config each time they make a new world.
 */
public class MekanismModConfig extends ModConfig {

    private static final MekanismConfigFileTypeHandler MEK_TOML = new MekanismConfigFileTypeHandler();

    private final IMekanismConfig mekanismConfig;

    public MekanismModConfig(ModContainer modContainer, IMekanismConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), modContainer.metadata().id(), Mekanism.MOD_NAME + "/" + config.getFileName() + ".toml");
        this.mekanismConfig = config;
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return MEK_TOML;
    }

    public void clearCache(boolean unloading) {
        mekanismConfig.clearCache(unloading);
    }

    private static class MekanismConfigFileTypeHandler extends ConfigFileTypeHandler {

        private static Path getPath(Path configBasePath) {
            //Intercept server config path reading for Mekanism configs and reroute it to the normal config directory
            if (configBasePath.endsWith("serverconfig")) {
                return ForgeConfigPaths.INSTANCE.getCommonConfigDirectory();
            }
            return configBasePath;
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(getPath(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config) {
            super.unload(getPath(configBasePath), config);
        }
    }
}