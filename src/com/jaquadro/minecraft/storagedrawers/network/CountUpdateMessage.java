package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class CountUpdateMessage implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int slot;
    private int count;

    private boolean failed;

    public CountUpdateMessage () { }

    public CountUpdateMessage (BlockPos pos, int slot, int count) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.slot = slot;
        this.count = count;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        try {
            x = buf.readInt();
            y = buf.readShort();
            z = buf.readInt();
            slot = buf.readByte();
            count = buf.readInt();
        }
        catch (IndexOutOfBoundsException e) {
            failed = true;
            FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, e, "CountUpdateMessage: Unexpected end of packet.\nMessage: %s", ByteBufUtil.hexDump(buf, 0, buf.writerIndex()));
        }
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(slot);
        buf.writeInt(count);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<CountUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage (CountUpdateMessage message, MessageContext ctx) {
            if (!message.failed && ctx.side == Side.CLIENT) {
                World world = Minecraft.getMinecraft().theWorld;
                BlockPos pos = new BlockPos(message.x, message.y, message.z);
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TileEntityDrawers) {
                    ((TileEntityDrawers) tileEntity).clientUpdateCount(message.slot, message.count);
                }
            }

            return null;
        }
    }

    public static class HandlerStub implements IMessageHandler<CountUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage (CountUpdateMessage message, MessageContext ctx) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.WARN, "CountUpdateMessage stub handler called.");
            return null;
        }
    }
}
