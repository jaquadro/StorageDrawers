package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public class CountUpdateMessage
{
    private int x;
    private int y;
    private int z;
    private int slot;
    private int count;

    private final boolean failed;

    public CountUpdateMessage (BlockPos pos, int slot, int count) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.slot = slot;
        this.count = count;
        this.failed = false;
    }

    private CountUpdateMessage (boolean failed) {
        this.failed = failed;
    }

    public static CountUpdateMessage decode (ByteBuf buf) {
        try {
            int x = buf.readInt();
            int y = buf.readShort();
            int z = buf.readInt();
            int slot = buf.readByte();
            int count = buf.readInt();
            return new CountUpdateMessage(new BlockPos(x, y, z), slot, count);
        }
        catch (IndexOutOfBoundsException e) {
            StorageDrawers.log.error("CountUpdateMessage: Unexpected end of packet.\nMessage: " + ByteBufUtil.hexDump(buf, 0, buf.writerIndex()), e);
            return new CountUpdateMessage(true);
        }
    }

    public static void encode (CountUpdateMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.x);
        buf.writeShort(msg.y);
        buf.writeInt(msg.z);
        buf.writeByte(msg.slot);
        buf.writeInt(msg.count);
    }

    public static void handle(CountUpdateMessage msg, CustomPayloadEvent.Context ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(CountUpdateMessage msg, CustomPayloadEvent.Context ctx) {
        if (!msg.failed) {
            Level world = Minecraft.getInstance().level;
            if (world != null) {
                BlockPos pos = new BlockPos(msg.x, msg.y, msg.z);
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityDrawers) {
                    ((BlockEntityDrawers) blockEntity).clientUpdateCount(msg.slot, msg.count);
                }
            }
        }
        ctx.setPacketHandled(true);
    }
}
