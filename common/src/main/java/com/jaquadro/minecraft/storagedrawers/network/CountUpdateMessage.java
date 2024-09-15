package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public record CountUpdateMessage(int x, int y, int z, int slot, int count) implements ChameleonPacket
{
    public static final Type<CountUpdateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "count_update"));

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

    @Override
    public void handleMessage (Player player, Consumer<Runnable> workQueue) {
        if (!(player instanceof ServerPlayer)) {
            workQueue.accept(() -> {
                handleClient();
            });
        }
    }

    void handleClient() {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            //if (blockEntity instanceof BlockEntityDrawers) {
            //    ((BlockEntityDrawers) blockEntity).clientUpdateCount(slot, count);
            //}
        }
    }
}
