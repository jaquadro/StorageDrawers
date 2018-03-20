package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsManagerServer extends StatisticsManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final File statsFile;
    private final Set<StatBase> dirty = Sets.<StatBase>newHashSet();
    private int lastStatRequest = -300;
    private boolean hasUnsentAchievement;

    public StatisticsManagerServer(MinecraftServer serverIn, File statsFileIn)
    {
        this.mcServer = serverIn;
        this.statsFile = statsFileIn;
    }

    public void readStatFile()
    {
        if (this.statsFile.isFile())
        {
            try
            {
                this.statsData.clear();
                this.statsData.putAll(this.parseJson(FileUtils.readFileToString(this.statsFile)));
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn\'t read statistics file {}", new Object[] {this.statsFile, ioexception});
            }
            catch (JsonParseException jsonparseexception)
            {
                LOGGER.error("Couldn\'t parse statistics file {}", new Object[] {this.statsFile, jsonparseexception});
            }
        }
    }

    public void saveStatFile()
    {
        try
        {
            FileUtils.writeStringToFile(this.statsFile, dumpJson(this.statsData));
        }
        catch (IOException ioexception)
        {
            LOGGER.error((String)"Couldn\'t save stats", (Throwable)ioexception);
        }
    }

    /**
     * Triggers the logging of an achievement and attempts to announce to server
     */
    public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_)
    {
        int i = statIn.isAchievement() ? this.readStat(statIn) : 0;
        super.unlockAchievement(playerIn, statIn, p_150873_3_);
        this.dirty.add(statIn);

        if (statIn.isAchievement() && i == 0 && p_150873_3_ > 0)
        {
            this.hasUnsentAchievement = true;

            if (this.mcServer.isAnnouncingPlayerAchievements())
            {
                this.mcServer.getPlayerList().sendChatMsg(new TextComponentTranslation("chat.type.achievement", new Object[] {playerIn.getDisplayName(), statIn.createChatComponent()}));
            }
        }

        if (statIn.isAchievement() && i > 0 && p_150873_3_ == 0)
        {
            this.hasUnsentAchievement = true;

            if (this.mcServer.isAnnouncingPlayerAchievements())
            {
                this.mcServer.getPlayerList().sendChatMsg(new TextComponentTranslation("chat.type.achievement.taken", new Object[] {playerIn.getDisplayName(), statIn.createChatComponent()}));
            }
        }
    }

    public Set<StatBase> getDirty()
    {
        Set<StatBase> set = Sets.newHashSet(this.dirty);
        this.dirty.clear();
        this.hasUnsentAchievement = false;
        return set;
    }

    public Map<StatBase, TupleIntJsonSerializable> parseJson(String p_150881_1_)
    {
        JsonElement jsonelement = (new JsonParser()).parse(p_150881_1_);

        if (!jsonelement.isJsonObject())
        {
            return Maps.<StatBase, TupleIntJsonSerializable>newHashMap();
        }
        else
        {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            Map<StatBase, TupleIntJsonSerializable> map = Maps.<StatBase, TupleIntJsonSerializable>newHashMap();

            for (Entry<String, JsonElement> entry : jsonobject.entrySet())
            {
                StatBase statbase = StatList.getOneShotStat((String)entry.getKey());

                if (statbase != null)
                {
                    TupleIntJsonSerializable tupleintjsonserializable = new TupleIntJsonSerializable();

                    if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber())
                    {
                        tupleintjsonserializable.setIntegerValue(((JsonElement)entry.getValue()).getAsInt());
                    }
                    else if (((JsonElement)entry.getValue()).isJsonObject())
                    {
                        JsonObject jsonobject1 = ((JsonElement)entry.getValue()).getAsJsonObject();

                        if (jsonobject1.has("value") && jsonobject1.get("value").isJsonPrimitive() && jsonobject1.get("value").getAsJsonPrimitive().isNumber())
                        {
                            tupleintjsonserializable.setIntegerValue(jsonobject1.getAsJsonPrimitive("value").getAsInt());
                        }

                        if (jsonobject1.has("progress") && statbase.getSerializableClazz() != null)
                        {
                            try
                            {
                                Constructor <? extends IJsonSerializable > constructor = statbase.getSerializableClazz().getConstructor(new Class[0]);
                                IJsonSerializable ijsonserializable = (IJsonSerializable)constructor.newInstance(new Object[0]);
                                ijsonserializable.fromJson(jsonobject1.get("progress"));
                                tupleintjsonserializable.setJsonSerializableValue(ijsonserializable);
                            }
                            catch (Throwable throwable)
                            {
                                LOGGER.warn("Invalid statistic progress in {}", new Object[] {this.statsFile, throwable});
                            }
                        }
                    }

                    map.put(statbase, tupleintjsonserializable);
                }
                else
                {
                    LOGGER.warn("Invalid statistic in {}: Don\'t know what {} is", new Object[] {this.statsFile, entry.getKey()});
                }
            }

            return map;
        }
    }

    public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> p_150880_0_)
    {
        JsonObject jsonobject = new JsonObject();

        for (Entry<StatBase, TupleIntJsonSerializable> entry : p_150880_0_.entrySet())
        {
            if (((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue() != null)
            {
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.addProperty("value", (Number)Integer.valueOf(((TupleIntJsonSerializable)entry.getValue()).getIntegerValue()));

                try
                {
                    jsonobject1.add("progress", ((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue().getSerializableElement());
                }
                catch (Throwable throwable)
                {
                    LOGGER.warn("Couldn\'t save statistic {}: error serializing progress", new Object[] {((StatBase)entry.getKey()).getStatName(), throwable});
                }

                jsonobject.add(((StatBase)entry.getKey()).statId, jsonobject1);
            }
            else
            {
                jsonobject.addProperty(((StatBase)entry.getKey()).statId, (Number)Integer.valueOf(((TupleIntJsonSerializable)entry.getValue()).getIntegerValue()));
            }
        }

        return jsonobject.toString();
    }

    public void markAllDirty()
    {
        for (StatBase statbase : this.statsData.keySet())
        {
            this.dirty.add(statbase);
        }
    }

    public void sendStats(EntityPlayerMP player)
    {
        int i = this.mcServer.getTickCounter();
        Map<StatBase, Integer> map = Maps.<StatBase, Integer>newHashMap();

        if (this.hasUnsentAchievement || i - this.lastStatRequest > 300)
        {
            this.lastStatRequest = i;

            for (StatBase statbase : this.getDirty())
            {
                map.put(statbase, Integer.valueOf(this.readStat(statbase)));
            }
        }

        player.connection.sendPacket(new SPacketStatistics(map));
    }

    public void sendAchievements(EntityPlayerMP player)
    {
        Map<StatBase, Integer> map = Maps.<StatBase, Integer>newHashMap();

        for (Achievement achievement : AchievementList.ACHIEVEMENTS)
        {
            if (this.hasAchievementUnlocked(achievement))
            {
                map.put(achievement, Integer.valueOf(this.readStat(achievement)));
                this.dirty.remove(achievement);
            }
        }

        player.connection.sendPacket(new SPacketStatistics(map));
    }

    public boolean hasUnsentAchievement()
    {
        return this.hasUnsentAchievement;
    }
}