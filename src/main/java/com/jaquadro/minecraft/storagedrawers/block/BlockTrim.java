package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.block.tile.util.FrameHelper;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

public class BlockTrim extends Block implements INetworked, IFramedSourceBlock
{
    private String matKey = null;
    private String matNamespace = StorageDrawers.MOD_ID;

    public BlockTrim (BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockTrim setMatKey (ResourceLocation material) {
        this.matNamespace = material.getNamespace();
        this.matKey = material.getPath();
        return this;
    }

    public BlockTrim setMatKey (@Nullable String matKey) {
        this.matKey = matKey;
        return this;
    }

    public String getMatKey () {
        return matKey;
    }

    public String getNameMatKey () {
        return "block." + matNamespace + ".mat." + matKey;
    }

    public String getNameTypeKey() {
        return "block." + StorageDrawers.MOD_ID + ".type.trim";
    }

    @Override
    public ItemStack makeFramedItem (ItemStack source, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        return FrameHelper.makeFramedItem(ModBlocks.FRAMED_TRIM.get(), source, matSide, matTrim, matFront);
    }
}
