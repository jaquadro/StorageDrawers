package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class ControllerUpdateMessage  implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int[] inventorySlots;
    private boolean error;

    public ControllerUpdateMessage () { }

    public ControllerUpdateMessage (int x, int y, int z, int[] inventorySlots) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.inventorySlots = inventorySlots;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        x = buf.readInt();
        y = buf.readShort();
        z = buf.readInt();

        int count = buf.readShort();
        if (count >= 0 && count <= buf.readableBytes() / 2) {
            inventorySlots = new int[count];

            for (int i = 0; i < count; i++)
                inventorySlots[i] = buf.readShort();
        }
        else {
            FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, "ControllerUpdateMessage invalid data: count = %i, remaining = %i", count, buf.readableBytes());
            error = true;
        }
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);

        buf.writeShort(inventorySlots.length);
        for (int i = 0; i < inventorySlots.length; i++)
            buf.writeShort(inventorySlots[i]);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<ControllerUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage (ControllerUpdateMessage message, MessageContext ctx) {
            if (message.error)
                return null;

            if (ctx.side == Side.CLIENT) {
                World world = Minecraft.getMinecraft().theWorld;
                TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
                if (tileEntity instanceof TileEntityController) {
                    ((TileEntityController) tileEntity).clientUpdate(message.inventorySlots);
                }
            }

            return null;
        }
    }

    public static class HandlerStub implements IMessageHandler<ControllerUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage (ControllerUpdateMessage message, MessageContext ctx) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.WARN, "ControllerUpdateMessage stub handler called.");
            return null;
        }
    }
}
