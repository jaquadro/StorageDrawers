package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
    public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
    public static final File OLD_OPS_FILE = new File("ops.txt");
    public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

    private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback)
    {
        String[] astring = (String[])Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>()
        {
            public boolean apply(@Nullable String p_apply_1_)
            {
                return !StringUtils.isNullOrEmpty(p_apply_1_);
            }
        }), String.class);

        if (server.isServerInOnlineMode())
        {
            server.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, callback);
        }
        else
        {
            for (String s : astring)
            {
                UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);
                callback.onProfileLookupSucceeded(gameprofile);
            }
        }
    }

    public static String convertMobOwnerIfNeeded(final MinecraftServer server, String username)
    {
        if (!StringUtils.isNullOrEmpty(username) && username.length() <= 16)
        {
            GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(username);

            if (gameprofile != null && gameprofile.getId() != null)
            {
                return gameprofile.getId().toString();
            }
            else if (!server.isSinglePlayer() && server.isServerInOnlineMode())
            {
                final List<GameProfile> list = Lists.<GameProfile>newArrayList();
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        list.add(p_onProfileLookupSucceeded_1_);
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", new Object[] {p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_});
                    }
                };
                lookupNames(server, Lists.newArrayList(new String[] {username}), profilelookupcallback);
                return !list.isEmpty() && ((GameProfile)list.get(0)).getId() != null ? ((GameProfile)list.get(0)).getId().toString() : "";
            }
            else
            {
                return EntityPlayer.getUUID(new GameProfile((UUID)null, username)).toString();
            }
        }
        else
        {
            return username;
        }
    }

    @SideOnly(Side.SERVER)
    static List<String> readFile(File inFile, Map<String, String[]> read) throws IOException
    {
        List<String> list = Files.readLines(inFile, Charsets.UTF_8);

        for (String s : list)
        {
            s = s.trim();

            if (!s.startsWith("#") && s.length() >= 1)
            {
                String[] astring = s.split("\\|");
                read.put(astring[0].toLowerCase(Locale.ROOT), astring);
            }
        }

        return list;
    }

    @SideOnly(Side.SERVER)
    public static boolean convertUserBanlist(final MinecraftServer server) throws IOException
    {
        final UserListBans userlistbans = new UserListBans(PlayerList.FILE_PLAYERBANS);

        if (OLD_PLAYERBAN_FILE.exists() && OLD_PLAYERBAN_FILE.isFile())
        {
            if (userlistbans.getSaveFile().exists())
            {
                try
                {
                    userlistbans.readSavedFile();
                }
                catch (FileNotFoundException filenotfoundexception)
                {
                    LOGGER.warn("Could not load existing file {}", new Object[] {userlistbans.getSaveFile().getName(), filenotfoundexception});
                }
            }

            try
            {
                final Map<String, String[]> map = Maps.<String, String[]>newHashMap();
                readFile(OLD_PLAYERBAN_FILE, map);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        String[] astring = (String[])map.get(p_onProfileLookupSucceeded_1_.getName().toLowerCase(Locale.ROOT));

                        if (astring == null)
                        {
                            PreYggdrasilConverter.LOGGER.warn("Could not convert user banlist entry for {}", new Object[] {p_onProfileLookupSucceeded_1_.getName()});
                            throw new PreYggdrasilConverter.ConversionError("Profile not in the conversionlist");
                        }
                        else
                        {
                            Date date = astring.length > 1 ? PreYggdrasilConverter.parseDate(astring[1], (Date)null) : null;
                            String s = astring.length > 2 ? astring[2] : null;
                            Date date1 = astring.length > 3 ? PreYggdrasilConverter.parseDate(astring[3], (Date)null) : null;
                            String s1 = astring.length > 4 ? astring[4] : null;
                            userlistbans.addEntry(new UserListBansEntry(p_onProfileLookupSucceeded_1_, date, s, date1, s1));
                        }
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user banlist entry for {}", new Object[] {p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_});

                        if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException))
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                        }
                    }
                };
                lookupNames(server, map.keySet(), profilelookupcallback);
                userlistbans.writeChanges();
                backupConverted(OLD_PLAYERBAN_FILE);
                return true;
            }
            catch (IOException ioexception)
            {
                LOGGER.warn((String)"Could not read old user banlist to convert it!", (Throwable)ioexception);
                return false;
            }
            catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror)
            {
                LOGGER.error((String)"Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean convertIpBanlist(MinecraftServer server) throws IOException
    {
        UserListIPBans userlistipbans = new UserListIPBans(PlayerList.FILE_IPBANS);

        if (OLD_IPBAN_FILE.exists() && OLD_IPBAN_FILE.isFile())
        {
            if (userlistipbans.getSaveFile().exists())
            {
                try
                {
                    userlistipbans.readSavedFile();
                }
                catch (FileNotFoundException filenotfoundexception)
                {
                    LOGGER.warn("Could not load existing file {}", new Object[] {userlistipbans.getSaveFile().getName(), filenotfoundexception});
                }
            }

            try
            {
                Map<String, String[]> map = Maps.<String, String[]>newHashMap();
                readFile(OLD_IPBAN_FILE, map);

                for (String s : map.keySet())
                {
                    String[] astring = (String[])map.get(s);
                    Date date = astring.length > 1 ? parseDate(astring[1], (Date)null) : null;
                    String s1 = astring.length > 2 ? astring[2] : null;
                    Date date1 = astring.length > 3 ? parseDate(astring[3], (Date)null) : null;
                    String s2 = astring.length > 4 ? astring[4] : null;
                    userlistipbans.addEntry(new UserListIPBansEntry(s, date, s1, date1, s2));
                }

                userlistipbans.writeChanges();
                backupConverted(OLD_IPBAN_FILE);
                return true;
            }
            catch (IOException ioexception)
            {
                LOGGER.warn((String)"Could not parse old ip banlist to convert it!", (Throwable)ioexception);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean convertOplist(final MinecraftServer server) throws IOException
    {
        final UserListOps userlistops = new UserListOps(PlayerList.FILE_OPS);

        if (OLD_OPS_FILE.exists() && OLD_OPS_FILE.isFile())
        {
            if (userlistops.getSaveFile().exists())
            {
                try
                {
                    userlistops.readSavedFile();
                }
                catch (FileNotFoundException filenotfoundexception)
                {
                    LOGGER.warn("Could not load existing file {}", new Object[] {userlistops.getSaveFile().getName(), filenotfoundexception});
                }
            }

            try
            {
                List<String> list = Files.readLines(OLD_OPS_FILE, Charsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        userlistops.addEntry(new UserListOpsEntry(p_onProfileLookupSucceeded_1_, server.getOpPermissionLevel(), false));
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup oplist entry for {}", new Object[] {p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_});

                        if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException))
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                        }
                    }
                };
                lookupNames(server, list, profilelookupcallback);
                userlistops.writeChanges();
                backupConverted(OLD_OPS_FILE);
                return true;
            }
            catch (IOException ioexception)
            {
                LOGGER.warn((String)"Could not read old oplist to convert it!", (Throwable)ioexception);
                return false;
            }
            catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror)
            {
                LOGGER.error((String)"Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean convertWhitelist(final MinecraftServer server) throws IOException
    {
        final UserListWhitelist userlistwhitelist = new UserListWhitelist(PlayerList.FILE_WHITELIST);

        if (OLD_WHITELIST_FILE.exists() && OLD_WHITELIST_FILE.isFile())
        {
            if (userlistwhitelist.getSaveFile().exists())
            {
                try
                {
                    userlistwhitelist.readSavedFile();
                }
                catch (FileNotFoundException filenotfoundexception)
                {
                    LOGGER.warn("Could not load existing file {}", new Object[] {userlistwhitelist.getSaveFile().getName(), filenotfoundexception});
                }
            }

            try
            {
                List<String> list = Files.readLines(OLD_WHITELIST_FILE, Charsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        userlistwhitelist.addEntry(new UserListWhitelistEntry(p_onProfileLookupSucceeded_1_));
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", new Object[] {p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_});

                        if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException))
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                        }
                    }
                };
                lookupNames(server, list, profilelookupcallback);
                userlistwhitelist.writeChanges();
                backupConverted(OLD_WHITELIST_FILE);
                return true;
            }
            catch (IOException ioexception)
            {
                LOGGER.warn((String)"Could not read old whitelist to convert it!", (Throwable)ioexception);
                return false;
            }
            catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror)
            {
                LOGGER.error((String)"Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean convertSaveFiles(final DedicatedServer server, PropertyManager p_152723_1_)
    {
        final File file1 = getPlayersDirectory(p_152723_1_);
        final File file2 = new File(file1.getParentFile(), "playerdata");
        final File file3 = new File(file1.getParentFile(), "unknownplayers");

        if (file1.exists() && file1.isDirectory())
        {
            File[] afile = file1.listFiles();
            List<String> list = Lists.<String>newArrayList();

            for (File file4 : afile)
            {
                String s = file4.getName();

                if (s.toLowerCase(Locale.ROOT).endsWith(".dat"))
                {
                    String s1 = s.substring(0, s.length() - ".dat".length());

                    if (!s1.isEmpty())
                    {
                        list.add(s1);
                    }
                }
            }

            try
            {
                final String[] astring = (String[])list.toArray(new String[list.size()]);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        UUID uuid = p_onProfileLookupSucceeded_1_.getId();

                        if (uuid == null)
                        {
                            throw new PreYggdrasilConverter.ConversionError("Missing UUID for user profile " + p_onProfileLookupSucceeded_1_.getName());
                        }
                        else
                        {
                            this.renamePlayerFile(file2, this.getFileNameForProfile(p_onProfileLookupSucceeded_1_), uuid.toString());
                        }
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user uuid for {}", new Object[] {p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_});

                        if (p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)
                        {
                            String s2 = this.getFileNameForProfile(p_onProfileLookupFailed_1_);
                            this.renamePlayerFile(file3, s2, s2);
                        }
                        else
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                        }
                    }
                    private void renamePlayerFile(File p_152743_1_, String p_152743_2_, String p_152743_3_)
                    {
                        File file5 = new File(file1, p_152743_2_ + ".dat");
                        File file6 = new File(p_152743_1_, p_152743_3_ + ".dat");
                        PreYggdrasilConverter.mkdir(p_152743_1_);

                        if (!file5.renameTo(file6))
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not convert file for " + p_152743_2_);
                        }
                    }
                    private String getFileNameForProfile(GameProfile p_152744_1_)
                    {
                        String s2 = null;

                        for (String s3 : astring)
                        {
                            if (s3 != null && s3.equalsIgnoreCase(p_152744_1_.getName()))
                            {
                                s2 = s3;
                                break;
                            }
                        }

                        if (s2 == null)
                        {
                            throw new PreYggdrasilConverter.ConversionError("Could not find the filename for " + p_152744_1_.getName() + " anymore");
                        }
                        else
                        {
                            return s2;
                        }
                    }
                };
                lookupNames(server, Lists.newArrayList(astring), profilelookupcallback);
                return true;
            }
            catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror)
            {
                LOGGER.error((String)"Conversion failed, please try again later", (Throwable)preyggdrasilconverter$conversionerror);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @SideOnly(Side.SERVER)
    private static void mkdir(File dir)
    {
        if (dir.exists())
        {
            if (!dir.isDirectory())
            {
                throw new PreYggdrasilConverter.ConversionError("Can\'t create directory " + dir.getName() + " in world save directory.");
            }
        }
        else if (!dir.mkdirs())
        {
            throw new PreYggdrasilConverter.ConversionError("Can\'t create directory " + dir.getName() + " in world save directory.");
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean tryConvert(PropertyManager properties)
    {
        boolean flag = hasUnconvertableFiles(properties);
        flag = flag && hasUnconvertablePlayerFiles(properties);
        return flag;
    }

    @SideOnly(Side.SERVER)
    private static boolean hasUnconvertableFiles(PropertyManager properties)
    {
        boolean flag = false;

        if (OLD_PLAYERBAN_FILE.exists() && OLD_PLAYERBAN_FILE.isFile())
        {
            flag = true;
        }

        boolean flag1 = false;

        if (OLD_IPBAN_FILE.exists() && OLD_IPBAN_FILE.isFile())
        {
            flag1 = true;
        }

        boolean flag2 = false;

        if (OLD_OPS_FILE.exists() && OLD_OPS_FILE.isFile())
        {
            flag2 = true;
        }

        boolean flag3 = false;

        if (OLD_WHITELIST_FILE.exists() && OLD_WHITELIST_FILE.isFile())
        {
            flag3 = true;
        }

        if (!flag && !flag1 && !flag2 && !flag3)
        {
            return true;
        }
        else
        {
            LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            LOGGER.warn("** please remove the following files and restart the server:");

            if (flag)
            {
                LOGGER.warn("* {}", new Object[] {OLD_PLAYERBAN_FILE.getName()});
            }

            if (flag1)
            {
                LOGGER.warn("* {}", new Object[] {OLD_IPBAN_FILE.getName()});
            }

            if (flag2)
            {
                LOGGER.warn("* {}", new Object[] {OLD_OPS_FILE.getName()});
            }

            if (flag3)
            {
                LOGGER.warn("* {}", new Object[] {OLD_WHITELIST_FILE.getName()});
            }

            return false;
        }
    }

    @SideOnly(Side.SERVER)
    private static boolean hasUnconvertablePlayerFiles(PropertyManager properties)
    {
        File file1 = getPlayersDirectory(properties);

        if (!file1.exists() || !file1.isDirectory() || file1.list().length <= 0 && file1.delete())
        {
            return true;
        }
        else
        {
            LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            LOGGER.warn("** please restart the server and if the problem persists, remove the directory \'{}\'", new Object[] {file1.getPath()});
            return false;
        }
    }

    @SideOnly(Side.SERVER)
    private static File getPlayersDirectory(PropertyManager properties)
    {
        String s = properties.getStringProperty("level-name", "world");
        File file1 = new File(s);
        return new File(file1, "players");
    }

    @SideOnly(Side.SERVER)
    private static void backupConverted(File convertedFile)
    {
        File file1 = new File(convertedFile.getName() + ".converted");
        convertedFile.renameTo(file1);
    }

    @SideOnly(Side.SERVER)
    private static Date parseDate(String input, Date defaultValue)
    {
        Date date;

        try
        {
            date = UserListEntryBan.DATE_FORMAT.parse(input);
        }
        catch (ParseException var4)
        {
            date = defaultValue;
        }

        return date;
    }

    @SideOnly(Side.SERVER)
    static class ConversionError extends RuntimeException
        {
            private ConversionError(String message, Throwable cause)
            {
                super(message, cause);
            }

            private ConversionError(String message)
            {
                super(message);
            }
        }
}