package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import io.netty.buffer.ByteBuf;
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

public class ControllerUpdateMessage  implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int[] inventorySlots;

    public ControllerUpdateMessage () { }

    public ControllerUpdateMessage (BlockPos pos, int[] inventorySlots) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.inventorySlots = inventorySlots;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        x = buf.readInt();
        y = buf.readShort();
        z = buf.readInt();

        int count = buf.readShort();
        inventorySlots = new int[count];

        for (int i = 0; i < count; i++)
            inventorySlots[i] = buf.readShort();
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
            if (ctx.side == Side.CLIENT) {
                World world = Minecraft.getMinecraft().theWorld;
                BlockPos pos = new BlockPos(message.x, message.y, message.z);
                TileEntity tileEntity = world.getTileEntity(pos);
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
