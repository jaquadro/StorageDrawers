package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketSpawnPlayer implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private EntityDataManager watcher;
    private List < EntityDataManager.DataEntry<? >> dataManagerEntries;

    public SPacketSpawnPlayer()
    {
    }

    public SPacketSpawnPlayer(EntityPlayer player)
    {
        this.entityId = player.getEntityId();
        this.uniqueId = player.getGameProfile().getId();
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
        this.yaw = (byte)((int)(player.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(player.rotationPitch * 256.0F / 360.0F));
        this.watcher = player.getDataManager();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.dataManagerEntries = EntityDataManager.readEntries(buf);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityId);
        buf.writeUniqueId(this.uniqueId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        this.watcher.writeEntries(buf);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnPlayer(this);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public List < EntityDataManager.DataEntry<? >> getDataManagerEntries()
    {
        if (this.dataManagerEntries == null)
        {
            this.dataManagerEntries = this.watcher.getAll();
        }

        return this.dataManagerEntries;
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID()
    {
        return this.entityId;
    }

    @SideOnly(Side.CLIENT)
    public UUID getUniqueId()
    {
        return this.uniqueId;
    }

    @SideOnly(Side.CLIENT)
    public double getX()
    {
        return this.x;
    }

    @SideOnly(Side.CLIENT)
    public double getY()
    {
        return this.y;
    }

    @SideOnly(Side.CLIENT)
    public double getZ()
    {
        return this.z;
    }

    @SideOnly(Side.CLIENT)
    public byte getYaw()
    {
        return this.yaw;
    }

    @SideOnly(Side.CLIENT)
    public byte getPitch()
    {
        return this.pitch;
    }
}