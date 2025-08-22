package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradeMagnet extends ItemUpgrade
{
    private static final int magnetGroupId;
    static {
        magnetGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeMagnet type;

    public ItemUpgradeMagnet (EnumUpgradeMagnet type, Properties properties) {
        this(type, properties, magnetGroupId);
    }

    protected ItemUpgradeMagnet (EnumUpgradeMagnet type, Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(true);
        this.type = type;
    }

    @Override
    @NotNull
    public Component getDescription() {
        if (!isEnabled())
            return super.getDescription();

        return Component.translatable("item.storagedrawers.magnet_upgrade.desc");
    }

    @Override
    public boolean isEnabled () {
        if (!ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.enableUpgrade.get())
            return false;

        if (type == EnumUpgradeMagnet.LEVEL2)
            return ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.enableLevel2.get();
        else if (type == EnumUpgradeMagnet.LEVEL3)
            return ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.enableLevel3.get();

        return true;
    }

    public int getHorzRange () {
        return getIndexedRange(0);
    }

    public int getUpRange () {
        return getIndexedRange(1);
    }

    public int getDownRange () {
        return getIndexedRange(2);
    }

    private int getIndexedRange (int index) {
        return switch (type) {
            case LEVEL1 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.level1Range.get().get(index);
            case LEVEL2 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.level2Range.get().get(index);
            case LEVEL3 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.level3Range.get().get(index);
        };
    }
}