package com.jaquadro.minecraft.storagedrawers.block.tile;

/**
* Created by Justin on 4/25/2015.
*/
class BlockCoord
{
    private int x;
    private int y;
    private int z;

    public BlockCoord (int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x () {
        return x;
    }

    public int y () {
        return y;
    }

    public int z () {
        return z;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;

        BlockCoord that = (BlockCoord)obj;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode () {
        int hash = 23;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;

        return hash;
    }
}
