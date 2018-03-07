package net.minecraft.world.chunk;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IChunkGenerator
{
    Chunk provideChunk(int x, int z);

    void populate(int x, int z);

    boolean generateStructures(Chunk chunkIn, int x, int z);

    List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos);

    @Nullable
    BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position);

    void recreateStructures(Chunk chunkIn, int x, int z);
}