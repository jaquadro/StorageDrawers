package com.jaquadro.minecraft.storagedrawers.util;

import com.google.common.base.Objects;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class UniqueMetaIdentifier
{
    private final ResourceLocation loc;
    private final int meta;

    public UniqueMetaIdentifier (String modId, String name) {
        this.loc = new ResourceLocation(modId, name);
        this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public UniqueMetaIdentifier (String modId, String name, int meta) {
        this.loc = new ResourceLocation(modId, name);
        this.meta = meta;
    }

    public UniqueMetaIdentifier (String qualifiedName, int meta) {
        this.loc = new ResourceLocation(qualifiedName);
        this.meta = meta;
    }

    public UniqueMetaIdentifier (String compoundName) {
        this(compoundName, ';');
    }

    public UniqueMetaIdentifier (ResourceLocation loc) {
        this.loc = loc;
        this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public UniqueMetaIdentifier (ResourceLocation loc, int meta) {
        this.loc = loc;
        this.meta = meta;
    }

    public UniqueMetaIdentifier (String compoundName, char separator) {
        String[] parts1 = compoundName.split("[ ]*" + separator + "[ ]*");

        this.loc = new ResourceLocation(parts1[0]);

        if (parts1.length >= 2)
            this.meta = Integer.parseInt(parts1[1]);
        else
            this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public ResourceLocation getResourceLocation () {
        return loc;
    }

    public int getMeta () {
        return meta;
    }

    public String getModID () {
        return loc.getResourceDomain();
    }

    public String getName() {
        return loc.getResourcePath();
    }

    public Block getBlock () {
        return GameRegistry.findBlock(loc.getResourceDomain(), loc.getResourcePath());
    }

    public Item getItem () {
        return GameRegistry.findItem(loc.getResourceDomain(), loc.getResourcePath());
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;

        final UniqueMetaIdentifier other = (UniqueMetaIdentifier) obj;

        return Objects.equal(loc, other.loc)
            && meta == other.meta;
    }

    @Override
    public int hashCode () {
        return Objects.hashCode(loc) ^ (meta * 37);
    }

    @Override
    public String toString () {
        return String.format("%s;%d", loc, meta);
    }

    public static UniqueMetaIdentifier createFor (ItemStack itemStack) {
        if (itemStack.getItem() == null)
            return null;

        ResourceLocation name = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        return new UniqueMetaIdentifier(name, itemStack.getItemDamage());
    }

    public static UniqueMetaIdentifier createFor (Block block, int meta) {
        if (block == null)
            return null;

        ResourceLocation name = GameData.getBlockRegistry().getNameForObject(block);
        return new UniqueMetaIdentifier(name, meta);
    }

    public static UniqueMetaIdentifier createFor (Block block) {
        if (block == null)
            return null;

        ResourceLocation name = GameData.getBlockRegistry().getNameForObject(block);
        return new UniqueMetaIdentifier(name);
    }
}
