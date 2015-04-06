package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.block.IExtendedBlockClickHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class BlockClickMessage implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int side;
    private float hitX;
    private float hitY;
    private float hitZ;

    public BlockClickMessage () { }

    public BlockClickMessage (int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        x = buf.readInt();
        y = buf.readShort();
        z = buf.readInt();
        side = buf.readByte();
        hitX = buf.readByte() / 16f;
        hitY = buf.readByte() / 16f;
        hitZ = buf.readByte() / 16f;
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(side);
        buf.writeByte((int)(hitX * 16));
        buf.writeByte((int)(hitY * 16));
        buf.writeByte((int)(hitZ * 16));
    }

    public static class Handler implements IMessageHandler<BlockClickMessage, IMessage>
    {
        @Override
        public IMessage onMessage (BlockClickMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                BlockPos pos = new BlockPos(message.x, message.y, message.z);
                Block block = world.getBlockState(pos).getBlock();
                if (block instanceof IExtendedBlockClickHandler)
                    ((IExtendedBlockClickHandler) block).onBlockClicked(world, pos, ctx.getServerHandler().playerEntity, EnumFacing.getFront(message.side), message.hitX, message.hitY, message.hitZ);
            }

            return null;
        }
    }
}
