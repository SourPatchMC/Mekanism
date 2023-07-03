package mekanism.api.math;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.common.util.NonNullSupplier;

/**
 * Represents a supplier of {@link FloatingLong}-valued results.  This is a specialization of {@link Supplier} for {@link FloatingLong}s, used to make it cleaner and
 * easier to declare {@link Supplier}'s for {@link FloatingLong}s.
 */
@FunctionalInterface
public interface FloatingLongSupplier extends NonNullSupplier<FloatingLong> {

    @NotNull
    @Override
    FloatingLong get();
}