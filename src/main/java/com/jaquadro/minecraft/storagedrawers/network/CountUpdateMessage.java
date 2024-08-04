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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

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

    public CountUpdateMessage(FriendlyByteBuf buf) {
        try {
            this.x = buf.readInt();
            this.y = buf.readShort();
            this.z = buf.readInt();
            this.slot = buf.readByte();
            this.count = buf.readInt();
        }
        catch (IndexOutOfBoundsException e) {
            StorageDrawers.log.error("CountUpdateMessage: Unexpected end of packet.\nMessage: " + ByteBufUtil.hexDump(buf, 0, buf.writerIndex()), e);
            this.failed = true;
            return;
        }

        this.failed = false;
    }

    public void write (FriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(slot);
        buf.writeInt(count);
    }

    public void handle(NetworkEvent.Context ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(this, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(CountUpdateMessage msg, NetworkEvent.Context ctx) {
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
