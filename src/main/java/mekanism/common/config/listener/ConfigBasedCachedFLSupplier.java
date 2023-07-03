package mekanism.common.config.listener;

import io.github.fabricators_of_create.porting_lib.common.util.NonNullSupplier;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.config.value.CachedValue;

public class ConfigBasedCachedFLSupplier extends ConfigBasedCachedSupplier<FloatingLong> implements FloatingLongSupplier {

    public ConfigBasedCachedFLSupplier(NonNullSupplier<FloatingLong> resolver, CachedValue<?>... dependantConfigValues) {
        super(resolver, dependantConfigValues);
    }
}