package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ChunkRenderWorker implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkRenderDispatcher chunkRenderDispatcher;
    private final RegionRenderCacheBuilder regionRenderCacheBuilder;
    private boolean shouldRun;

    public ChunkRenderWorker(ChunkRenderDispatcher p_i46201_1_)
    {
        this(p_i46201_1_, (RegionRenderCacheBuilder)null);
    }

    public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, @Nullable RegionRenderCacheBuilder regionRenderCacheBuilderIn)
    {
        this.shouldRun = true;
        this.chunkRenderDispatcher = chunkRenderDispatcherIn;
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }

    public void run()
    {
        while (this.shouldRun)
        {
            try
            {
                this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
            }
            catch (InterruptedException var3)
            {
                LOGGER.debug("Stopping chunk worker due to interrupt");
                return;
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Batching chunks");
                Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(crashreport));
                return;
            }
        }
    }

    protected void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException
    {
        generator.getLock().lock();

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING)
            {
                if (!generator.isFinished())
                {
                    LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", new Object[] {generator.getStatus()});
                }

                return;
            }

            BlockPos blockpos = new BlockPos(Minecraft.getMinecraft().player);
            BlockPos blockpos1 = generator.getRenderChunk().getPosition();
            int i = 16;
            int j = 8;
            int k = 24;

            if (blockpos1.add(8, 8, 8).distanceSq(blockpos) > 576.0D)
            {
                World world = generator.getRenderChunk().getWorld();
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(blockpos1);

                if (!this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.WEST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.NORTH, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.EAST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.SOUTH, 16), world))
                {
                    return;
                }
            }

            generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
        }
        finally
        {
            generator.getLock().unlock();
        }

        Entity lvt_2_2_ = Minecraft.getMinecraft().getRenderViewEntity();

        if (lvt_2_2_ == null)
        {
            generator.finish();
        }
        else
        {
            generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
            float f = (float)lvt_2_2_.posX;
            float f1 = (float)lvt_2_2_.posY + lvt_2_2_.getEyeHeight();
            float f2 = (float)lvt_2_2_.posZ;
            ChunkCompileTaskGenerator.Type chunkcompiletaskgenerator$type = generator.getType();

            if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK)
            {
                generator.getRenderChunk().rebuildChunk(f, f1, f2, generator);
            }
            else if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)
            {
                generator.getRenderChunk().resortTransparency(f, f1, f2, generator);
            }

            generator.getLock().lock();

            try
            {
                if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
                {
                    if (!generator.isFinished())
                    {
                        LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", new Object[] {generator.getStatus()});
                    }

                    this.freeRenderBuilder(generator);
                    return;
                }

                generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
            }
            finally
            {
                generator.getLock().unlock();
            }

            final CompiledChunk lvt_7_2_ = generator.getCompiledChunk();
            ArrayList lvt_8_2_ = Lists.newArrayList();

            if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK)
            {
                for (BlockRenderLayer blockrenderlayer : BlockRenderLayer.values())
                {
                    if (lvt_7_2_.isLayerStarted(blockrenderlayer))
                    {
                        lvt_8_2_.add(this.chunkRenderDispatcher.uploadChunk(blockrenderlayer, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), generator.getRenderChunk(), lvt_7_2_, generator.getDistanceSq()));
                    }
                }
            }
            else if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)
            {
                lvt_8_2_.add(this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT), generator.getRenderChunk(), lvt_7_2_, generator.getDistanceSq()));
            }

            final ListenableFuture<List<Object>> listenablefuture = Futures.allAsList(lvt_8_2_);
            generator.addFinishRunnable(new Runnable()
            {
                public void run()
                {
                    listenablefuture.cancel(false);
                }
            });
            Futures.addCallback(listenablefuture, new FutureCallback<List<Object>>()
            {
                public void onSuccess(@Nullable List<Object> p_onSuccess_1_)
                {
                    ChunkRenderWorker.this.freeRenderBuilder(generator);
                    generator.getLock().lock();
                    label21:
                    {
                        try
                        {
                            if (generator.getStatus() == ChunkCompileTaskGenerator.Status.UPLOADING)
                            {
                                generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
                                break label21;
                            }

                            if (!generator.isFinished())
                            {
                                ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", new Object[] {generator.getStatus()});
                            }
                        }
                        finally
                        {
                            generator.getLock().unlock();
                        }

                        return;
                    }
                    generator.getRenderChunk().setCompiledChunk(lvt_7_2_);
                }
                public void onFailure(Throwable p_onFailure_1_)
                {
                    ChunkRenderWorker.this.freeRenderBuilder(generator);

                    if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException))
                    {
                        Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
                    }
                }
            });
        }
    }

    private boolean isChunkExisting(BlockPos p_188263_1_, World p_188263_2_)
    {
        return !p_188263_2_.getChunkFromChunkCoords(p_188263_1_.getX() >> 4, p_188263_1_.getZ() >> 4).isEmpty();
    }

    private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException
    {
        return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
    }

    private void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator)
    {
        if (this.regionRenderCacheBuilder == null)
        {
            this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
        }
    }

    public void notifyToStop()
    {
        this.shouldRun = false;
    }
}