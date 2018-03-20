package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketSpawnExperienceOrb implements Packet<INetHandlerPlayClient>
{
    private int entityID;
    private double posX;
    private double posY;
    private double posZ;
    private int xpValue;

    public SPacketSpawnExperienceOrb()
    {
    }

    public SPacketSpawnExperienceOrb(EntityXPOrb orb)
    {
        this.entityID = orb.getEntityId();
        this.posX = orb.posX;
        this.posY = orb.posY;
        this.posZ = orb.posZ;
        this.xpValue = orb.getXpValue();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityID = buf.readVarInt();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.xpValue = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityID);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeShort(this.xpValue);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnExperienceOrb(this);
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID()
    {
        return this.entityID;
    }

    @SideOnly(Side.CLIENT)
    public double getX()
    {
        return this.posX;
    }

    @SideOnly(Side.CLIENT)
    public double getY()
    {
        return this.posY;
    }

    @SideOnly(Side.CLIENT)
    public double getZ()
    {
        return this.posZ;
    }

    @SideOnly(Side.CLIENT)
    public int getXPValue()
    {
        return this.xpValue;
    }
}