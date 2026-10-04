package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.client.gui.Font;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Locale;

public class CountFormatter
{
    private static final ThreadLocal<LabelCache> LABELS = ThreadLocal.withInitial(LabelCache::new);

    private static final class LabelCache {
        final Int2ObjectOpenHashMap<String> labels = new Int2ObjectOpenHashMap<>();
        Locale locale;
    }

    public static String format (Font font, IDrawer drawer) {
        return formatApprox(font, drawer);
    }

    public static String formatStackNotation (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        String text;
        int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
        int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());

        if (stacks > 0 && remainder > 0)
            text = stacks + "x" + drawer.getStoredItemStackSize() + "+" + remainder;
        else if (stacks > 0)
            text = stacks + "x" + drawer.getStoredItemStackSize();
        else
            text = String.valueOf(remainder);

        return text;
    }

    public static String formatExact (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        return String.valueOf(drawer.getStoredItemCount());
    }

    public static String formatApprox (Font font, IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        int count = drawer.getStoredItemCount();
        if (count < 10000)
            return String.valueOf(count);

        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        LabelCache cache = LABELS.get();
        if (!locale.equals(cache.locale)) {
            cache.labels.clear();
            cache.locale = locale;
        }
        String cached = cache.labels.get(count);
        if (cached != null)
            return cached;

        String text;

        if (count >= 1000000000)
            text = String.format(locale, "%.1fG", count / 1000000000f);
        else if (count >= 100000000)
            text = String.format(locale, "%.0fM", count / 1000000f);
        else if (count >= 1000000)
            text = String.format(locale, "%.1fM", count / 1000000f);
        else if (count >= 100000)
            text = String.format(locale, "%.0fK", count / 1000f);
        else if (count >= 10000)
            text = String.format(locale, "%.1fK", count / 1000f);
        else
            text = String.valueOf(count);

        if (cache.labels.size() >= 2048)
            cache.labels.clear();
        cache.labels.put(count, text);
        return text;
    }
}
