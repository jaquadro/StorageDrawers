package com.jaquadro.minecraft.storagedrawers.integration.notenoughitems;

/*import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

@Optional.Interface(iface = "codechicken.nei.api.IConfigureNEI", modid = "NotEnoughItems")
public class NEIStorageDrawersConfig implements IConfigureNEI
{
    private static Set<ItemStack> pendingHide = new HashSet<ItemStack>();
    private static boolean loaded;

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public void loadConfig () {
        for (ItemStack stack : pendingHide)
            API.hideItem(stack);

        pendingHide.clear();
        loaded = true;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public String getName () {
        return StorageDrawers.MOD_NAME;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public String getVersion () {
        return StorageDrawers.MOD_VERSION;
    }

    private static void hideItem (ItemStack stack) {
        API.hideItem(stack);
    }

    public static void hideBlock (String blockID) {
        Block block = GameData.getBlockRegistry().getObject(blockID);
        if (block != null) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(block), 1, OreDictionary.WILDCARD_VALUE);
            if (loaded)
                hideItem(stack);
            else
                pendingHide.add(stack);
        }
    }
}*/
