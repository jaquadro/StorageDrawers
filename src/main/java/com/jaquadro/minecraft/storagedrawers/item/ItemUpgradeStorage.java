package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradeStorage extends ItemUpgrade
{
    private static final int storageGroupId;
    static {
        storageGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeStorage level;

    public ItemUpgradeStorage (EnumUpgradeStorage level, Item.Properties properties) {
        this(level, properties, storageGroupId);
    }

    protected ItemUpgradeStorage (EnumUpgradeStorage level, Item.Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(true);
        this.level = level;
    }

    @Override
    public boolean isEnabled () {
        return CommonConfig.UPGRADES.enableStorageUpgrade.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @NotNull
    public Component getDescription() {
        if (!isEnabled())
            return super.getDescription();

        int mult = CommonConfig.UPGRADES.getLevelMult(level.getLevel());
        return Component.translatable("item.storagedrawers.storage_upgrade.desc", mult);
    }
}
