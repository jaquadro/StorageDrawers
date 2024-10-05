package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradeStorage extends ItemUpgrade
{
    private static final int storageGroupId;
    static {
        storageGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeStorage level;

    public ItemUpgradeStorage (EnumUpgradeStorage level, Properties properties) {
        this(level, properties, storageGroupId);
    }

    protected ItemUpgradeStorage (EnumUpgradeStorage level, Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(true);
        this.level = level;
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.UPGRADES.enableStorageUpgrade.get();
    }

    @Override
    @NotNull
    public Component getDescription() {
        if (!isEnabled())
            return super.getDescription();

        int mult = ModCommonConfig.INSTANCE.UPGRADES.getLevelMult(level.getLevel());
        return Component.translatable("item.storagedrawers.storage_upgrade.desc", mult);
    }
}
