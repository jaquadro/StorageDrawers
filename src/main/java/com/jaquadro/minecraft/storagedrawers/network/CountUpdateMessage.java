package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CountUpdateMessage(int x, int y, int z, int slot, int count) implements CustomPacketPayload
{
    public static final Type<CountUpdateMessage> TYPE = new Type<>(new ResourceLocation(StorageDrawers.MOD_ID, "count_update"));

    public static final StreamCodec<FriendlyByteBuf, CountUpdateMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        CountUpdateMessage::x,
        ByteBufCodecs.INT,
        CountUpdateMessage::y,
        ByteBufCodecs.INT,
        CountUpdateMessage::z,
        ByteBufCodecs.INT,
        CountUpdateMessage::slot,
        ByteBufCodecs.INT,
        CountUpdateMessage::count,
        CountUpdateMessage::new
    );

    public CountUpdateMessage (BlockPos pos, int slot, int count) {
        this(pos.getX(), pos.getY(), pos.getZ(), slot, count);
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    public static void handle(final CountUpdateMessage data, final IPayloadContext context) {
        if (Dist.CLIENT.isClient())
            handleClient(data, context);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleClient(final CountUpdateMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
            if (world != null) {
                BlockPos pos = new BlockPos(data.x, data.y, data.z);
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BlockEntityDrawers) {
                    ((BlockEntityDrawers) blockEntity).clientUpdateCount(data.slot, data.count);
                }
            }
        }).exceptionally(e -> null);
    }
}
