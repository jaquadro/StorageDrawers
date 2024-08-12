package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record CountUpdateMessage(int x, int y, int z, int slot, int count) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(StorageDrawers.MOD_ID, "count_update");

    public CountUpdateMessage (BlockPos pos, int slot, int count) {
        this(pos.getX(), pos.getY(), pos.getZ(), slot, count);
    }

    public CountUpdateMessage(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readShort(), buf.readInt(), buf.readByte(), buf.readInt());
    }

    @Override
    public void write (FriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(slot);
        buf.writeInt(count);
    }

    @Override
    public ResourceLocation id () {
        return ID;
    }

    public static void handleClient(final CountUpdateMessage data, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            handleClient(data);
        }).exceptionally(e -> null);
    }

    @OnlyIn(Dist.CLIENT)
    static void handleClient(final CountUpdateMessage data) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            BlockPos pos = new BlockPos(data.x, data.y, data.z);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlockEntityDrawers) {
                ((BlockEntityDrawers) blockEntity).clientUpdateCount(data.slot, data.count);
            }
        }
    }
}
