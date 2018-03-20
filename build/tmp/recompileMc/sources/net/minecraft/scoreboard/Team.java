package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Team
{
    /**
     * Same as ==
     */
    public boolean isSameTeam(@Nullable Team other)
    {
        return other == null ? false : this == other;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public abstract String getRegisteredName();

    public abstract String formatString(String input);

    @SideOnly(Side.CLIENT)
    public abstract boolean getSeeFriendlyInvisiblesEnabled();

    public abstract boolean getAllowFriendlyFire();

    @SideOnly(Side.CLIENT)
    public abstract Team.EnumVisible getNameTagVisibility();

    public abstract TextFormatting getChatFormat();

    public abstract Collection<String> getMembershipCollection();

    public abstract Team.EnumVisible getDeathMessageVisibility();

    public abstract Team.CollisionRule getCollisionRule();

    public static enum CollisionRule
    {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("pushOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, Team.CollisionRule> nameMap = Maps.<String, Team.CollisionRule>newHashMap();
        public final String name;
        public final int id;

        public static String[] getNames()
        {
            return (String[])nameMap.keySet().toArray(new String[nameMap.size()]);
        }

        public static Team.CollisionRule getByName(String nameIn)
        {
            return (Team.CollisionRule)nameMap.get(nameIn);
        }

        private CollisionRule(String nameIn, int idIn)
        {
            this.name = nameIn;
            this.id = idIn;
        }

        static
        {
            for (Team.CollisionRule team$collisionrule : values())
            {
                nameMap.put(team$collisionrule.name, team$collisionrule);
            }
        }
    }

    public static enum EnumVisible
    {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Team.EnumVisible> nameMap = Maps.<String, Team.EnumVisible>newHashMap();
        public final String internalName;
        public final int id;

        public static String[] getNames()
        {
            return (String[])nameMap.keySet().toArray(new String[nameMap.size()]);
        }

        public static Team.EnumVisible getByName(String nameIn)
        {
            return (Team.EnumVisible)nameMap.get(nameIn);
        }

        private EnumVisible(String nameIn, int idIn)
        {
            this.internalName = nameIn;
            this.id = idIn;
        }

        static
        {
            for (Team.EnumVisible team$enumvisible : values())
            {
                nameMap.put(team$enumvisible.internalName, team$enumvisible);
            }
        }
    }
}