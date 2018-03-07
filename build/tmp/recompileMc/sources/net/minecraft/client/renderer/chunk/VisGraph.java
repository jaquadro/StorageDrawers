package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Queues;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VisGraph
{
    private static final int DX = (int)Math.pow(16.0D, 0.0D);
    private static final int DZ = (int)Math.pow(16.0D, 1.0D);
    private static final int DY = (int)Math.pow(16.0D, 2.0D);
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = new int[1352];
    private int empty = 4096;

    public void setOpaqueCube(BlockPos pos)
    {
        this.bitSet.set(getIndex(pos), true);
        --this.empty;
    }

    private static int getIndex(BlockPos pos)
    {
        return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static int getIndex(int x, int y, int z)
    {
        return x << 0 | y << 8 | z << 4;
    }

    public SetVisibility computeVisibility()
    {
        SetVisibility setvisibility = new SetVisibility();

        if (4096 - this.empty < 256)
        {
            setvisibility.setAllVisible(true);
        }
        else if (this.empty == 0)
        {
            setvisibility.setAllVisible(false);
        }
        else
        {
            for (int i : INDEX_OF_EDGES)
            {
                if (!this.bitSet.get(i))
                {
                    setvisibility.setManyVisible(this.floodFill(i));
                }
            }
        }

        return setvisibility;
    }

    public Set<EnumFacing> getVisibleFacings(BlockPos pos)
    {
        return this.floodFill(getIndex(pos));
    }

    private Set<EnumFacing> floodFill(int p_178604_1_)
    {
        Set<EnumFacing> set = EnumSet.<EnumFacing>noneOf(EnumFacing.class);
        Queue<Integer> queue = Queues.<Integer>newArrayDeque();
        queue.add(IntegerCache.getInteger(p_178604_1_));
        this.bitSet.set(p_178604_1_, true);

        while (!((Queue)queue).isEmpty())
        {
            int i = ((Integer)queue.poll()).intValue();
            this.addEdges(i, set);

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                int j = this.getNeighborIndexAtFace(i, enumfacing);

                if (j >= 0 && !this.bitSet.get(j))
                {
                    this.bitSet.set(j, true);
                    queue.add(IntegerCache.getInteger(j));
                }
            }
        }

        return set;
    }

    private void addEdges(int p_178610_1_, Set<EnumFacing> p_178610_2_)
    {
        int i = p_178610_1_ >> 0 & 15;

        if (i == 0)
        {
            p_178610_2_.add(EnumFacing.WEST);
        }
        else if (i == 15)
        {
            p_178610_2_.add(EnumFacing.EAST);
        }

        int j = p_178610_1_ >> 8 & 15;

        if (j == 0)
        {
            p_178610_2_.add(EnumFacing.DOWN);
        }
        else if (j == 15)
        {
            p_178610_2_.add(EnumFacing.UP);
        }

        int k = p_178610_1_ >> 4 & 15;

        if (k == 0)
        {
            p_178610_2_.add(EnumFacing.NORTH);
        }
        else if (k == 15)
        {
            p_178610_2_.add(EnumFacing.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int p_178603_1_, EnumFacing p_178603_2_)
    {
        switch (p_178603_2_)
        {
            case DOWN:

                if ((p_178603_1_ >> 8 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - DY;
            case UP:

                if ((p_178603_1_ >> 8 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + DY;
            case NORTH:

                if ((p_178603_1_ >> 4 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - DZ;
            case SOUTH:

                if ((p_178603_1_ >> 4 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + DZ;
            case WEST:

                if ((p_178603_1_ >> 0 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - DX;
            case EAST:

                if ((p_178603_1_ >> 0 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + DX;
            default:
                return -1;
        }
    }

    static
    {
        int i = 0;
        int j = 15;
        int k = 0;

        for (int l = 0; l < 16; ++l)
        {
            for (int i1 = 0; i1 < 16; ++i1)
            {
                for (int j1 = 0; j1 < 16; ++j1)
                {
                    if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15)
                    {
                        INDEX_OF_EDGES[k++] = getIndex(l, i1, j1);
                    }
                }
            }
        }
    }
}