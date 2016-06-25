package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.IBlockDestroyHandler;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class BlockDestroyMessage implements IMessage
{
    private int x;
    private int y;
    private int z;

    private boolean failed;

    public BlockDestroyMessage () { }

    public BlockDestroyMessage (BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        try {
            x = buf.readInt();
            y = buf.readShort();
            z = buf.readInt();
        }
        catch (IndexOutOfBoundsException e) {
            failed = true;
            FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, e, "BlockDestroyMessage: Unexpected end of packet.\nMessage: %s", ByteBufUtil.hexDump(buf, 0, buf.writerIndex()));
        }
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<BlockDestroyMessage, IMessage>
    {
        @Override
        public IMessage onMessage (BlockDestroyMessage message, MessageContext ctx) {
            if (!message.failed && ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                if (world != null) {
                    BlockPos pos = new BlockPos(message.x, message.y, message.z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof IBlockDestroyHandler)
                        ((IBlockDestroyHandler) block).onBlockDestroyed(world, pos);
                }
            }

            return null;
        }
    }
}
