/*package com.jaquadro.minecraft.storagedrawers.core.api;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.util.UniqueMetaIdentifier;
import com.jaquadro.minecraft.storagedrawers.util.UniqueMetaRegistry;
import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistry
{
    private Map<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>> registry = new HashMap<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>>();
    private Map<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>> invertRegistry = new HashMap<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>>();

    public void register (BlockConfiguration config, IExtendedDataResolver resolver) {
        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i))
                register(config, resolver.getBlock(config), i, resolver.getPlankBlock(i), resolver.getPlankMeta(i));
        }
    }

    public void register (BlockConfiguration config, Block block, int meta, Block plankBlock, int plankMeta) {
        UniqueMetaRegistry<UniqueMetaIdentifier> uniq = registry.get(config);
        if (uniq == null) {
            uniq = new UniqueMetaRegistry<UniqueMetaIdentifier>();
            registry.put(config, uniq);
        }

        uniq.register(UniqueMetaIdentifier.createFor(plankBlock, plankMeta), UniqueMetaIdentifier.createFor(block, meta));

        UniqueMetaRegistry<UniqueMetaIdentifier> blockReg = invertRegistry.get(config);
        if (blockReg == null) {
            blockReg = new UniqueMetaRegistry<UniqueMetaIdentifier>();
            invertRegistry.put(config, blockReg);
        }

        blockReg.register(UniqueMetaIdentifier.createFor(block, meta), UniqueMetaIdentifier.createFor(plankBlock, plankMeta));
    }

    public Block getBlock (BlockConfiguration config, Block plankBlock, int plankMeta) {
        return blockLookup(registry, config, plankBlock, plankMeta);
    }

    public int getMeta (BlockConfiguration config, Block plankBlock, int plankMeta) {
        return metaLookup(registry, config, plankBlock, plankMeta);
    }

    public Block getPlankBlock (BlockConfiguration config, Block block, int meta) {
        return blockLookup(invertRegistry, config, block, meta);
    }

    public int getPlankMeta (BlockConfiguration config, Block block, int meta) {
        return metaLookup(invertRegistry, config, block, meta);
    }

    private Block blockLookup (Map<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>> reg, BlockConfiguration config, Block block, int meta) {
        UniqueMetaRegistry<UniqueMetaIdentifier> uniq = reg.get(config);
        if (uniq == null)
            return null;

        UniqueMetaIdentifier id = uniq.getEntry(UniqueMetaIdentifier.createFor(block, meta));
        if (id == null)
            return null;

        return id.getBlock();
    }

    private int metaLookup (Map<BlockConfiguration, UniqueMetaRegistry<UniqueMetaIdentifier>> reg, BlockConfiguration config, Block block, int meta) {
        UniqueMetaRegistry<UniqueMetaIdentifier> uniq = reg.get(config);
        if (uniq == null)
            return 0;

        UniqueMetaIdentifier id = uniq.getEntry(UniqueMetaIdentifier.createFor(block, meta));
        if (id == null)
            return 0;

        return id.meta;
    }
}
*/