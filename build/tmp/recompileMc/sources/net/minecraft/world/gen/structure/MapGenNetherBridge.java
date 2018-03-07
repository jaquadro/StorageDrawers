package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class MapGenNetherBridge extends MapGenStructure
{
    private final List<Biome.SpawnListEntry> spawnList = Lists.<Biome.SpawnListEntry>newArrayList();

    public MapGenNetherBridge()
    {
        this.spawnList.add(new Biome.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.spawnList.add(new Biome.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.spawnList.add(new Biome.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnList.add(new Biome.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    public String getStructureName()
    {
        return "Fortress";
    }

    public List<Biome.SpawnListEntry> getSpawnList()
    {
        return this.spawnList;
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX >> 4;
        int j = chunkZ >> 4;
        this.rand.setSeed((long)(i ^ j << 4) ^ this.world.getSeed());
        this.rand.nextInt();
        return this.rand.nextInt(3) != 0 ? false : (chunkX != (i << 4) + 4 + this.rand.nextInt(8) ? false : chunkZ == (j << 4) + 4 + this.rand.nextInt(8));
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenNetherBridge.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
        {
            public Start()
            {
            }

            public Start(World worldIn, Random random, int chunkX, int chunkZ)
            {
                super(chunkX, chunkZ);
                StructureNetherBridgePieces.Start structurenetherbridgepieces$start = new StructureNetherBridgePieces.Start(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
                this.components.add(structurenetherbridgepieces$start);
                structurenetherbridgepieces$start.buildComponent(structurenetherbridgepieces$start, this.components, random);
                List<StructureComponent> list = structurenetherbridgepieces$start.pendingChildren;

                while (!list.isEmpty())
                {
                    int i = random.nextInt(list.size());
                    StructureComponent structurecomponent = (StructureComponent)list.remove(i);
                    structurecomponent.buildComponent(structurenetherbridgepieces$start, this.components, random);
                }

                this.updateBoundingBox();
                this.setRandomHeight(worldIn, random, 48, 70);
            }
        }
}