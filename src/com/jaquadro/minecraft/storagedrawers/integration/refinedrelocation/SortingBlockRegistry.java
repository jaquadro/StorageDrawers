package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class SortingBlockRegistry
{
    private static Map<Block, Block> registry = new HashMap<Block, Block>();

    public static void register (Block storageBlock, Block sortingBlock) {
        registry.put(storageBlock, sortingBlock);
    }

    public static Block resolveSortingBlock (Block storageBlock) {
        return registry.get(storageBlock);
    }
}
