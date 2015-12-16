package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.block.IExtendedBlockClickHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockClickMessage implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int side;
    private float hitX;
    private float hitY;
    private float hitZ;
    private boolean invertShift;

    public BlockClickMessage () { }

    public BlockClickMessage (int x, int y, int z, int side, float hitX, float hitY, float hitZ, boolean invertShift) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        this.invertShift = invertShift;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        x = buf.readInt();
        y = buf.readShort();
        z = buf.readInt();
        side = buf.readByte();
        hitX = buf.readFloat();
        hitY = buf.readFloat();
        hitZ = buf.readFloat();
        invertShift = buf.readBoolean();
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(side);
        buf.writeFloat(hitX);
        buf.writeFloat(hitY);
        buf.writeFloat(hitZ);
        buf.writeBoolean(invertShift);
    }

    public static class Handler implements IMessageHandler<BlockClickMessage, IMessage>
    {
        @Override
        public IMessage onMessage (BlockClickMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                Block block = world.getBlock(message.x, message.y, message.z);
                if (block instanceof IExtendedBlockClickHandler)
                    ((IExtendedBlockClickHandler) block).onBlockClicked(world, message.x, message.y, message.z, ctx.getServerHandler().playerEntity, message.side, message.hitX, message.hitY, message.hitZ, message.invertShift);
            }

            return null;
        }
    }
}
