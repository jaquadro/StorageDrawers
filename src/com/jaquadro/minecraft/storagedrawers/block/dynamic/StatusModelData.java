package com.jaquadro.minecraft.storagedrawers.block.dynamic;

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

        private int activeStepsX;
        private int activeStepsY;

        public Slot (JsonObject json) {
            if (json == null)
                return;

            statusArea = readArea(json, "statusAreaFrom", "statusAreaTo");
            statusActiveArea = readArea(json, "statusActiveFrom", "statusActiveTo");

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

            double startX = 0;
            double startY = 0;
            double stopX = 0;
            double stopY = 0;

            if (object.has(keyStart)) {
                JsonArray arr = object.getAsJsonArray(keyStart);
                if (arr != null && arr.size() == 2) {
                    startX = arr.get(0).getAsDouble();
                    startY = arr.get(1).getAsDouble();
                }
            }

            if (object.has(keyStop)) {
                JsonArray arr = object.getAsJsonArray(keyStop);
                if (arr != null && arr.size() == 2) {
                    stopX = arr.get(0).getAsDouble();
                    stopY = arr.get(1).getAsDouble();
                }
            }

            return Area2D.From(startX, startY, stopX, stopY);
        }
    }
}
