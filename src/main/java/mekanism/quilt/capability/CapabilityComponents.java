package mekanism.quilt.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import mekanism.common.Mekanism;
import net.minecraft.world.entity.LivingEntity;

public class CapabilityComponents implements ChunkComponentInitializer, EntityComponentInitializer {
    static final Multimap<CapabilityType, Capability.Provider<?, ?>> PROVIDERS = HashMultimap.create();

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, LivingEntityCapabilityWrapperComponent.KEY, ctx -> new LivingEntityCapabilityWrapperComponent(ctx, LivingEntity.class));
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(ChunkVersionComponent.KEY, ChunkVersionComponent::new);
    }

    public enum CapabilityType {
        LIVING_ENTITY
    }

    private static class LivingEntityCapabilityWrapperComponent extends CapabilityWrapperComponent<LivingEntity> {
        private static final ComponentKey<LivingEntityCapabilityWrapperComponent> KEY = ComponentRegistry.getOrCreate(Mekanism.rl("living_entity"), LivingEntityCapabilityWrapperComponent.class);

        LivingEntityCapabilityWrapperComponent(LivingEntity ctx, Class<LivingEntity> ctxClass) {
            super(CapabilityType.LIVING_ENTITY, ctx, ctxClass);
        }
    }
}
