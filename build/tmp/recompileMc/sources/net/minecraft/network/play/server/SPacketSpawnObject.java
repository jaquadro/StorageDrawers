package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketSpawnObject implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private int pitch;
    private int yaw;
    private int type;
    private int data;

    public SPacketSpawnObject()
    {
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn)
    {
        this(entityIn, typeIn, 0);
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn, int dataIn)
    {
        this.entityId = entityIn.getEntityId();
        this.uniqueId = entityIn.getUniqueID();
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.pitch = MathHelper.floor(entityIn.rotationPitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor(entityIn.rotationYaw * 256.0F / 360.0F);
        this.type = typeIn;
        this.data = dataIn;
        double d0 = 3.9D;
        this.speedX = (int)(MathHelper.clamp(entityIn.motionX, -3.9D, 3.9D) * 8000.0D);
        this.speedY = (int)(MathHelper.clamp(entityIn.motionY, -3.9D, 3.9D) * 8000.0D);
        this.speedZ = (int)(MathHelper.clamp(entityIn.motionZ, -3.9D, 3.9D) * 8000.0D);
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn, int dataIn, BlockPos pos)
    {
        this(entityIn, typeIn, dataIn);
        this.x = (double)pos.getX();
        this.y = (double)pos.getY();
        this.z = (double)pos.getZ();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.type = buf.readByte();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.data = buf.readInt();
        this.speedX = buf.readShort();
        this.speedY = buf.readShort();
        this.speedZ = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityId);
        buf.writeUniqueId(this.uniqueId);
        buf.writeByte(this.type);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.pitch);
        buf.writeByte(this.yaw);
        buf.writeInt(this.data);
        buf.writeShort(this.speedX);
        buf.writeShort(this.speedY);
        buf.writeShort(this.speedZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSpawnObject(this);
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID()
    {
        return this.entityId;
    }

    public void setSpeedX(int newSpeedX)
    {
        this.speedX = newSpeedX;
    }

    @SideOnly(Side.CLIENT)
    public UUID getUniqueId()
    {
        return this.uniqueId;
    }

    public void setSpeedY(int newSpeedY)
    {
        this.speedY = newSpeedY;
    }

    @SideOnly(Side.CLIENT)
    public double getX()
    {
        return this.x;
    }

    public void setSpeedZ(int newSpeedZ)
    {
        this.speedZ = newSpeedZ;
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
    public int getSpeedX()
    {
        return this.speedX;
    }

    @SideOnly(Side.CLIENT)
    public int getSpeedY()
    {
        return this.speedY;
    }

    @SideOnly(Side.CLIENT)
    public int getSpeedZ()
    {
        return this.speedZ;
    }

    @SideOnly(Side.CLIENT)
    public int getPitch()
    {
        return this.pitch;
    }

    @SideOnly(Side.CLIENT)
    public int getYaw()
    {
        return this.yaw;
    }

    @SideOnly(Side.CLIENT)
    public int getType()
    {
        return this.type;
    }

    @SideOnly(Side.CLIENT)
    public int getData()
    {
        return this.data;
    }

    @SideOnly(Side.CLIENT)
    public void setData(int dataIn)
    {
        this.data = dataIn;
    }
}