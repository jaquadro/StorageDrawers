/*package com.jaquadro.minecraft.storagedrawers.block.dynamic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaquadro.minecraft.chameleon.geometry.Area2D;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StatusModelData
{
    private double frontDepth;
    private Slot[] slots;

    public StatusModelData (int slotCount, ResourceLocation location) {
        slots = new Slot[slotCount];
        load(location);
    }

    private void load (ResourceLocation location) {
        try {
            IResource configResource = Minecraft.getMinecraft().getResourceManager().getResource(location);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(configResource.getInputStream()));
                JsonObject root = (new JsonParser()).parse(reader).getAsJsonObject();

                if (root.has("frontDepth"))
                    frontDepth = root.get("frontDepth").getAsDouble();

                if (root.has("slots")) {
                    JsonArray slotsArray = root.getAsJsonArray("slots");
                    if (slotsArray != null && slotsArray.size() == slots.length) {
                        for (int i = 0; i < slots.length; i++)
                            slots[i] = new Slot(slotsArray.get(i).getAsJsonObject());
                    }
                }
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getFrontDepth () {
        return frontDepth;
    }

    public Slot getSlot (int i) {
        return slots[i];
    }

    public static class Slot {
        private ResourceLocation level1On;
        private ResourceLocation level1Off;
        private ResourceLocation level2On;
        private ResourceLocation level2Off;

        private Area2D statusArea;
        private Area2D statusActiveArea;
        private Area2D labelArea;
        private Area2D slotArea;
        private Area2D iconArea;

        private int activeStepsX;
        private int activeStepsY;

        public Slot (JsonObject json) {
            if (json == null)
                return;

            statusArea = readArea(json, "statusAreaFrom", "statusAreaTo");
            statusActiveArea = readArea(json, "statusActiveFrom", "statusActiveTo");

            labelArea = readArea(json, "labelAreaFrom", "labelAreaTo");
            if (labelArea.equals(Area2D.EMPTY))
                labelArea = statusActiveArea;

            slotArea = readArea(json, "slotAreaFrom", "slotAreaTo");
            if (slotArea.equals(Area2D.EMPTY))
                slotArea = statusArea;

            iconArea = readIconArea(json, "iconSize", slotArea);

            if (json.has("statusActiveSteps")) {
                JsonArray arr = json.getAsJsonArray("statusActiveSteps");
                if (arr != null && arr.size() == 2) {
                    activeStepsX = arr.get(0).getAsInt();
                    activeStepsY = arr.get(1).getAsInt();
                }
            }

            if (json.has("textures")) {
                JsonObject textures = json.getAsJsonObject("textures");
                if (textures.has("level1Off"))
                    level1Off = new ResourceLocation(textures.get("level1Off").getAsString());
                if (textures.has("level1On"))
                    level1On = new ResourceLocation(textures.get("level1On").getAsString());
                if (textures.has("level2Off"))
                    level2Off = new ResourceLocation(textures.get("level2Off").getAsString());
                if (textures.has("level2On"))
                    level2On = new ResourceLocation(textures.get("level2On").getAsString());
            }
        }

        public Area2D getStatusArea () {
            return statusArea;
        }

        public Area2D getStatusActiveArea () {
            return statusActiveArea;
        }

        public Area2D getLabelArea () {
            return labelArea;
        }

        public Area2D getSlotArea () {
            return slotArea;
        }

        public Area2D getIconArea () {
            return iconArea;
        }

        public int getActiveStepsX () {
            return activeStepsX;
        }

        public int getActiveStepsY () {
            return activeStepsY;
        }

        public ResourceLocation getOnResource (EnumUpgradeStatus status) {
            switch (status.getLevel()) {
                case 1:
                    return level1On;
                case 2:
                    return level2On;
                default:
                    return null;
            }
        }

        public ResourceLocation getOffResource (EnumUpgradeStatus status) {
            switch (status.getLevel()) {
                case 1:
                    return level1Off;
                case 2:
                    return level2Off;
                default:
                    return null;
            }
        }

        private Area2D readArea (JsonObject object, String keyStart, String keyStop) {
            if (object == null)
                return Area2D.EMPTY;

            double[] start = readDoublePair(object, keyStart);
            double[] stop = readDoublePair(object, keyStop);

            return Area2D.From(start[0], start[1], stop[0], stop[1]);
        }

        private Area2D readIconArea (JsonObject object, String key, Area2D bound) {
            if (object == null)
                return Area2D.EMPTY;

            double[] size = readDoublePair(object, key);
            double startX = bound.getX() + bound.getWidth() / 2 - size[0] / 2;
            double startY = bound.getY() + bound.getHeight() / 2 - size[1] / 2;

            return Area2D.From(startX, startY, startX + size[0], startY + size[1]);
        }

        private double[] readDoublePair (JsonObject object, String key) {
            double[] size = new double[] { 0, 0 };

            if (object.has(key)) {
                JsonArray arr = object.getAsJsonArray(key);
                if (arr != null && arr.size() == 2) {
                    size[0] = arr.get(0).getAsDouble();
                    size[1] = arr.get(1).getAsDouble();
                }
            }

            return size;
        }
    }
}
*/