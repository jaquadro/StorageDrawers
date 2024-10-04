package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialData extends BlockEntityDataShim implements IFramedMaterials
{
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

    @Override
    public void read (CompoundTag tag) {
        frameBase = ItemStack.EMPTY;
        if (tag.contains("MatB"))
            frameBase = ItemStack.of(tag.getCompound("MatB"));

        materialSide = ItemStack.EMPTY;
        if (tag.contains("MatS"))
            materialSide = ItemStack.of(tag.getCompound("MatS"));

        materialFront = ItemStack.EMPTY;
        if (tag.contains("MatF"))
            materialFront = ItemStack.of(tag.getCompound("MatF"));

        materialTrim = ItemStack.EMPTY;
        if (tag.contains("MatT"))
            materialTrim = ItemStack.of(tag.getCompound("MatT"));
    }

    @Override
    public CompoundTag write (CompoundTag tag) {
        if (!frameBase.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            frameBase.save(itag);
            tag.put("MatB", itag);
        } else if (tag.contains("MatB"))
            tag.remove("MatB");

        if (!materialSide.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            materialSide.save(itag);
            tag.put("MatS", itag);
        } else if (tag.contains("MatS"))
            tag.remove("MatS");

        if (!materialFront.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            materialFront.save(itag);
            tag.put("MatF", itag);
        } else if (tag.contains("MatF"))
            tag.remove("MatF");

        if (!materialTrim.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            materialTrim.save(itag);
            tag.put("MatT", itag);
        } else if (tag.contains("MatT"))
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
