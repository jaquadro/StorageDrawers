package net.minecraft.inventory;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnimalChest extends InventoryBasic
{
    public AnimalChest(String inventoryName, int slotCount)
    {
        super(inventoryName, false, slotCount);
    }

    @SideOnly(Side.CLIENT)
    public AnimalChest(ITextComponent invTitle, int slotCount)
    {
        super(invTitle, slotCount);
    }
}