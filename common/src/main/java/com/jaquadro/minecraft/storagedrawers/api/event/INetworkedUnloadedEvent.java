package com.jaquadro.minecraft.storagedrawers.api.event;

import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.Event;

public class INetworkedUnloadedEvent extends Event {
    private final BlockPos pos;
    private final BaseBlockEntity entity;

    public INetworkedUnloadedEvent(BaseBlockEntity entity, BlockPos pos) {
        this.entity = entity;
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BaseBlockEntity getEntity() {
        return entity;
    }


}
