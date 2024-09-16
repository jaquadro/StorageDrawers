package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersStandard;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStandardDrawers extends BlockDrawers
{
    public static final MapCodec<BlockStandardDrawers> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("drawerCount").forGetter(BlockDrawers::getDrawerCount),
            Codec.BOOL.fieldOf("halfDepth").forGetter(BlockDrawers::isHalfDepth),
            Codec.INT.fieldOf("storageUnits").forGetter(BlockDrawers::getStorageUnits),
            propertiesCodec()
        ).apply(instance, BlockStandardDrawers::new)
    );

    private String matKey = null;
    private String matNamespace = ModConstants.MOD_ID;

    public BlockStandardDrawers (int drawerCount, boolean halfDepth, int storageUnits, Properties properties) {
       super(drawerCount, halfDepth, storageUnits, properties);
    }

    public BlockStandardDrawers (int drawerCount, boolean halfDepth, Properties properties) {
        super(drawerCount, halfDepth, calcUnits(drawerCount, halfDepth), properties);
    }

    private static int calcUnits (int drawerCount, boolean halfDepth) {
        return halfDepth ? 4 / drawerCount : 8 / drawerCount;
    }

    public BlockStandardDrawers setMatKey (ResourceLocation material) {
        this.matNamespace = material.getNamespace();
        this.matKey = material.getPath();
        return this;
    }

    public BlockStandardDrawers setMatKey (@Nullable String matKey) {
        this.matKey = matKey;
        return this;
    }

    public String getMatKey () {
        return matKey;
    }

    public String getNameMatKey () {
        return "block." + matNamespace + ".mat." + matKey;
    }

    @Override
    public MapCodec<BlockStandardDrawers> codec() {
        return CODEC;
    }

    @Override
    protected int getDrawerSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        if (!hitAny(correctSide, normalizedHit))
            return super.getDrawerSlot(correctSide, normalizedHit);

        if (getDrawerCount() == 1)
            return 0;

        boolean hitTop = hitTop(normalizedHit);

        if (getDrawerCount() == 2)
            return hitTop ? 0 : 1;

        if (getDrawerCount() == 4) {
            if (hitLeft(correctSide, normalizedHit))
                return hitTop ? 0 : 2;
            else
                return hitTop ? 1 : 3;
        }

        return super.getDrawerSlot(correctSide, normalizedHit);
    }

    @Override
    @Nullable
    public BlockEntityDrawers newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModServices.RESOURCE_FACTORY.createBlockEntityDrawersStandard(getDrawerCount()).create(pos, state);
    }
}
