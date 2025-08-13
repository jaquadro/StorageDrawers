package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedMaterials;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialData extends BlockEntityDataShim implements IFramedMaterials
{
    public static final MaterialData EMPTY = new MaterialData();

    public static final Codec<MaterialData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("frameBase").forGetter(MaterialData::getFrameBase),
            ItemStack.CODEC.fieldOf("materialSide").forGetter(MaterialData::getSide),
            ItemStack.CODEC.fieldOf("materialFront").forGetter(MaterialData::getFront),
            ItemStack.CODEC.fieldOf("materialTrim").forGetter(MaterialData::getTrim)
        ).apply(instance, MaterialData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialData> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        MaterialData::getFrameBase,
        ItemStack.OPTIONAL_STREAM_CODEC,
        MaterialData::getSide,
        ItemStack.OPTIONAL_STREAM_CODEC,
        MaterialData::getFront,
        ItemStack.OPTIONAL_STREAM_CODEC,
        MaterialData::getTrim,
        MaterialData::new
    );

    @NotNull
    private ItemStack frameBase;
    @NotNull
    private ItemStack materialSide;
    @NotNull
    private ItemStack materialFront;
    @NotNull
    private ItemStack materialTrim;

    public MaterialData () {
        this(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public MaterialData (@NotNull ItemStack frameBase, @NotNull ItemStack side, @NotNull ItemStack front, @NotNull ItemStack trim) {
        this.frameBase = frameBase;
        materialSide = side;
        materialFront = front;
        materialTrim = trim;
    }

    public MaterialData (IFramedMaterials materials) {
        this();

        if (materials != null) {
            frameBase = materials.getHostBlock();
            materialSide = materials.getMaterial(FrameMaterial.SIDE);
            materialFront = materials.getMaterial(FrameMaterial.FRONT);
            materialTrim = materials.getMaterial(FrameMaterial.TRIM);
        }
    }

    @NotNull
    public ItemStack getFrameBase() {
        return frameBase;
    }

    @NotNull
    public ItemStack getSide () {
        return materialSide;
    }

    @NotNull
    public ItemStack getFront () {
        return materialFront;
    }

    @NotNull
    public ItemStack getTrim () {
        return materialTrim;
    }

    @NotNull
    public ItemStack getEffectiveSide () {
        return materialSide;
    }

    public boolean isMatOpaque (ItemStack mat) {
        if (mat.getItem() instanceof BlockItem blockItem)
            return blockItem.getBlock().defaultBlockState().canOcclude();
        return false;
    }

    public boolean allMatOpaque () {
        return isMatOpaque(materialSide)
            && (materialFront.isEmpty() || isMatOpaque(materialFront))
            && (materialTrim.isEmpty() || isMatOpaque(materialTrim));
    }

    @NotNull
    public ItemStack getEffectiveFront () {
        return !materialFront.isEmpty() ? materialFront : materialSide;
    }

    @NotNull
    public ItemStack getEffectiveTrim () {
        return !materialTrim.isEmpty() ? materialTrim : materialSide;
    }

    public void setFrameBase (@NotNull ItemStack frameBase) {
        this.frameBase = frameBase;
    }

    public void setSide (@NotNull ItemStack material) {
        materialSide = material;
    }

    public void setFront (@NotNull ItemStack material) {
        materialFront = material;
    }

    public void setTrim (@NotNull ItemStack material) {
        materialTrim = material;
    }

    public void clear () {
        materialSide = ItemStack.EMPTY;
        materialFront = ItemStack.EMPTY;
        materialTrim = ItemStack.EMPTY;
    }

    public boolean isEmpty () {
        return materialFront.isEmpty() && materialSide.isEmpty() && materialTrim.isEmpty();
    }

    public void read (ItemStack stack) {
        FrameData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY);

        frameBase = data.base();
        materialSide = data.side();
        materialFront = data.front();
        materialTrim = data.trim();
    }

    @Override
    public void read (HolderLookup.Provider provider, CompoundTag tag) {
        frameBase = ItemStack.EMPTY;
        if (tag.contains("MatB"))
            frameBase = ItemStack.parseOptional(provider, tag.getCompound("MatB"));

        materialSide = ItemStack.EMPTY;
        if (tag.contains("MatS"))
            materialSide = ItemStack.parseOptional(provider, tag.getCompound("MatS"));

        materialFront = ItemStack.EMPTY;
        if (tag.contains("MatF"))
            materialFront = ItemStack.parseOptional(provider, tag.getCompound("MatF"));

        materialTrim = ItemStack.EMPTY;
        if (tag.contains("MatT"))
            materialTrim = ItemStack.parseOptional(provider, tag.getCompound("MatT"));
    }

    @Override
    public CompoundTag write (HolderLookup.Provider provider, CompoundTag tag) {
        if (!frameBase.isEmpty())
            tag.put("MatB", frameBase.saveOptional(provider));
        else if (tag.contains("MatB"))
            tag.remove("MatB");

        if (!materialSide.isEmpty())
            tag.put("MatS", materialSide.saveOptional(provider));
        else if (tag.contains("MatS"))
            tag.remove("MatS");

        if (!materialFront.isEmpty())
            tag.put("MatF", materialFront.saveOptional(provider));
        else if (tag.contains("MatF"))
            tag.remove("MatF");

        if (!materialTrim.isEmpty())
            tag.put("MatT", materialTrim.saveOptional(provider));
        else if (tag.contains("MatT"))
            tag.remove("MatT");

        return tag;
    }

    @Override
    public @NotNull ItemStack getHostBlock () {
        return frameBase;
    }

    @Override
    public void setHostBlock (@NotNull ItemStack stack) {
        frameBase = stack;
    }

    @Override
    public @NotNull ItemStack getMaterial (FrameMaterial material) {
        return switch (material) {
            case SIDE -> materialSide;
            case TRIM -> materialTrim;
            case FRONT -> materialFront;
        };
    }

    @Override
    public void setMaterial (FrameMaterial material, @NotNull ItemStack stack) {
        switch (material) {
            case SIDE -> materialSide = stack;
            case TRIM -> materialTrim = stack;
            case FRONT -> materialFront = stack;
        }
    }
}