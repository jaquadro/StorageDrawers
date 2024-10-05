package com.jaquadro.minecraft.storagedrawers.components.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ControllerBinding (boolean valid, int x, int y, int z)
{
    public static final ControllerBinding EMPTY = new ControllerBinding(false, 0, 0, 0);

    public static final Codec<ControllerBinding> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.BOOL.fieldOf("valid").forGetter(ControllerBinding::valid),
            Codec.INT.fieldOf("x").forGetter(ControllerBinding::x),
            Codec.INT.fieldOf("y").forGetter(ControllerBinding::y),
            Codec.INT.fieldOf("z").forGetter(ControllerBinding::z)
        ).apply(instance, ControllerBinding::new));

    public static final StreamCodec<FriendlyByteBuf, ControllerBinding> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ControllerBinding::valid,
        ByteBufCodecs.INT,
        ControllerBinding::x,
        ByteBufCodecs.INT,
        ControllerBinding::y,
        ByteBufCodecs.INT,
        ControllerBinding::z,
        ControllerBinding::new
    );

    public ControllerBinding (BlockPos blockPos) {
        this(true, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public BlockPos blockPos () {
        return new BlockPos(x, y, z);
    }
}
