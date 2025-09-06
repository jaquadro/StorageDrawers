package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ConnectionMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class SidedConnectData extends BlockEntityDataShim
{
    private IDrawerAttributesModifiable attrs;

    public SidedConnectData () { }

    public void setDrawerAttributes (IDrawerAttributesModifiable attrs) {
        this.attrs = attrs;
    }

    public void setConnectionMode (Direction dir, ConnectionMode mode) {
        if (attrs != null)
            attrs.setSidedConnectionMode(dir, mode);
    }

    public void cycleConnectionMode (Direction dir) {
        if (attrs == null)
            return;

        ConnectionMode mode = attrs.getSidedConnectionMode(dir);
        setConnectionMode(dir, mode.getNextMode());
    }

    @Override
    public void read (CompoundTag tag) {
        if (attrs == null)
            return;

        if (tag.contains("Conn")) {
            CompoundTag conn = tag.getCompound("Conn");
            for (Direction direction : Direction.values()) {
                if (direction == Direction.NORTH)
                    continue;

                if (conn.contains(direction.getSerializedName()))
                    attrs.setSidedConnectionMode(direction, ConnectionMode.byName(conn.getString(direction.getSerializedName())));
                else
                    attrs.setSidedConnectionMode(direction, ConnectionMode.DEFAULT);
            }
        } else {
            for (Direction direction : Direction.values())
                attrs.setSidedConnectionMode(direction, ConnectionMode.DEFAULT);
        }
    }

    @Override
    public CompoundTag write (CompoundTag tag) {
        if (attrs == null)
            return tag;

        CompoundTag conn = new CompoundTag();
        for (Direction direction : Direction.values()) {
            if (direction == Direction.NORTH)
                continue;

            ConnectionMode mode = attrs.getSidedConnectionMode(direction);
            if (mode == null || mode == ConnectionMode.DEFAULT)
                continue;

            conn.putString(direction.getSerializedName(), mode.getSerializedName());
        }

        if (conn.isEmpty())
            tag.remove("Conn");
        else
            tag.put("Conn", conn);

        return tag;
    }
}
