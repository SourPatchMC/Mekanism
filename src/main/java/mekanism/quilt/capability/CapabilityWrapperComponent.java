package mekanism.quilt.capability;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import dev.onyxstudios.cca.api.v3.component.Component;
import mekanism.quilt.capability.CapabilityComponents.CapabilityType;
import net.minecraft.nbt.CompoundTag;

public class CapabilityWrapperComponent<C> implements Component {
    private final List<Pair<Capability, String>> CAPABILITIES = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    CapabilityWrapperComponent(CapabilityType type, C ctx, Class<C> ctxClass) {
        for (Capability.Provider<?, ?> provider : CapabilityComponents.PROVIDERS.get(type)) {
            CAPABILITIES.add(Pair.of(((Capability.Provider<?, C>) provider).create(ctx), provider.getNbtKey()));
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        CAPABILITIES.forEach(capability -> capability.getFirst().readFromNbt(tag.getCompound(capability.getSecond())));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        CAPABILITIES.forEach(capability -> {
            var capabilityTag = new CompoundTag();
            capability.getFirst().writeToNbt(capabilityTag);
            tag.put(capability.getSecond(), capabilityTag);
        });
    }
}
