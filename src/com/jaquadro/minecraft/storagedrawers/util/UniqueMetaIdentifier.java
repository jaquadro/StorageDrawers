package com.jaquadro.minecraft.storagedrawers.util;

import com.google.common.base.Objects;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class UniqueMetaIdentifier
{
    public final String modId;
    public final String name;
    public final int meta;

    private GameRegistry.UniqueIdentifier cachedUID;

    public UniqueMetaIdentifier (String modId, String name) {
        this.modId = modId;
        this.name = name;
        this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public UniqueMetaIdentifier (String modId, String name, int meta) {
        this.modId = modId;
        this.name = name;
        this.meta = meta;
    }

    public UniqueMetaIdentifier (String qualifiedName, int meta) {
        String[] parts = qualifiedName.split(":");
        this.modId = parts[0];
        this.name = parts[1];
        this.meta = meta;
    }

    public UniqueMetaIdentifier (String compoundName) {
        String[] parts1 = compoundName.split(";");
        String[] parts2 = parts1[0].split(":");
        this.modId = parts2[0];

        if (parts2.length >= 2)
            this.name = parts2[1];
        else
            this.name = "";

        if (parts1.length >= 2)
            this.meta = Integer.parseInt(parts1[1]);
        else if (parts2.length > 2)
            this.meta = Integer.parseInt(parts2[parts2.length - 1]);
        else
            this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public UniqueMetaIdentifier (String compoundName, char separator) {
        String[] parts1 = compoundName.split("[ ]*" + separator + "[ ]*");
        String[] parts2 = parts1[0].split(":");
        this.modId = parts2[0];

        if (parts2.length >= 2)
            this.name = parts2[1];
        else
            this.name = "";

        if (parts1.length >= 2)
            this.meta = Integer.parseInt(parts1[1]);
        else
            this.meta = OreDictionary.WILDCARD_VALUE;
    }

    public GameRegistry.UniqueIdentifier getUniqueIdentifier () {
        if (cachedUID == null)
            cachedUID = new GameRegistry.UniqueIdentifier(modId + ":" + name);
        return cachedUID;
    }

    public Block getBlock () {
        return GameRegistry.findBlock(modId, name);
    }

    public Item getItem () {
        return GameRegistry.findItem(modId, name);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;

        final UniqueMetaIdentifier other = (UniqueMetaIdentifier) obj;

        return Objects.equal(modId, other.modId)
            && Objects.equal(name, other.name)
            && meta == other.meta;
    }

    @Override
    public int hashCode () {
        return Objects.hashCode(modId, name) ^ (meta * 37);
    }

    @Override
    public String toString () {
        return String.format("%s:%s;%d", modId, name, meta);
    }

    public static UniqueMetaIdentifier createFor (ItemStack itemStack) {
        if (itemStack.getItem() == null)
            return null;

        String name = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        return new UniqueMetaIdentifier(name, itemStack.getItemDamage());
    }

    public static UniqueMetaIdentifier createFor (Block block, int meta) {
        if (block == null)
            return null;

        String name = GameData.getBlockRegistry().getNameForObject(block);
        return new UniqueMetaIdentifier(name, meta);
    }

    public static UniqueMetaIdentifier createFor (Block block) {
        if (block == null)
            return null;

        String name = GameData.getBlockRegistry().getNameForObject(block);
        return new UniqueMetaIdentifier(name);
    }
}
