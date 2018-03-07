package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i
{
    private static final Logger LOGGER = LogManager.getLogger();
    /** An immutable block pos with zero as all coordinates. */
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = 0 + NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z)
    {
        super(x, y, z);
    }

    public BlockPos(Entity source)
    {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPos(Vec3d vec)
    {
        this(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public BlockPos(Vec3i source)
    {
        this(source.getX(), source.getY(), source.getZ());
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPos add(double x, double y, double z)
    {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPos add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    /**
     * Add the given Vector to this BlockPos
     */
    public BlockPos add(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    /**
     * Subtract the given Vector from this BlockPos
     */
    public BlockPos subtract(Vec3i vec)
    {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    /**
     * Offset this BlockPos 1 block up
     */
    public BlockPos up()
    {
        return this.up(1);
    }

    /**
     * Offset this BlockPos n blocks up
     */
    public BlockPos up(int n)
    {
        return this.offset(EnumFacing.UP, n);
    }

    /**
     * Offset this BlockPos 1 block down
     */
    public BlockPos down()
    {
        return this.down(1);
    }

    /**
     * Offset this BlockPos n blocks down
     */
    public BlockPos down(int n)
    {
        return this.offset(EnumFacing.DOWN, n);
    }

    /**
     * Offset this BlockPos 1 block in northern direction
     */
    public BlockPos north()
    {
        return this.north(1);
    }

    /**
     * Offset this BlockPos n blocks in northern direction
     */
    public BlockPos north(int n)
    {
        return this.offset(EnumFacing.NORTH, n);
    }

    /**
     * Offset this BlockPos 1 block in southern direction
     */
    public BlockPos south()
    {
        return this.south(1);
    }

    /**
     * Offset this BlockPos n blocks in southern direction
     */
    public BlockPos south(int n)
    {
        return this.offset(EnumFacing.SOUTH, n);
    }

    /**
     * Offset this BlockPos 1 block in western direction
     */
    public BlockPos west()
    {
        return this.west(1);
    }

    /**
     * Offset this BlockPos n blocks in western direction
     */
    public BlockPos west(int n)
    {
        return this.offset(EnumFacing.WEST, n);
    }

    /**
     * Offset this BlockPos 1 block in eastern direction
     */
    public BlockPos east()
    {
        return this.east(1);
    }

    /**
     * Offset this BlockPos n blocks in eastern direction
     */
    public BlockPos east(int n)
    {
        return this.offset(EnumFacing.EAST, n);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPos offset(EnumFacing facing)
    {
        return this.offset(facing, 1);
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public BlockPos offset(EnumFacing facing, int n)
    {
        return n == 0 ? this : new BlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public BlockPos crossProduct(Vec3i vec)
    {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * Serialize this BlockPos into a long value
     */
    public long toLong()
    {
        return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
    }

    /**
     * Create a BlockPos from a serialized long value (created by toLong)
     */
    public static BlockPos fromLong(long serialized)
    {
        int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(i, j, k);
    }

    /**
     * Create an Iterable that returns all positions in the box specified by the given corners
     */
    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to)
    {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos>()
        {
            public Iterator<BlockPos> iterator()
            {
                return new AbstractIterator<BlockPos>()
                {
                    private BlockPos lastReturned;
                    protected BlockPos computeNext()
                    {
                        if (this.lastReturned == null)
                        {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        }
                        else if (this.lastReturned.equals(blockpos1))
                        {
                            return (BlockPos)this.endOfData();
                        }
                        else
                        {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new BlockPos(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    /**
     * Returns a version of this BlockPos that is guaranteed to be immutable.
     *  
     * <p>When storing a BlockPos given to you for an extended period of time, make sure you
     * use this in case the value is changed internally.</p>
     */
    public BlockPos toImmutable()
    {
        return this;
    }

    /**
     * Like getAllInBox but reuses a single MutableBlockPos instead. If this method is used, the resulting BlockPos
     * instances can only be used inside the iteration loop.
     */
    public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to)
    {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos.MutableBlockPos>()
        {
            public Iterator<BlockPos.MutableBlockPos> iterator()
            {
                return new AbstractIterator<BlockPos.MutableBlockPos>()
                {
                    private BlockPos.MutableBlockPos theBlockPos;
                    protected BlockPos.MutableBlockPos computeNext()
                    {
                        if (this.theBlockPos == null)
                        {
                            this.theBlockPos = new BlockPos.MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        }
                        else if (this.theBlockPos.equals(blockpos1))
                        {
                            return (BlockPos.MutableBlockPos)this.endOfData();
                        }
                        else
                        {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX())
                            {
                                ++i;
                            }
                            else if (j < blockpos1.getY())
                            {
                                i = blockpos.getX();
                                ++j;
                            }
                            else if (k < blockpos1.getZ())
                            {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPos.x = i;
                            this.theBlockPos.y = j;
                            this.theBlockPos.z = k;
                            return this.theBlockPos;
                        }
                    }
                };
            }
        };
    }

    public static class MutableBlockPos extends BlockPos
        {
            /** Mutable X Coordinate */
            protected int x;
            /** Mutable Y Coordinate */
            protected int y;
            /** Mutable Z Coordinate */
            protected int z;

            public MutableBlockPos()
            {
                this(0, 0, 0);
            }

            public MutableBlockPos(BlockPos pos)
            {
                this(pos.getX(), pos.getY(), pos.getZ());
            }

            public MutableBlockPos(int x_, int y_, int z_)
            {
                super(0, 0, 0);
                this.x = x_;
                this.y = y_;
                this.z = z_;
            }

            /**
             * Gets the X coordinate.
             */
            public int getX()
            {
                return this.x;
            }

            /**
             * Gets the Y coordinate.
             */
            public int getY()
            {
                return this.y;
            }

            /**
             * Gets the Z coordinate.
             */
            public int getZ()
            {
                return this.z;
            }

            /**
             * Sets the position, MUST not be name 'set' as that causes obfusication conflicts with func_185343_d
             */
            public BlockPos.MutableBlockPos setPos(int xIn, int yIn, int zIn)
            {
                this.x = xIn;
                this.y = yIn;
                this.z = zIn;
                return this;
            }

            public BlockPos.MutableBlockPos setPos(double xIn, double yIn, double zIn)
            {
                return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
            }

            @SideOnly(Side.CLIENT)
            public BlockPos.MutableBlockPos setPos(Entity entityIn)
            {
                return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
            }

            public BlockPos.MutableBlockPos setPos(Vec3i vec)
            {
                return this.setPos(vec.getX(), vec.getY(), vec.getZ());
            }

            public BlockPos.MutableBlockPos move(EnumFacing facing)
            {
                return this.move(facing, 1);
            }

            public BlockPos.MutableBlockPos move(EnumFacing facing, int p_189534_2_)
            {
                return this.setPos(this.x + facing.getFrontOffsetX() * p_189534_2_, this.y + facing.getFrontOffsetY() * p_189534_2_, this.z + facing.getFrontOffsetZ() * p_189534_2_);
            }

            public void setY(int yIn)
            {
                this.y = yIn;
            }

            /**
             * Returns a version of this BlockPos that is guaranteed to be immutable.
             *  
             * <p>When storing a BlockPos given to you for an extended period of time, make sure you
             * use this in case the value is changed internally.</p>
             */
            public BlockPos toImmutable()
            {
                return new BlockPos(this);
            }
        }

    public static final class PooledMutableBlockPos extends BlockPos.MutableBlockPos
        {
            private boolean released;
            private static final List<BlockPos.PooledMutableBlockPos> POOL = Lists.<BlockPos.PooledMutableBlockPos>newArrayList();

            private PooledMutableBlockPos(int xIn, int yIn, int zIn)
            {
                super(xIn, yIn, zIn);
            }

            public static BlockPos.PooledMutableBlockPos retain()
            {
                return retain(0, 0, 0);
            }

            public static BlockPos.PooledMutableBlockPos retain(double xIn, double yIn, double zIn)
            {
                return retain(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
            }

            @SideOnly(Side.CLIENT)
            public static BlockPos.PooledMutableBlockPos retain(Vec3i vec)
            {
                return retain(vec.getX(), vec.getY(), vec.getZ());
            }

            public static BlockPos.PooledMutableBlockPos retain(int xIn, int yIn, int zIn)
            {
                synchronized (POOL)
                {
                    if (!POOL.isEmpty())
                    {
                        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = (BlockPos.PooledMutableBlockPos)POOL.remove(POOL.size() - 1);

                        if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released)
                        {
                            blockpos$pooledmutableblockpos.released = false;
                            blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
                            return blockpos$pooledmutableblockpos;
                        }
                    }
                }

                return new BlockPos.PooledMutableBlockPos(xIn, yIn, zIn);
            }

            public void release()
            {
                synchronized (POOL)
                {
                    if (POOL.size() < 100)
                    {
                        POOL.add(this);
                    }

                    this.released = true;
                }
            }

            /**
             * Sets the position, MUST not be name 'set' as that causes obfusication conflicts with func_185343_d
             */
            public BlockPos.PooledMutableBlockPos setPos(int xIn, int yIn, int zIn)
            {
                if (this.released)
                {
                    BlockPos.LOGGER.error("PooledMutableBlockPosition modified after it was released.", new Throwable());
                    this.released = false;
                }

                return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
            }

            @SideOnly(Side.CLIENT)
            public BlockPos.PooledMutableBlockPos setPos(Entity entityIn)
            {
                return (BlockPos.PooledMutableBlockPos)super.setPos(entityIn);
            }

            public BlockPos.PooledMutableBlockPos setPos(double xIn, double yIn, double zIn)
            {
                return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
            }

            public BlockPos.PooledMutableBlockPos setPos(Vec3i vec)
            {
                return (BlockPos.PooledMutableBlockPos)super.setPos(vec);
            }

            public BlockPos.PooledMutableBlockPos move(EnumFacing facing)
            {
                return (BlockPos.PooledMutableBlockPos)super.move(facing);
            }

            public BlockPos.PooledMutableBlockPos move(EnumFacing facing, int p_189534_2_)
            {
                return (BlockPos.PooledMutableBlockPos)super.move(facing, p_189534_2_);
            }
        }
}