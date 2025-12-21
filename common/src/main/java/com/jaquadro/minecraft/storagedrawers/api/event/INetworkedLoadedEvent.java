package com.jaquadro.minecraft.storagedrawers.api.event;

import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.Event;

public class INetworkedLoadedEvent extends Event {
    private final BlockPos pos;
    private final BaseBlockEntity entity;

    public INetworkedLoadedEvent(BaseBlockEntity entity, BlockPos pos) {
        this.pos = pos;
        this.entity = entity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BaseBlockEntity getEntity() {
        return entity;
    }


}
