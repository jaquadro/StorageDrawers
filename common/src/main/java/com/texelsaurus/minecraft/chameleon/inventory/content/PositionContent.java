package com.texelsaurus.minecraft.chameleon.inventory.content;

import com.texelsaurus.minecraft.chameleon.util.WorldUtils;
import com.texelsaurus.minecraft.chameleon.inventory.ContainerContent;
import com.texelsaurus.minecraft.chameleon.inventory.ContainerContentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PositionContent(BlockPos pos) implements ContainerContent<PositionContent>
{
    public static final ContainerContentSerializer<PositionContent> SERIALIZER = new Serializer();

    @Override
    public ContainerContentSerializer<PositionContent> serializer () {
        return SERIALIZER;
    }

    public <T extends BlockEntity> T getBlockEntity(Level level, Class<T> type) {
        if (!level.isClientSide)
            return null;

        return WorldUtils.getBlockEntity(level, pos, type);
    }

    public static <T extends BlockEntity> T getOrNull(Optional<PositionContent> content, Level level, Class<T> type) {
        return content.map(pc -> pc.getBlockEntity(level, type)).orElse(null);
    }

    private static class Serializer implements ContainerContentSerializer<PositionContent>
    {
        @Override
        public @Nullable PositionContent from (FriendlyByteBuf buf) {
            return new PositionContent(buf.readBlockPos());
        }

        @Override
        public void to (FriendlyByteBuf buf, PositionContent content) {
            buf.writeBlockPos(content.pos);
        }
    }
}