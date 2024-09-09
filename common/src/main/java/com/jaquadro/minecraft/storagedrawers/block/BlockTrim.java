package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockTrim extends Block implements INetworked
{
    private String matKey = null;
    private String matNamespace = ModConstants.MOD_ID;

    public BlockTrim (Properties properties) {
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
        return "block." + ModConstants.MOD_ID + ".type.trim";
    }
}
