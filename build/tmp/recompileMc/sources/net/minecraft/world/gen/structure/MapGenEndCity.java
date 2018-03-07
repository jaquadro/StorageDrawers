package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderEnd;

public class MapGenEndCity extends MapGenStructure
{
    private final int citySpacing = 20;
    private final int minCitySeparation = 11;
    private final ChunkProviderEnd endProvider;

    public MapGenEndCity(ChunkProviderEnd p_i46665_1_)
    {
        this.endProvider = p_i46665_1_;
    }

    public String getStructureName()
    {
        return "EndCity";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= 19;
        }

        if (chunkZ < 0)
        {
            chunkZ -= 19;
        }

        int k = chunkX / 20;
        int l = chunkZ / 20;
        Random random = this.world.setRandomSeed(k, l, 10387313);
        k = k * 20;
        l = l * 20;
        k = k + (random.nextInt(9) + random.nextInt(9)) / 2;
        l = l + (random.nextInt(9) + random.nextInt(9)) / 2;
        return i == k && j == l && this.endProvider.isIslandChunk(i, j);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenEndCity.Start(this.world, this.endProvider, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
        {
            private boolean isSizeable;

            public Start()
            {
            }

            public Start(World worldIn, ChunkProviderEnd chunkProvider, Random random, int chunkX, int chunkZ)
            {
                super(chunkX, chunkZ);
                this.create(worldIn, chunkProvider, random, chunkX, chunkZ);
            }

            private void create(World worldIn, ChunkProviderEnd chunkProvider, Random rnd, int chunkX, int chunkZ)
            {
                Rotation rotation = Rotation.values()[rnd.nextInt(Rotation.values().length)];
                ChunkPrimer chunkprimer = new ChunkPrimer();
                chunkProvider.setBlocksInChunk(chunkX, chunkZ, chunkprimer);
                int i = 5;
                int j = 5;

                if (rotation == Rotation.CLOCKWISE_90)
                {
                    i = -5;
                }
                else if (rotation == Rotation.CLOCKWISE_180)
                {
                    i = -5;
                    j = -5;
                }
                else if (rotation == Rotation.COUNTERCLOCKWISE_90)
                {
                    j = -5;
                }

                int k = chunkprimer.findGroundBlockIdx(7, 7);
                int l = chunkprimer.findGroundBlockIdx(7, 7 + j);
                int i1 = chunkprimer.findGroundBlockIdx(7 + i, 7);
                int j1 = chunkprimer.findGroundBlockIdx(7 + i, 7 + j);
                int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

                if (k1 < 60)
                {
                    this.isSizeable = false;
                }
                else
                {
                    BlockPos blockpos = new BlockPos(chunkX * 16 + 8, k1, chunkZ * 16 + 8);
                    StructureEndCityPieces.beginHouseTower(blockpos, rotation, this.components, rnd);
                    this.updateBoundingBox();
                    this.isSizeable = true;
                }
            }

            /**
             * currently only defined for Villages, returns true if Village has more than 2 non-road components
             */
            public boolean isSizeableStructure()
            {
                return this.isSizeable;
            }

            public void writeToNBT(NBTTagCompound tagCompound)
            {
                super.writeToNBT(tagCompound);
            }

            public void readFromNBT(NBTTagCompound tagCompound)
            {
                super.readFromNBT(tagCompound);
            }
        }
}