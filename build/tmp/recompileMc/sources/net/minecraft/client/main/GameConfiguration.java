package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourceIndexFolder;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GameConfiguration
{
    public final GameConfiguration.UserInformation userInfo;
    public final GameConfiguration.DisplayInformation displayInfo;
    public final GameConfiguration.FolderInformation folderInfo;
    public final GameConfiguration.GameInformation gameInfo;
    public final GameConfiguration.ServerInformation serverInfo;

    public GameConfiguration(GameConfiguration.UserInformation userInfoIn, GameConfiguration.DisplayInformation displayInfoIn, GameConfiguration.FolderInformation folderInfoIn, GameConfiguration.GameInformation gameInfoIn, GameConfiguration.ServerInformation serverInfoIn)
    {
        this.userInfo = userInfoIn;
        this.displayInfo = displayInfoIn;
        this.folderInfo = folderInfoIn;
        this.gameInfo = gameInfoIn;
        this.serverInfo = serverInfoIn;
    }

    @SideOnly(Side.CLIENT)
    public static class DisplayInformation
        {
            public final int width;
            public final int height;
            public final boolean fullscreen;
            public final boolean checkGlErrors;

            public DisplayInformation(int widthIn, int heightIn, boolean fullscreenIn, boolean checkGlErrorsIn)
            {
                this.width = widthIn;
                this.height = heightIn;
                this.fullscreen = fullscreenIn;
                this.checkGlErrors = checkGlErrorsIn;
            }
        }

    @SideOnly(Side.CLIENT)
    public static class FolderInformation
        {
            public final File mcDataDir;
            public final File resourcePacksDir;
            public final File assetsDir;
            public final String assetIndex;

            public FolderInformation(File mcDataDirIn, File resourcePacksDirIn, File assetsDirIn, @Nullable String assetIndexIn)
            {
                this.mcDataDir = mcDataDirIn;
                this.resourcePacksDir = resourcePacksDirIn;
                this.assetsDir = assetsDirIn;
                this.assetIndex = assetIndexIn;
            }

            public ResourceIndex getAssetsIndex()
            {
                return (ResourceIndex)(this.assetIndex == null ? new ResourceIndexFolder(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
            }
        }

    @SideOnly(Side.CLIENT)
    public static class GameInformation
        {
            public final boolean isDemo;
            public final String version;
            /** Defaults to "release" */
            public final String versionType;

            public GameInformation(boolean demo, String versionIn, String versionTypeIn)
            {
                this.isDemo = demo;
                this.version = versionIn;
                this.versionType = versionTypeIn;
            }
        }

    @SideOnly(Side.CLIENT)
    public static class ServerInformation
        {
            public final String serverName;
            public final int serverPort;

            public ServerInformation(String serverNameIn, int serverPortIn)
            {
                this.serverName = serverNameIn;
                this.serverPort = serverPortIn;
            }
        }

    @SideOnly(Side.CLIENT)
    public static class UserInformation
        {
            public final Session session;
            public final PropertyMap userProperties;
            public final PropertyMap profileProperties;
            public final Proxy proxy;

            public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Proxy proxyIn)
            {
                this.session = sessionIn;
                this.userProperties = userPropertiesIn;
                this.profileProperties = profilePropertiesIn;
                this.proxy = proxyIn;
            }
        }
}