package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChunkCompileTaskGenerator implements Comparable<ChunkCompileTaskGenerator>
{
    private final RenderChunk renderChunk;
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Runnable> listFinishRunnables = Lists.<Runnable>newArrayList();
    private final ChunkCompileTaskGenerator.Type type;
    private final double distanceSq;
    private RegionRenderCacheBuilder regionRenderCacheBuilder;
    private CompiledChunk compiledChunk;
    private ChunkCompileTaskGenerator.Status status = ChunkCompileTaskGenerator.Status.PENDING;
    private boolean finished;

    public ChunkCompileTaskGenerator(RenderChunk p_i46560_1_, ChunkCompileTaskGenerator.Type p_i46560_2_, double p_i46560_3_)
    {
        this.renderChunk = p_i46560_1_;
        this.type = p_i46560_2_;
        this.distanceSq = p_i46560_3_;
    }

    public ChunkCompileTaskGenerator.Status getStatus()
    {
        return this.status;
    }

    public RenderChunk getRenderChunk()
    {
        return this.renderChunk;
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.compiledChunk = compiledChunkIn;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder()
    {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn)
    {
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }

    public void setStatus(ChunkCompileTaskGenerator.Status statusIn)
    {
        this.lock.lock();

        try
        {
            this.status = statusIn;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public void finish()
    {
        this.lock.lock();

        try
        {
            if (this.type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.status != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.renderChunk.setNeedsUpdate(false);
            }

            this.finished = true;
            this.status = ChunkCompileTaskGenerator.Status.DONE;

            for (Runnable runnable : this.listFinishRunnables)
            {
                runnable.run();
            }
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public void addFinishRunnable(Runnable runnable)
    {
        this.lock.lock();

        try
        {
            this.listFinishRunnables.add(runnable);

            if (this.finished)
            {
                runnable.run();
            }
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock()
    {
        return this.lock;
    }

    public ChunkCompileTaskGenerator.Type getType()
    {
        return this.type;
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public int compareTo(ChunkCompileTaskGenerator p_compareTo_1_)
    {
        return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
    }

    public double getDistanceSq()
    {
        return this.distanceSq;
    }

    @SideOnly(Side.CLIENT)
    public static enum Status
    {
        PENDING,
        COMPILING,
        UPLOADING,
        DONE;
    }

    @SideOnly(Side.CLIENT)
    public static enum Type
    {
        REBUILD_CHUNK,
        RESORT_TRANSPARENCY;
    }
}