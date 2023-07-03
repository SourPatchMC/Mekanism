package mekanism.quilt.capability;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import mekanism.api.NBTConstants;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

// Quilt: replaces CommonWorldTickHandler.chunkSave and CommonWorldTickHandler.onChunkDataLoad
public class ChunkVersionComponent implements Component {
    public static final ComponentKey<ChunkVersionComponent> KEY = ComponentRegistry.getOrCreate(Mekanism.rl("chunk_version"), ChunkVersionComponent.class);


    private final ChunkAccess chunk;

    ChunkVersionComponent(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (!(chunk instanceof LevelChunk chunk)) return;

        int version = tag.getInt(NBTConstants.WORLD_GEN_VERSION);
        //When a chunk is loaded, if it has an older version than the latest one
        if (version < MekanismConfig.world.userGenVersion.get()) {
            //Track what version it has so that when we save it, if we haven't gotten a chance to update
            // the chunk yet, then we are able to properly save that we still will need to update it
            if (Mekanism.worldTickHandler.chunkVersions == null) {
                Mekanism.worldTickHandler.chunkVersions = new Object2ObjectArrayMap<>();
            }
            ChunkPos chunkCoord = chunk.getPos();
            ResourceKey<Level> dimension = chunk.getLevel().dimension();
            Mekanism.worldTickHandler.chunkVersions.computeIfAbsent(dimension.location(), dim -> new Object2IntOpenHashMap<>())
                  .put(chunkCoord, version);
            if (MekanismConfig.world.enableRegeneration.get()) {
                //If retrogen is enabled, then we also need to mark the chunk as needing retrogen
                Mekanism.worldTickHandler.addRegenChunk(dimension, chunkCoord);
            }
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        if (!(chunk instanceof LevelChunk chunk)) return;

        int chunkVersion = MekanismConfig.world.userGenVersion.get();
        if (Mekanism.worldTickHandler.chunkVersions != null) {
            chunkVersion = Mekanism.worldTickHandler.chunkVersions.getOrDefault(chunk.getLevel().dimension().location(), Object2IntMaps.emptyMap())
                  .getOrDefault(chunk.getPos(), chunkVersion);
        }
        tag.putInt(NBTConstants.WORLD_GEN_VERSION, chunkVersion);
    }
}
