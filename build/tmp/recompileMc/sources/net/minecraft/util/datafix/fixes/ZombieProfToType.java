package net.minecraft.util.datafix.fixes;

import java.util.Random;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class ZombieProfToType implements IFixableData
{
    private static final Random RANDOM = new Random();

    public int getFixVersion()
    {
        return 502;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound)
    {
        if ("Zombie".equals(compound.getString("id")) && compound.getBoolean("IsVillager"))
        {
            if (!compound.hasKey("ZombieType", 99))
            {
                ZombieType zombietype = null;

                if (compound.hasKey("VillagerProfession", 99))
                {
                    try
                    {
                        zombietype = ZombieType.getByOrdinal(compound.getInteger("VillagerProfession") + 1);
                    }
                    catch (RuntimeException var4)
                    {
                        ;
                    }
                }

                if (zombietype == null)
                {
                    zombietype = ZombieType.getByOrdinal(RANDOM.nextInt(5) + 1);
                }

                compound.setInteger("ZombieType", zombietype.getId());
            }

            compound.removeTag("IsVillager");
        }

        return compound;
    }
}