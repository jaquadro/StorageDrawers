package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Session
{
    private final String username;
    private final String playerID;
    private final String token;
    private final Session.Type sessionType;
    /** Forge: Cache of the local session's GameProfile properties. */
    private com.mojang.authlib.properties.PropertyMap properties;

    public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn)
    {
        if (usernameIn == null || usernameIn.isEmpty())
        {
            usernameIn = "MissingName";
            playerIDIn = tokenIn = "NotValid";
            org.apache.logging.log4j.Logger logger = net.minecraftforge.fml.common.FMLLog.getLogger();
            logger.log(org.apache.logging.log4j.Level.WARN, "=========================================================");
            logger.log(org.apache.logging.log4j.Level.WARN, "WARNING!! the username was not set for this session, typically");
            logger.log(org.apache.logging.log4j.Level.WARN, "this means you installed Forge incorrectly. We have set your");
            logger.log(org.apache.logging.log4j.Level.WARN, "name to \"MissingName\" and your session to nothing. Please");
            logger.log(org.apache.logging.log4j.Level.WARN, "check your installation and post a console log from the launcher");
            logger.log(org.apache.logging.log4j.Level.WARN, "when asking for help!");
            logger.log(org.apache.logging.log4j.Level.WARN, "=========================================================");
        }

        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Session.Type.setSessionType(sessionTypeIn);
    }

    public String getSessionID()
    {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID()
    {
        return this.playerID;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getToken()
    {
        return this.token;
    }

    public GameProfile getProfile()
    {
        try
        {
            UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            GameProfile ret = new GameProfile(uuid, this.getUsername());    //Forge: Adds cached GameProfile properties to returned GameProfile.
            if (properties != null) ret.getProperties().putAll(properties); // Helps to cut down on calls to the session service,
            return ret;                                                     // which helps to fix MC-52974.
        }
        catch (IllegalArgumentException var2)
        {
            return new GameProfile(net.minecraft.entity.player.EntityPlayer.getUUID(new GameProfile((UUID)null, this.getUsername())), this.getUsername());
        }
    }

    /* ======================================== FORGE START ===================================== */
    //For internal use only. Modders should never need to use this.
    public void setProperties(com.mojang.authlib.properties.PropertyMap properties)
    {
        if(this.properties == null) this.properties = properties;
    }

    public boolean hasCachedProperties()
    {
        return properties != null;
    }
    /* ========================================= FORGE END ====================================== */

    @SideOnly(Side.CLIENT)
    public static enum Type
    {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, Session.Type> SESSION_TYPES = Maps.<String, Session.Type>newHashMap();
        private final String sessionType;

        private Type(String sessionTypeIn)
        {
            this.sessionType = sessionTypeIn;
        }

        @Nullable
        public static Session.Type setSessionType(String sessionTypeIn)
        {
            return (Session.Type)SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }

        static
        {
            for (Session.Type session$type : values())
            {
                SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }
    }
}