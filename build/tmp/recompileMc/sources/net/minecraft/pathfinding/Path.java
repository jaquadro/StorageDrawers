package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Path
{
    /** The actual points in the path */
    private final PathPoint[] points;
    private PathPoint[] openSet = new PathPoint[0];
    private PathPoint[] closedSet = new PathPoint[0];
    @SideOnly(Side.CLIENT)
    private PathPoint target;
    /** PathEntity Array Index the Entity is currently targeting */
    private int currentPathIndex;
    /** The total length of the path */
    private int pathLength;

    public Path(PathPoint[] pathpoints)
    {
        this.points = pathpoints;
        this.pathLength = pathpoints.length;
    }

    /**
     * Directs this path to the next point in its array
     */
    public void incrementPathIndex()
    {
        ++this.currentPathIndex;
    }

    /**
     * Returns true if this path has reached the end
     */
    public boolean isFinished()
    {
        return this.currentPathIndex >= this.pathLength;
    }

    /**
     * returns the last PathPoint of the Array
     */
    public PathPoint getFinalPathPoint()
    {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    /**
     * return the PathPoint located at the specified PathIndex, usually the current one
     */
    public PathPoint getPathPointFromIndex(int index)
    {
        return this.points[index];
    }

    public void setPoint(int index, PathPoint point)
    {
        this.points[index] = point;
    }

    public int getCurrentPathLength()
    {
        return this.pathLength;
    }

    public void setCurrentPathLength(int length)
    {
        this.pathLength = length;
    }

    public int getCurrentPathIndex()
    {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int currentPathIndexIn)
    {
        this.currentPathIndex = currentPathIndexIn;
    }

    /**
     * Gets the vector of the PathPoint associated with the given index.
     */
    public Vec3d getVectorFromIndex(Entity entityIn, int index)
    {
        double d0 = (double)this.points[index].xCoord + (double)((int)(entityIn.width + 1.0F)) * 0.5D;
        double d1 = (double)this.points[index].yCoord;
        double d2 = (double)this.points[index].zCoord + (double)((int)(entityIn.width + 1.0F)) * 0.5D;
        return new Vec3d(d0, d1, d2);
    }

    /**
     * returns the current PathEntity target node as Vec3D
     */
    public Vec3d getPosition(Entity entityIn)
    {
        return this.getVectorFromIndex(entityIn, this.currentPathIndex);
    }

    public Vec3d getCurrentPos()
    {
        PathPoint pathpoint = this.points[this.currentPathIndex];
        return new Vec3d((double)pathpoint.xCoord, (double)pathpoint.yCoord, (double)pathpoint.zCoord);
    }

    /**
     * Returns true if the EntityPath are the same. Non instance related equals.
     */
    public boolean isSamePath(Path pathentityIn)
    {
        if (pathentityIn == null)
        {
            return false;
        }
        else if (pathentityIn.points.length != this.points.length)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < this.points.length; ++i)
            {
                if (this.points[i].xCoord != pathentityIn.points[i].xCoord || this.points[i].yCoord != pathentityIn.points[i].yCoord || this.points[i].zCoord != pathentityIn.points[i].zCoord)
                {
                    return false;
                }
            }

            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public PathPoint[] getOpenSet()
    {
        return this.openSet;
    }

    @SideOnly(Side.CLIENT)
    public PathPoint[] getClosedSet()
    {
        return this.closedSet;
    }

    @SideOnly(Side.CLIENT)
    public PathPoint getTarget()
    {
        return this.target;
    }

    @SideOnly(Side.CLIENT)
    public static Path read(PacketBuffer buf)
    {
        int i = buf.readInt();
        PathPoint pathpoint = PathPoint.createFromBuffer(buf);
        PathPoint[] apathpoint = new PathPoint[buf.readInt()];

        for (int j = 0; j < apathpoint.length; ++j)
        {
            apathpoint[j] = PathPoint.createFromBuffer(buf);
        }

        PathPoint[] apathpoint1 = new PathPoint[buf.readInt()];

        for (int k = 0; k < apathpoint1.length; ++k)
        {
            apathpoint1[k] = PathPoint.createFromBuffer(buf);
        }

        PathPoint[] apathpoint2 = new PathPoint[buf.readInt()];

        for (int l = 0; l < apathpoint2.length; ++l)
        {
            apathpoint2[l] = PathPoint.createFromBuffer(buf);
        }

        Path path = new Path(apathpoint);
        path.openSet = apathpoint1;
        path.closedSet = apathpoint2;
        path.target = pathpoint;
        path.currentPathIndex = i;
        return path;
    }
}