package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockTrim extends Block implements INetworked
{
    public BlockTrim (BlockBehaviour.Properties properties) {
        super(properties);
    }
}
