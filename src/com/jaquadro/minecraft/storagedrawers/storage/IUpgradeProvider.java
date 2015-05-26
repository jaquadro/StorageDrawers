package com.jaquadro.minecraft.storagedrawers.storage;

public interface IUpgradeProvider
{
    boolean isLocked ();

    boolean isVoid ();

    boolean isSorting ();

    boolean isShrouded ();
}
