package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern INT_ARRAY_MATCHER = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

    public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException
    {
        jsonString = jsonString.trim();

        if (!jsonString.startsWith("{"))
        {
            throw new NBTException("Invalid tag encountered, expected \'{\' as first char.");
        }
        else if (topTagsCount(jsonString) != 1)
        {
            throw new NBTException("Encountered multiple top tags, only one expected");
        }
        else
        {
            return (NBTTagCompound)nameValueToNBT("tag", jsonString).parse();
        }
    }

    static int topTagsCount(String str) throws NBTException
    {
        int i = 0;
        boolean flag = false;
        Stack<Character> stack = new Stack();

        for (int j = 0; j < str.length(); ++j)
        {
            char c0 = str.charAt(j);

            if (c0 == 34)
            {
                if (isCharEscaped(str, j))
                {
                    if (!flag)
                    {
                        throw new NBTException("Illegal use of \\\": " + str);
                    }
                }
                else
                {
                    flag = !flag;
                }
            }
            else if (!flag)
            {
                if (c0 != 123 && c0 != 91)
                {
                    if (c0 == 125 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + str);
                    }

                    if (c0 == 93 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + str);
                    }
                }
                else
                {
                    if (stack.isEmpty())
                    {
                        ++i;
                    }

                    stack.push(Character.valueOf(c0));
                }
            }
        }

        if (flag)
        {
            throw new NBTException("Unbalanced quotation: " + str);
        }
        else if (!stack.isEmpty())
        {
            throw new NBTException("Unbalanced brackets: " + str);
        }
        else
        {
            if (i == 0 && !str.isEmpty())
            {
                i = 1;
            }

            return i;
        }
    }

    static JsonToNBT.Any joinStrToNBT(String... args) throws NBTException
    {
        return nameValueToNBT(args[0], args[1]);
    }

    static JsonToNBT.Any nameValueToNBT(String key, String value) throws NBTException
    {
        value = value.trim();

        if (value.startsWith("{"))
        {
            value = value.substring(1, value.length() - 1);
            JsonToNBT.Compound jsontonbt$compound;
            String s1;

            for (jsontonbt$compound = new JsonToNBT.Compound(key); value.length() > 0; value = value.substring(s1.length() + 1))
            {
                s1 = nextNameValuePair(value, true);

                if (s1.length() > 0)
                {
                    boolean flag1 = false;
                    jsontonbt$compound.tagList.add(getTagFromNameValue(s1, false));
                }

                if (value.length() < s1.length() + 1)
                {
                    break;
                }

                char c1 = value.charAt(s1.length());

                if (c1 != 44 && c1 != 123 && c1 != 125 && c1 != 91 && c1 != 93)
                {
                    throw new NBTException("Unexpected token \'" + c1 + "\' at: " + value.substring(s1.length()));
                }
            }

            return jsontonbt$compound;
        }
        else if (value.startsWith("[") && !INT_ARRAY_MATCHER.matcher(value).matches())
        {
            value = value.substring(1, value.length() - 1);
            JsonToNBT.List jsontonbt$list;
            String s;

            for (jsontonbt$list = new JsonToNBT.List(key); value.length() > 0; value = value.substring(s.length() + 1))
            {
                s = nextNameValuePair(value, false);

                if (s.length() > 0)
                {
                    boolean flag = true;
                    jsontonbt$list.tagList.add(getTagFromNameValue(s, true));
                }

                if (value.length() < s.length() + 1)
                {
                    break;
                }

                char c0 = value.charAt(s.length());

                if (c0 != 44 && c0 != 123 && c0 != 125 && c0 != 91 && c0 != 93)
                {
                    throw new NBTException("Unexpected token \'" + c0 + "\' at: " + value.substring(s.length()));
                }
            }

            return jsontonbt$list;
        }
        else
        {
            return new JsonToNBT.Primitive(key, value);
        }
    }

    private static JsonToNBT.Any getTagFromNameValue(String str, boolean isArray) throws NBTException
    {
        String s = locateName(str, isArray);
        String s1 = locateValue(str, isArray);
        return joinStrToNBT(new String[] {s, s1});
    }

    private static String nextNameValuePair(String str, boolean isCompound) throws NBTException
    {
        int i = getNextCharIndex(str, ':');
        int j = getNextCharIndex(str, ',');

        if (isCompound)
        {
            if (i == -1)
            {
                throw new NBTException("Unable to locate name/value separator for string: " + str);
            }

            if (j != -1 && j < i)
            {
                throw new NBTException("Name error at: " + str);
            }
        }
        else if (i == -1 || i > j)
        {
            i = -1;
        }

        return locateValueAt(str, i);
    }

    private static String locateValueAt(String str, int index) throws NBTException
    {
        Stack<Character> stack = new Stack();
        int i = index + 1;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;

        for (int j = 0; i < str.length(); ++i)
        {
            char c0 = str.charAt(i);

            if (c0 == 34)
            {
                if (isCharEscaped(str, i))
                {
                    if (!flag)
                    {
                        throw new NBTException("Illegal use of \\\": " + str);
                    }
                }
                else
                {
                    flag = !flag;

                    if (flag && !flag2)
                    {
                        flag1 = true;
                    }

                    if (!flag)
                    {
                        j = i;
                    }
                }
            }
            else if (!flag)
            {
                if (c0 != 123 && c0 != 91)
                {
                    if (c0 == 125 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + str);
                    }

                    if (c0 == 93 && (stack.isEmpty() || ((Character)stack.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + str);
                    }

                    if (c0 == 44 && stack.isEmpty())
                    {
                        return str.substring(0, i);
                    }
                }
                else
                {
                    stack.push(Character.valueOf(c0));
                }
            }

            if (!Character.isWhitespace(c0))
            {
                if (!flag && flag1 && j != i)
                {
                    return str.substring(0, j + 1);
                }

                flag2 = true;
            }
        }

        return str.substring(0, i);
    }

    private static String locateName(String str, boolean isArray) throws NBTException
    {
        if (isArray)
        {
            str = str.trim();

            if (str.startsWith("{") || str.startsWith("["))
            {
                return "";
            }
        }

        int i = getNextCharIndex(str, ':');

        if (i == -1)
        {
            if (isArray)
            {
                return "";
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + str);
            }
        }
        else
        {
            return str.substring(0, i).trim();
        }
    }

    private static String locateValue(String str, boolean isArray) throws NBTException
    {
        if (isArray)
        {
            str = str.trim();

            if (str.startsWith("{") || str.startsWith("["))
            {
                return str;
            }
        }

        int i = getNextCharIndex(str, ':');

        if (i == -1)
        {
            if (isArray)
            {
                return str;
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + str);
            }
        }
        else
        {
            return str.substring(i + 1).trim();
        }
    }

    private static int getNextCharIndex(String str, char targetChar)
    {
        int i = 0;

        for (boolean flag = true; i < str.length(); ++i)
        {
            char c0 = str.charAt(i);

            if (c0 == 34)
            {
                if (!isCharEscaped(str, i))
                {
                    flag = !flag;
                }
            }
            else if (flag)
            {
                if (c0 == targetChar)
                {
                    return i;
                }

                if (c0 == 123 || c0 == 91)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    private static boolean isCharEscaped(String str, int index)
    {
        return index > 0 && str.charAt(index - 1) == 92 && !isCharEscaped(str, index - 1);
    }

    abstract static class Any
        {
            protected String json;

            /**
             * Parses the JSON string contained in this object.
             * @return an {@link NBTBase} which can be safely cast to the type represented by this class.
             */
            public abstract NBTBase parse() throws NBTException;
        }

    static class Compound extends JsonToNBT.Any
        {
            protected java.util.List<JsonToNBT.Any> tagList = Lists.<JsonToNBT.Any>newArrayList();

            public Compound(String jsonIn)
            {
                this.json = jsonIn;
            }

            /**
             * Parses the JSON string contained in this object.
             * @return an {@link NBTBase} which can be safely cast to the type represented by this class.
             */
            public NBTBase parse() throws NBTException
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                for (JsonToNBT.Any jsontonbt$any : this.tagList)
                {
                    nbttagcompound.setTag(jsontonbt$any.json, jsontonbt$any.parse());
                }

                return nbttagcompound;
            }
        }

    static class List extends JsonToNBT.Any
        {
            protected java.util.List<JsonToNBT.Any> tagList = Lists.<JsonToNBT.Any>newArrayList();

            public List(String json)
            {
                this.json = json;
            }

            /**
             * Parses the JSON string contained in this object.
             * @return an {@link NBTBase} which can be safely cast to the type represented by this class.
             */
            public NBTBase parse() throws NBTException
            {
                NBTTagList nbttaglist = new NBTTagList();

                for (JsonToNBT.Any jsontonbt$any : this.tagList)
                {
                    nbttaglist.appendTag(jsontonbt$any.parse());
                }

                return nbttaglist;
            }
        }

    static class Primitive extends JsonToNBT.Any
        {
            private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
            private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
            private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
            private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
            private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
            private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
            private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
            private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
            /** The value to be parsed into a tag. */
            protected String jsonValue;

            public Primitive(String jsonIn, String valueIn)
            {
                this.json = jsonIn;
                this.jsonValue = valueIn;
            }

            /**
             * Parses the JSON string contained in this object.
             * @return an {@link NBTBase} which can be safely cast to the type represented by this class.
             */
            public NBTBase parse() throws NBTException
            {
                try
                {
                    if (DOUBLE.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                    }

                    if (FLOAT.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                    }

                    if (BYTE.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                    }

                    if (LONG.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                    }

                    if (SHORT.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                    }

                    if (INTEGER.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagInt(Integer.parseInt(this.jsonValue));
                    }

                    if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches())
                    {
                        return new NBTTagDouble(Double.parseDouble(this.jsonValue));
                    }

                    if ("true".equalsIgnoreCase(this.jsonValue) || "false".equalsIgnoreCase(this.jsonValue))
                    {
                        return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
                    }
                }
                catch (NumberFormatException var6)
                {
                    this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                    return new NBTTagString(this.jsonValue);
                }

                if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]"))
                {
                    String s = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                    String[] astring = (String[])Iterables.toArray(SPLITTER.split(s), String.class);

                    try
                    {
                        int[] aint = new int[astring.length];

                        for (int j = 0; j < astring.length; ++j)
                        {
                            aint[j] = Integer.parseInt(astring[j].trim());
                        }

                        return new NBTTagIntArray(aint);
                    }
                    catch (NumberFormatException var5)
                    {
                        return new NBTTagString(this.jsonValue);
                    }
                }
                else
                {
                    if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\""))
                    {
                        this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                    }

                    this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                    StringBuilder stringbuilder = new StringBuilder();

                    for (int i = 0; i < this.jsonValue.length(); ++i)
                    {
                        if (i < this.jsonValue.length() - 1 && this.jsonValue.charAt(i) == 92 && this.jsonValue.charAt(i + 1) == 92)
                        {
                            stringbuilder.append('\\');
                            ++i;
                        }
                        else
                        {
                            stringbuilder.append(this.jsonValue.charAt(i));
                        }
                    }

                    return new NBTTagString(stringbuilder.toString());
                }
            }
        }
}