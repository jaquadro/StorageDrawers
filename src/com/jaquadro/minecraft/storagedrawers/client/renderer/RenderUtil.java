package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;

public final class RenderUtil
{
    /*private static float[] colorScratch = new float[3];

    private RenderUtil () { }

    public static void calculateBaseColor (float[] target, int color) {
        float r = (float)(color >> 16 & 255) / 255f;
        float g = (float)(color >> 8 & 255) / 255f;
        float b = (float)(color & 255) / 255f;

        if (EntityRenderer.anaglyphEnable) {
            float gray = (r * 30f + g * 59f + b * 11f) / 100f;
            float rg = (r * 30f + g * 70f) / 100f;
            float rb = (r * 30f + b * 70f) / 100f;

            r = gray;
            g = rg;
            b = rb;
        }

        target[0] = r;
        target[1] = g;
        target[2] = b;
    }

    public static void scaleColor (float[] target, float[] source, float scale) {
        target[0] = source[0] * scale;
        target[1] = source[1] * scale;
        target[2] = source[2] * scale;
    }

    public static void setTessellatorColor (Tessellator tessellator, float[] color) {
        tessellator.setColorOpaque_F(color[0], color[1], color[2]);
    }

    public static void renderEmptyPlane (Block block, int x, int y, int z, RenderBlocks renderer) {
        renderer.setRenderBounds(0, 0, 0, 0, 0, 0);
        renderer.renderFaceYNeg(block, x, y, z, Blocks.dirt.getIcon(0, 0));
    }

    public static void renderBlock (RenderBlocks renderer, Block block, int x, int y, int z) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        float r = colorScratch[0];
        float g = colorScratch[1];
        float b = colorScratch[2];

        renderFaceYNeg(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 0), r, g, b);
        renderFaceYPos(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 1), r, g, b);
        renderFaceZNeg(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 2), r, g, b);
        renderFaceZPos(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 3), r, g, b);
        renderFaceXNeg(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 4), r, g, b);
        renderFaceXPos(renderer, block, x, y, z, block.getIcon(renderer.blockAccess, x, y, z, 5), r, g, b);
    }

    public static void renderFaceYNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceYNeg(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceYPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceYPos(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceZNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceZNeg(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceZPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceZPos(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceXNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceXNeg(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceXPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon) {
        calculateBaseColor(colorScratch, block.colorMultiplier(renderer.blockAccess, x, y, z));
        renderFaceXPos(renderer, block, x, y, z, icon, colorScratch[0], colorScratch[1], colorScratch[2]);
    }

    public static void renderFaceYNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceYNegAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceYNegColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceYPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceYPosAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceYPosColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceZNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceZNegAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceZNegColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceZPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceZPosAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceZPosColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceXNeg (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceXNegAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceXNegColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceXPos (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (Minecraft.isAmbientOcclusionEnabled() && renderer.blockAccess != null && block.getLightValue(renderer.blockAccess, x, y, z) == 0)
            renderFaceXPosAOPartial(renderer, block, x, y, z, icon, r, g, b);
        else
            renderFaceXPosColorMult(renderer, block, x, y, z, icon, r, g, b);
    }

    public static void renderFaceYNegColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(.5f * r, .5f * g, .5f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0, -1, 0);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, (renderer.renderMinY > 0) ? y : y - 1, z));

        renderer.enableAO = false;
        renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    public static void renderFaceYPosColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(1f * r, 1f * g, 1f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0, 1, 0);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, (renderer.renderMaxY < 1) ? y : y + 1, z));

        renderer.enableAO = false;
        renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    public static void renderFaceZNegColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(.8f * r, .8f * g, .8f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0, 0, -1);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, (renderer.renderMinZ > 0) ? z : z - 1));

        renderer.enableAO = false;
        renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    public static void renderFaceZPosColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(.8f * r, .8f * g, .8f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0, 0, 1);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, (renderer.renderMaxZ < 1) ? z : z + 1));

        renderer.enableAO = false;
        renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    public static void renderFaceXNegColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(.6f * r, .6f * g, .6f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1, 0, 0);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, (renderer.renderMinX > 0) ? x : x - 1, y, z));

        renderer.enableAO = false;
        renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    public static void renderFaceXPosColorMult (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        if (icon == null)
            return;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(.6f * r, .6f * g, .6f * b);
        if (renderer.blockAccess == null) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1, 0, 0);
        }
        else
            tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, (renderer.renderMaxX < 1) ? x : x + 1, y, z));

        renderer.enableAO = false;
        renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, icon);

        if (renderer.blockAccess == null)
            tessellator.draw();
    }

    private static int aoBrightnessXYNN;
    private static int aoBrightnessYZNN;
    private static int aoBrightnessYZNP;
    private static int aoBrightnessXYPN;
    private static int aoBrightnessXYNP;
    private static int aoBrightnessXYPP;
    private static int aoBrightnessYZPN;
    private static int aoBrightnessYZPP;
    private static int aoBrightnessXZNN;
    private static int aoBrightnessXZPN;
    private static int aoBrightnessXZNP;
    private static int aoBrightnessXZPP;
    private static int aoBrightnessXYZNNN;
    private static int aoBrightnessXYZNNP;
    private static int aoBrightnessXYZPNN;
    private static int aoBrightnessXYZPNP;
    private static int aoBrightnessXYZNPN;
    private static int aoBrightnessXYZPPN;
    private static int aoBrightnessXYZNPP;
    private static int aoBrightnessXYZPPP;

    private static int aoBrightnessXZNI;
    private static int aoBrightnessYZNI;
    private static int aoBrightnessYZPI;
    private static int aoBrightnessXZPI;
    private static int aoBrightnessXYIN;
    private static int aoBrightnessXZIN;
    private static int aoBrightnessXZIP;
    private static int aoBrightnessXYIP;
    private static int aoBrightnessXYZNNI;
    private static int aoBrightnessXYZNPI;
    private static int aoBrightnessXYZPNI;
    private static int aoBrightnessXYZPPI;
    private static int aoBrightnessXYZINN;
    private static int aoBrightnessXYZINP;
    private static int aoBrightnessXYZIPN;
    private static int aoBrightnessXYZIPP;

    private static float aoLightValueScratchXYNN;
    private static float aoLightValueScratchYZNN;
    private static float aoLightValueScratchYZNP;
    private static float aoLightValueScratchXYPN;
    private static float aoLightValueScratchXYNP;
    private static float aoLightValueScratchXYPP;
    private static float aoLightValueScratchYZPN;
    private static float aoLightValueScratchYZPP;
    private static float aoLightValueScratchXZNN;
    private static float aoLightValueScratchXZPN;
    private static float aoLightValueScratchXZNP;
    private static float aoLightValueScratchXZPP;
    private static float aoLightValueScratchXYZNNN;
    private static float aoLightValueScratchXYZNNP;
    private static float aoLightValueScratchXYZPNN;
    private static float aoLightValueScratchXYZPNP;
    private static float aoLightValueScratchXYZNPN;
    private static float aoLightValueScratchXYZPPN;
    private static float aoLightValueScratchXYZNPP;
    private static float aoLightValueScratchXYZPPP;

    private static float aoLightValueScratchXZNI;
    private static float aoLightValueScratchYZNI;
    private static float aoLightValueScratchYZPI;
    private static float aoLightValueScratchXZPI;
    private static float aoLightValueScratchXYIN;
    private static float aoLightValueScratchXZIN;
    private static float aoLightValueScratchXZIP;
    private static float aoLightValueScratchXYIP;
    private static float aoLightValueScratchXYZNNI;
    private static float aoLightValueScratchXYZNPI;
    private static float aoLightValueScratchXYZPNI;
    private static float aoLightValueScratchXYZPPI;
    private static float aoLightValueScratchXYZINN;
    private static float aoLightValueScratchXYZINP;
    private static float aoLightValueScratchXYZIPN;
    private static float aoLightValueScratchXYZIPP;

    private static void setupAOBrightnessZNeg (RenderBlocks renderer, Block block, int x, int y, int z, boolean bgXP, boolean bgXN, boolean bgYP, boolean bgYN) {
        aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZNNN = aoLightValueScratchXZNN;
        aoLightValueScratchXYZNPN = aoLightValueScratchXZNN;
        aoLightValueScratchXYZPNN = aoLightValueScratchXZPN;
        aoLightValueScratchXYZPPN = aoLightValueScratchXZPN;

        aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
        aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
        aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
        aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
        aoBrightnessXYZNNN = aoBrightnessXZNN;
        aoBrightnessXYZNPN = aoBrightnessXZNN;
        aoBrightnessXYZPNN = aoBrightnessXZPN;
        aoBrightnessXYZPPN = aoBrightnessXZPN;

        if (bgXN || bgYN) {
            aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y - 1, z);
        }

        if (bgXN || bgYP) {
            aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y + 1, z);
        }

        if (bgXP || bgYN) {
            aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y - 1, z);
        }

        if (bgXP || bgYP) {
            aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y + 1, z);
        }
    }

    private static void setupAOBrightnessZPos (RenderBlocks renderer, Block block, int x, int y, int z, boolean bgXP, boolean bgXN, boolean bgYP, boolean bgYN) {
        aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZNNP = aoLightValueScratchXZNP;
        aoLightValueScratchXYZNPP = aoLightValueScratchXZNP;
        aoLightValueScratchXYZPNP = aoLightValueScratchXZPP;
        aoLightValueScratchXYZPPP = aoLightValueScratchXZPP;

        aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
        aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
        aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
        aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
        aoBrightnessXYZNNP = aoBrightnessXZNP;
        aoBrightnessXYZNPP = aoBrightnessXZNP;
        aoBrightnessXYZPNP = aoBrightnessXZPP;
        aoBrightnessXYZPPP = aoBrightnessXZPP;

        if (bgXN || bgYN) {
            aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y - 1, z);
        }

        if (bgXN || bgYP) {
            aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y + 1, z);
        }

        if (bgXP || bgYN) {
            aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y - 1, z);
        }

        if (bgXP || bgYP) {
            aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y + 1, z);
        }
    }

    private static void setupAOBrightnessXNeg (RenderBlocks renderer, Block block, int x, int y, int z, boolean bgYP, boolean bgYN, boolean bgZN, boolean bgZP) {
        aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZNNN = aoLightValueScratchXZNN;
        aoLightValueScratchXYZNNP = aoLightValueScratchXZNP;
        aoLightValueScratchXYZNPN = aoLightValueScratchXZNN;
        aoLightValueScratchXYZNPP = aoLightValueScratchXZNP;

        aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
        aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
        aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
        aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
        aoBrightnessXYZNNN = aoBrightnessXZNN;
        aoBrightnessXYZNNP = aoBrightnessXZNP;
        aoBrightnessXYZNPN = aoBrightnessXZNN;
        aoBrightnessXYZNPP = aoBrightnessXZNP;

        if (bgZN || bgYN) {
            aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z - 1);
        }

        if (bgZP || bgYN) {
            aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z + 1);
        }

        if (bgZN || bgYP) {
            aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z - 1);
        }

        if (bgZP || bgYP) {
            aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z + 1);
        }
    }

    private static void setupAOBrightnessXPos (RenderBlocks renderer, Block block, int x, int y, int z, boolean bgYP, boolean bgYN, boolean bgZN, boolean bgZP) {
        aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZPNN = aoLightValueScratchXZPN;
        aoLightValueScratchXYZPNP = aoLightValueScratchXZPP;
        aoLightValueScratchXYZPPN = aoLightValueScratchXZPN;
        aoLightValueScratchXYZPPP = aoLightValueScratchXZPP;

        aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
        aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
        aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
        aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
        aoBrightnessXYZPNN = aoBrightnessXZPN;
        aoBrightnessXYZPNP = aoBrightnessXZPP;
        aoBrightnessXYZPPN = aoBrightnessXZPN;
        aoBrightnessXYZPPP = aoBrightnessXZPP;

        if (bgYN || bgZN) {
            aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z - 1);
        }

        if (bgYN || bgZP) {
            aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z + 1);
        }

        if (bgYP || bgZN) {
            aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z - 1);
        }

        if (bgYP || bgZP) {
            aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z + 1);
        }
    }

    private static void mixAOBrightnessLightValueZ (float fMin, float fMax) {
        if (fMin == 1 && fMax == 0) {
            aoLightValueScratchXZNI = aoLightValueScratchXZNN;
            aoLightValueScratchYZNI = aoLightValueScratchYZNN;
            aoLightValueScratchYZPI = aoLightValueScratchYZPN;
            aoLightValueScratchXZPI = aoLightValueScratchXZPN;
            aoLightValueScratchXYZNNI = aoLightValueScratchXYZNNN;
            aoLightValueScratchXYZNPI = aoLightValueScratchXYZNPN;
            aoLightValueScratchXYZPNI = aoLightValueScratchXYZPNN;
            aoLightValueScratchXYZPPI = aoLightValueScratchXYZPPN;

            aoBrightnessXZNI = aoBrightnessXZNN;
            aoBrightnessYZNI = aoBrightnessYZNN;
            aoBrightnessYZPI = aoBrightnessYZPN;
            aoBrightnessXZPI = aoBrightnessXZPN;
            aoBrightnessXYZNNI = aoBrightnessXYZNNN;
            aoBrightnessXYZNPI = aoBrightnessXYZNPN;
            aoBrightnessXYZPNI = aoBrightnessXYZPNN;
            aoBrightnessXYZPPI = aoBrightnessXYZPPN;
        }
        else if (fMin == 0 && fMax == 1) {
            aoLightValueScratchXZNI = aoLightValueScratchXZNP;
            aoLightValueScratchYZNI = aoLightValueScratchYZNP;
            aoLightValueScratchYZPI = aoLightValueScratchYZPP;
            aoLightValueScratchXZPI = aoLightValueScratchXZPP;
            aoLightValueScratchXYZNNI = aoLightValueScratchXYZNNP;
            aoLightValueScratchXYZNPI = aoLightValueScratchXYZNPP;
            aoLightValueScratchXYZPNI = aoLightValueScratchXYZPNP;
            aoLightValueScratchXYZPPI = aoLightValueScratchXYZPPP;

            aoBrightnessXZNI = aoBrightnessXZNP;
            aoBrightnessYZNI = aoBrightnessYZNP;
            aoBrightnessYZPI = aoBrightnessYZPP;
            aoBrightnessXZPI = aoBrightnessXZPP;
            aoBrightnessXYZNNI = aoBrightnessXYZNNP;
            aoBrightnessXYZNPI = aoBrightnessXYZNPP;
            aoBrightnessXYZPNI = aoBrightnessXYZPNP;
            aoBrightnessXYZPPI = aoBrightnessXYZPPP;
        }
        else {
            aoLightValueScratchXZNI = aoLightValueScratchXZNN * fMin + aoLightValueScratchXZNP * fMax;
            aoLightValueScratchYZNI = aoLightValueScratchYZNN * fMin + aoLightValueScratchYZNP * fMax;
            aoLightValueScratchYZPI = aoLightValueScratchYZPN * fMin + aoLightValueScratchYZPP * fMax;
            aoLightValueScratchXZPI = aoLightValueScratchXZPN * fMin + aoLightValueScratchXZPP * fMax;
            aoLightValueScratchXYZNNI = aoLightValueScratchXYZNNN * fMin + aoLightValueScratchXYZNNP * fMax;
            aoLightValueScratchXYZNPI = aoLightValueScratchXYZNPN * fMin + aoLightValueScratchXYZNPP * fMax;
            aoLightValueScratchXYZPNI = aoLightValueScratchXYZPNN * fMin + aoLightValueScratchXYZPNP * fMax;
            aoLightValueScratchXYZPPI = aoLightValueScratchXYZPPN * fMin + aoLightValueScratchXYZPPP * fMax;

            aoBrightnessXZNI = mixAOBrightness(aoBrightnessXZNN, aoBrightnessXZNP, fMin, fMax);
            aoBrightnessYZNI = mixAOBrightness(aoBrightnessYZNN, aoBrightnessYZNP, fMin, fMax);
            aoBrightnessYZPI = mixAOBrightness(aoBrightnessYZPN, aoBrightnessYZPP, fMin, fMax);
            aoBrightnessXZPI = mixAOBrightness(aoBrightnessXZPN, aoBrightnessXZPP, fMin, fMax);
            aoBrightnessXYZNNI = mixAOBrightness(aoBrightnessXYZNNN, aoBrightnessXYZNNP, fMin, fMax);
            aoBrightnessXYZNPI = mixAOBrightness(aoBrightnessXYZNPN, aoBrightnessXYZNPP, fMin, fMax);
            aoBrightnessXYZPNI = mixAOBrightness(aoBrightnessXYZPNN, aoBrightnessXYZPNP, fMin, fMax);
            aoBrightnessXYZPPI = mixAOBrightness(aoBrightnessXYZPPN, aoBrightnessXYZPPP, fMin, fMax);
        }
    }

    private static void mixAOBrightnessLightValueX (float fMin, float fMax) {
        if (fMin == 1 && fMax == 0) {
            aoLightValueScratchXYIN = aoLightValueScratchXYNN;
            aoLightValueScratchXZIN = aoLightValueScratchXZNN;
            aoLightValueScratchXZIP = aoLightValueScratchXZNP;
            aoLightValueScratchXYIP = aoLightValueScratchXYNP;
            aoLightValueScratchXYZINN = aoLightValueScratchXYZNNN;
            aoLightValueScratchXYZINP = aoLightValueScratchXYZNNP;
            aoLightValueScratchXYZIPN = aoLightValueScratchXYZNPN;
            aoLightValueScratchXYZIPP = aoLightValueScratchXYZNPP;

            aoBrightnessXYIN = aoBrightnessXYNN;
            aoBrightnessXZIN = aoBrightnessXZNN;
            aoBrightnessXZIP = aoBrightnessXZNP;
            aoBrightnessXYIP = aoBrightnessXYNP;
            aoBrightnessXYZINN = aoBrightnessXYZNNN;
            aoBrightnessXYZINP = aoBrightnessXYZNNP;
            aoBrightnessXYZIPN = aoBrightnessXYZNPN;
            aoBrightnessXYZIPP = aoBrightnessXYZNPP;
        }
        else if (fMin == 0 && fMax == 1) {
            aoLightValueScratchXYIN = aoLightValueScratchXYPN;
            aoLightValueScratchXZIN = aoLightValueScratchXZPN;
            aoLightValueScratchXZIP = aoLightValueScratchXZPP;
            aoLightValueScratchXYIP = aoLightValueScratchXYPP;
            aoLightValueScratchXYZINN = aoLightValueScratchXYZPNN;
            aoLightValueScratchXYZINP = aoLightValueScratchXYZPNP;
            aoLightValueScratchXYZIPN = aoLightValueScratchXYZPPN;
            aoLightValueScratchXYZIPP = aoLightValueScratchXYZPPP;

            aoBrightnessXYIN = aoBrightnessXYPN;
            aoBrightnessXZIN = aoBrightnessXZPN;
            aoBrightnessXZIP = aoBrightnessXZPP;
            aoBrightnessXYIP = aoBrightnessXYPP;
            aoBrightnessXYZINN = aoBrightnessXYZPNN;
            aoBrightnessXYZINP = aoBrightnessXYZPNP;
            aoBrightnessXYZIPN = aoBrightnessXYZPPN;
            aoBrightnessXYZIPP = aoBrightnessXYZPPP;
        }
        else {
            aoLightValueScratchXYIN = aoLightValueScratchXYNN * fMin + aoLightValueScratchXYPN * fMax;
            aoLightValueScratchXZIN = aoLightValueScratchXZNN * fMin + aoLightValueScratchXZPN * fMax;
            aoLightValueScratchXZIP = aoLightValueScratchXZNP * fMin + aoLightValueScratchXZPP * fMax;
            aoLightValueScratchXYIP = aoLightValueScratchXYNP * fMin + aoLightValueScratchXYPP * fMax;
            aoLightValueScratchXYZINN = aoLightValueScratchXYZNNN * fMin + aoLightValueScratchXYZPNN * fMax;
            aoLightValueScratchXYZINP = aoLightValueScratchXYZNNP * fMin + aoLightValueScratchXYZPNP * fMax;
            aoLightValueScratchXYZIPN = aoLightValueScratchXYZNPN * fMin + aoLightValueScratchXYZPPN * fMax;
            aoLightValueScratchXYZIPP = aoLightValueScratchXYZNPP * fMin + aoLightValueScratchXYZPPP * fMax;

            aoBrightnessXYIN = mixAOBrightness(aoBrightnessXYNN, aoBrightnessXYPN, fMin, fMax);
            aoBrightnessXZIN = mixAOBrightness(aoBrightnessXZNN, aoBrightnessXZPN, fMin, fMax);
            aoBrightnessXZIP = mixAOBrightness(aoBrightnessXZNP, aoBrightnessXZPP, fMin, fMax);
            aoBrightnessXYIP = mixAOBrightness(aoBrightnessXYNP, aoBrightnessXYPP, fMin, fMax);
            aoBrightnessXYZINN = mixAOBrightness(aoBrightnessXYZNNN, aoBrightnessXYZPNN, fMin, fMax);
            aoBrightnessXYZINP = mixAOBrightness(aoBrightnessXYZNNP, aoBrightnessXYZPNP, fMin, fMax);
            aoBrightnessXYZIPN = mixAOBrightness(aoBrightnessXYZNPN, aoBrightnessXYZPPN, fMin, fMax);
            aoBrightnessXYZIPP = mixAOBrightness(aoBrightnessXYZNPP, aoBrightnessXYZPPP, fMin, fMax);
        }
    }

    public static void renderFaceYNegAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (renderer.renderMinY <= 0.0D)
            --y;

        aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
        aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
        aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
        aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
        aoBrightnessXYZNNN = aoBrightnessXYNN;
        aoBrightnessXYZNNP = aoBrightnessXYNN;
        aoBrightnessXYZPNN = aoBrightnessXYPN;
        aoBrightnessXYZPNP = aoBrightnessXYPN;

        aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
        aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZNNN = aoLightValueScratchXYNN;
        aoLightValueScratchXYZNNP = aoLightValueScratchXYNN;
        aoLightValueScratchXYZPNN = aoLightValueScratchXYPN;
        aoLightValueScratchXYZPNP = aoLightValueScratchXYPN;

        boolean blocksGrassXYPN = renderer.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
        boolean blocksGrassXYNN = renderer.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
        boolean blocksGrassYZNP = renderer.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
        boolean blocksGrassYZNN = renderer.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

        if (blocksGrassYZNN || blocksGrassXYNN) {
            aoLightValueScratchXYZNNN = renderer.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z - 1);
        }

        if (blocksGrassYZNP || blocksGrassXYNN) {
            aoLightValueScratchXYZNNP = renderer.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z + 1);
        }

        if (blocksGrassYZNN || blocksGrassXYPN) {
            aoLightValueScratchXYZPNN = renderer.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z - 1);
        }

        if (blocksGrassYZNP || blocksGrassXYPN) {
            aoLightValueScratchXYZPNP = renderer.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z + 1);
        }

        if (renderer.renderMinY <= 0.0D)
            ++y;

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMinY <= 0.0D || !renderer.blockAccess.getBlock(x, y - 1, z).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
        float aoXYZNNP = (aoLightValueScratchXYZNNP + aoLightValueScratchXYNN + aoLightValueScratchYZNP + aoOpposingBlock) / 4.0F;  // TL
        float aoXYZPNP = (aoLightValueScratchYZNP + aoOpposingBlock + aoLightValueScratchXYZPNP + aoLightValueScratchXYPN) / 4.0F;  // TR
        float aoXYZPNN = (aoOpposingBlock + aoLightValueScratchYZNN + aoLightValueScratchXYPN + aoLightValueScratchXYZPNN) / 4.0F;  // BR
        float aoXYZNNN = (aoLightValueScratchXYNN + aoLightValueScratchXYZNNN + aoOpposingBlock + aoLightValueScratchYZNN) / 4.0F;  // BL

        float aoTL = (float)((double)aoXYZNNP * renderer.renderMinX * (1.0D - renderer.renderMaxZ) + (double)aoXYZPNP * renderer.renderMinX * renderer.renderMaxZ + (double)aoXYZPNN * (1.0D - renderer.renderMinX) * renderer.renderMaxZ + (double)aoXYZNNN * (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMaxZ));
        float aoBL = (float)((double)aoXYZNNP * renderer.renderMinX * (1.0D - renderer.renderMinZ) + (double)aoXYZPNP * renderer.renderMinX * renderer.renderMinZ + (double)aoXYZPNN * (1.0D - renderer.renderMinX) * renderer.renderMinZ + (double)aoXYZNNN * (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMinZ));
        float aoBR = (float)((double)aoXYZNNP * renderer.renderMaxX * (1.0D - renderer.renderMinZ) + (double)aoXYZPNP * renderer.renderMaxX * renderer.renderMinZ + (double)aoXYZPNN * (1.0D - renderer.renderMaxX) * renderer.renderMinZ + (double)aoXYZNNN * (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMinZ));
        float aoTR = (float)((double)aoXYZNNP * renderer.renderMaxX * (1.0D - renderer.renderMaxZ) + (double)aoXYZPNP * renderer.renderMaxX * renderer.renderMaxZ + (double)aoXYZPNN * (1.0D - renderer.renderMaxX) * renderer.renderMaxZ + (double)aoXYZNNN * (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMaxZ));

        int brXYZNNP = renderer.getAoBrightness(aoBrightnessXYZNNP, aoBrightnessXYNN, aoBrightnessYZNP, blockBrightness);
        int brXYZPNP = renderer.getAoBrightness(aoBrightnessYZNP, aoBrightnessXYZPNP, aoBrightnessXYPN, blockBrightness);
        int brXYZPNN = renderer.getAoBrightness(aoBrightnessYZNN, aoBrightnessXYPN, aoBrightnessXYZPNN, blockBrightness);
        int brXYZNNN = renderer.getAoBrightness(aoBrightnessXYNN, aoBrightnessXYZNNN, aoBrightnessYZNN, blockBrightness);

        renderer.brightnessTopLeft = renderer.mixAoBrightness(brXYZNNP, brXYZNNN, brXYZPNN, brXYZPNP, renderer.renderMaxX * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxX) * renderer.renderMaxZ, renderer.renderMaxX * renderer.renderMaxZ);
        renderer.brightnessBottomLeft = renderer.mixAoBrightness(brXYZNNP, brXYZNNN, brXYZPNN, brXYZPNP, renderer.renderMaxX * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxX) * renderer.renderMinZ, renderer.renderMaxX * renderer.renderMinZ);
        renderer.brightnessBottomRight = renderer.mixAoBrightness(brXYZNNP, brXYZNNN, brXYZPNN, brXYZPNP, renderer.renderMinX * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinX) * renderer.renderMinZ, renderer.renderMinX * renderer.renderMinZ);
        renderer.brightnessTopRight = renderer.mixAoBrightness(brXYZNNP, brXYZNNN, brXYZPNN, brXYZPNP, renderer.renderMinX * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinX) * renderer.renderMaxZ, renderer.renderMinX * renderer.renderMaxZ);

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.5F;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.5F;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.5F;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceYNeg(block, (double)x, (double)y, (double)z, icon);
        renderer.enableAO = false;
    }

    public static void renderFaceYPosAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (renderer.renderMaxY >= 1.0D)
            ++y;

        aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
        aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
        aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
        aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
        aoBrightnessXYZNPN = aoBrightnessXYNP;
        aoBrightnessXYZPPN = aoBrightnessXYPP;
        aoBrightnessXYZNPP = aoBrightnessXYNP;
        aoBrightnessXYZPPP = aoBrightnessXYPP;

        aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
        aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
        aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
        aoLightValueScratchXYZNPN = aoLightValueScratchXYNP;
        aoLightValueScratchXYZPPN = aoLightValueScratchXYPP;
        aoLightValueScratchXYZNPP = aoLightValueScratchXYNP;
        aoLightValueScratchXYZPPP = aoLightValueScratchXYPP;

        boolean blocksGrassXYPP = renderer.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
        boolean blocksGrassXYNP = renderer.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
        boolean blocksGrassYZPP = renderer.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
        boolean blocksGrassYZPN = renderer.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

        if (blocksGrassYZPN || blocksGrassXYNP) {
            aoLightValueScratchXYZNPN = renderer.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z - 1);
        }

        if (blocksGrassYZPN || blocksGrassXYPP) {
            aoLightValueScratchXYZPPN = renderer.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z - 1);
        }

        if (blocksGrassYZPP || blocksGrassXYNP) {
            aoLightValueScratchXYZNPP = renderer.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z + 1);
        }

        if (blocksGrassYZPP || blocksGrassXYPP) {
            aoLightValueScratchXYZPPP = renderer.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
            aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z + 1);
        }

        if (renderer.renderMaxY >= 1.0D)
            --y;

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMaxY >= 1.0D || !renderer.blockAccess.getBlock(x, y + 1, z).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
        float aoXYZNPN = (aoLightValueScratchXYZNPP + aoLightValueScratchXYNP + aoLightValueScratchYZPP + aoOpposingBlock) / 4.0F;  // TR
        float aoXYZNPP = (aoLightValueScratchYZPP + aoOpposingBlock + aoLightValueScratchXYZPPP + aoLightValueScratchXYPP) / 4.0F;  // TL
        float aoXYZPPP = (aoOpposingBlock + aoLightValueScratchYZPN + aoLightValueScratchXYPP + aoLightValueScratchXYZPPN) / 4.0F;  // BL
        float aoXYZPPN = (aoLightValueScratchXYNP + aoLightValueScratchXYZNPN + aoOpposingBlock + aoLightValueScratchYZPN) / 4.0F;  // BR

        float aoTL = (float)((double)aoXYZPPP * renderer.renderMaxX * (1.0D - renderer.renderMaxZ) + (double)aoXYZNPP * renderer.renderMaxX * renderer.renderMaxZ + (double)aoXYZNPN * (1.0D - renderer.renderMaxX) * renderer.renderMaxZ + (double)aoXYZPPN * (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMaxZ));
        float aoBL = (float)((double)aoXYZPPP * renderer.renderMaxX * (1.0D - renderer.renderMinZ) + (double)aoXYZNPP * renderer.renderMaxX * renderer.renderMinZ + (double)aoXYZNPN * (1.0D - renderer.renderMaxX) * renderer.renderMinZ + (double)aoXYZPPN * (1.0D - renderer.renderMaxX) * (1.0D - renderer.renderMinZ));
        float aoBR = (float)((double)aoXYZPPP * renderer.renderMinX * (1.0D - renderer.renderMinZ) + (double)aoXYZNPP * renderer.renderMinX * renderer.renderMinZ + (double)aoXYZNPN * (1.0D - renderer.renderMinX) * renderer.renderMinZ + (double)aoXYZPPN * (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMinZ));
        float aoTR = (float)((double)aoXYZPPP * renderer.renderMinX * (1.0D - renderer.renderMaxZ) + (double)aoXYZNPP * renderer.renderMinX * renderer.renderMaxZ + (double)aoXYZNPN * (1.0D - renderer.renderMinX) * renderer.renderMaxZ + (double)aoXYZPPN * (1.0D - renderer.renderMinX) * (1.0D - renderer.renderMaxZ));

        int brXYZPPN = renderer.getAoBrightness(aoBrightnessXYNP, aoBrightnessXYZNPP, aoBrightnessYZPP, blockBrightness);
        int brXYZNPN = renderer.getAoBrightness(aoBrightnessYZPP, aoBrightnessXYPP, aoBrightnessXYZPPP, blockBrightness);
        int brXYZNPP = renderer.getAoBrightness(aoBrightnessYZPN, aoBrightnessXYZPPN, aoBrightnessXYPP, blockBrightness);
        int brXYZPPP = renderer.getAoBrightness(aoBrightnessXYZNPN, aoBrightnessXYNP, aoBrightnessYZPN, blockBrightness);

        renderer.brightnessTopLeft = mixAOBrightness(brXYZPPP, brXYZPPN, brXYZNPN, brXYZNPP, renderer.renderMaxZ, renderer.renderMaxX);
        renderer.brightnessBottomLeft = mixAOBrightness(brXYZPPP, brXYZPPN, brXYZNPN, brXYZNPP, renderer.renderMinZ, renderer.renderMaxX);
        renderer.brightnessBottomRight = mixAOBrightness(brXYZPPP, brXYZPPN, brXYZNPN, brXYZNPP, renderer.renderMinZ, renderer.renderMinX);
        renderer.brightnessTopRight = mixAOBrightness(brXYZPPP, brXYZPPN, brXYZNPN, brXYZNPP, renderer.renderMaxZ, renderer.renderMinX);

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, icon);
        renderer.enableAO = false;
    }

    public static void renderFaceZNegAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        int zGrass = (renderer.renderMinZ <= 0) ? z - 1 : z;

        boolean blocksGrassXZPN = !renderer.blockAccess.getBlock(x + 1, y, zGrass).getCanBlockGrass();
        boolean blocksGrassXZNN = !renderer.blockAccess.getBlock(x - 1, y, zGrass).getCanBlockGrass();
        boolean blocksGrassYZPN = !renderer.blockAccess.getBlock(x, y + 1, zGrass).getCanBlockGrass();
        boolean blocksGrassYZNN = !renderer.blockAccess.getBlock(x, y - 1, zGrass).getCanBlockGrass();

        if (renderer.renderMinZ > 0)
            setupAOBrightnessZNeg(renderer, block, x, y, z, blocksGrassXZPN, blocksGrassXZNN, blocksGrassYZPN, blocksGrassYZNN);

        setupAOBrightnessZPos(renderer, block, x, y, z - 1, blocksGrassXZPN, blocksGrassXZNN, blocksGrassYZPN, blocksGrassYZNN);

        float zClamp = MathHelper.clamp_float((float)renderer.renderMinZ, 0, 1);
        mixAOBrightnessLightValueZ(zClamp, 1 - zClamp);

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMinZ <= 0.0D || !renderer.blockAccess.getBlock(x, y, z - 1).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
        float aoXYZNPN = (aoLightValueScratchXZNI + aoLightValueScratchXYZNPI + aoOpposingBlock + aoLightValueScratchYZPI) / 4.0F;
        float aoXYZPPN = (aoOpposingBlock + aoLightValueScratchYZPI + aoLightValueScratchXZPI + aoLightValueScratchXYZPPI) / 4.0F;
        float aoXYZPNN = (aoLightValueScratchYZNI + aoOpposingBlock + aoLightValueScratchXYZPNI + aoLightValueScratchXZPI) / 4.0F;
        float aoXYZNNN = (aoLightValueScratchXYZNNI + aoLightValueScratchXZNI + aoLightValueScratchYZNI + aoOpposingBlock) / 4.0F;

        float aoTL = (float)((double)aoXYZNPN * renderer.renderMaxY * (1.0D - renderer.renderMinX) + (double)aoXYZPPN * renderer.renderMaxY * renderer.renderMinX + (double)aoXYZPNN * (1.0D - renderer.renderMaxY) * renderer.renderMinX + (double)aoXYZNNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
        float aoBL = (float)((double)aoXYZNPN * renderer.renderMaxY * (1.0D - renderer.renderMaxX) + (double)aoXYZPPN * renderer.renderMaxY * renderer.renderMaxX + (double)aoXYZPNN * (1.0D - renderer.renderMaxY) * renderer.renderMaxX + (double)aoXYZNNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
        float aoBR = (float)((double)aoXYZNPN * renderer.renderMinY * (1.0D - renderer.renderMaxX) + (double)aoXYZPPN * renderer.renderMinY * renderer.renderMaxX + (double)aoXYZPNN * (1.0D - renderer.renderMinY) * renderer.renderMaxX + (double)aoXYZNNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
        float aoTR = (float)((double)aoXYZNPN * renderer.renderMinY * (1.0D - renderer.renderMinX) + (double)aoXYZPPN * renderer.renderMinY * renderer.renderMinX + (double)aoXYZPNN * (1.0D - renderer.renderMinY) * renderer.renderMinX + (double)aoXYZNNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));

        int brXYZNPN = renderer.getAoBrightness(aoBrightnessXZNI, aoBrightnessXYZNPI, aoBrightnessYZPI, blockBrightness);
        int brXYZPPN = renderer.getAoBrightness(aoBrightnessYZPI, aoBrightnessXZPI, aoBrightnessXYZPPI, blockBrightness);
        int brXYZPNN = renderer.getAoBrightness(aoBrightnessYZNI, aoBrightnessXYZPNI, aoBrightnessXZPI, blockBrightness);
        int brXYZNNN = renderer.getAoBrightness(aoBrightnessXYZNNI, aoBrightnessXZNI, aoBrightnessYZNI, blockBrightness);

        renderer.brightnessTopLeft = renderer.mixAoBrightness(brXYZNPN, brXYZPPN, brXYZPNN, brXYZNNN, renderer.renderMaxY * (1.0D - renderer.renderMinX), renderer.renderMaxY * renderer.renderMinX, (1.0D - renderer.renderMaxY) * renderer.renderMinX, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
        renderer.brightnessBottomLeft = renderer.mixAoBrightness(brXYZNPN, brXYZPPN, brXYZPNN, brXYZNNN, renderer.renderMaxY * (1.0D - renderer.renderMaxX), renderer.renderMaxY * renderer.renderMaxX, (1.0D - renderer.renderMaxY) * renderer.renderMaxX, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
        renderer.brightnessBottomRight = renderer.mixAoBrightness(brXYZNPN, brXYZPPN, brXYZPNN, brXYZNNN, renderer.renderMinY * (1.0D - renderer.renderMaxX), renderer.renderMinY * renderer.renderMaxX, (1.0D - renderer.renderMinY) * renderer.renderMaxX, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
        renderer.brightnessTopRight = renderer.mixAoBrightness(brXYZNPN, brXYZPPN, brXYZPNN, brXYZNNN, renderer.renderMinY * (1.0D - renderer.renderMinX), renderer.renderMinY * renderer.renderMinX, (1.0D - renderer.renderMinY) * renderer.renderMinX, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceZNeg(block, (double)x, (double)y, (double)z, icon);
        renderer.enableAO = false;
    }



    public static void renderFaceZPosAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        int zGrass = (renderer.renderMaxZ >= 1) ? z + 1 : z;

        boolean blocksGrassXZPP = !renderer.blockAccess.getBlock(x + 1, y, zGrass).getCanBlockGrass();
        boolean blocksGrassXZNP = !renderer.blockAccess.getBlock(x - 1, y, zGrass).getCanBlockGrass();
        boolean blocksGrassYZPP = !renderer.blockAccess.getBlock(x, y + 1, zGrass).getCanBlockGrass();
        boolean blocksGrassYZNP = !renderer.blockAccess.getBlock(x, y - 1, zGrass).getCanBlockGrass();

        if (renderer.renderMaxZ < 1)
            setupAOBrightnessZPos(renderer, block, x, y, z, blocksGrassXZPP, blocksGrassXZNP, blocksGrassYZPP, blocksGrassYZNP);

        setupAOBrightnessZNeg(renderer, block, x, y, z + 1, blocksGrassXZPP, blocksGrassXZNP, blocksGrassYZPP, blocksGrassYZNP);

        float zClamp = MathHelper.clamp_float((float)renderer.renderMaxZ, 0, 1);
        mixAOBrightnessLightValueZ(zClamp, 1 - zClamp);

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMaxZ >= 1.0D || !renderer.blockAccess.getBlock(x, y, z + 1).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
        float aoXYZNPP = (aoLightValueScratchXZNI + aoLightValueScratchXYZNPI + aoOpposingBlock + aoLightValueScratchYZPI) / 4.0F;
        float aoXYZPPP = (aoOpposingBlock + aoLightValueScratchYZPI + aoLightValueScratchXZPI + aoLightValueScratchXYZPPI) / 4.0F;
        float aoXYZPNP = (aoLightValueScratchYZNI + aoOpposingBlock + aoLightValueScratchXYZPNI + aoLightValueScratchXZPI) / 4.0F;
        float aoXYZNNP = (aoLightValueScratchXYZNNI + aoLightValueScratchXZNI + aoLightValueScratchYZNI + aoOpposingBlock) / 4.0F;

        float aoTL = (float)((double)aoXYZNPP * renderer.renderMaxY * (1.0D - renderer.renderMinX) + (double)aoXYZPPP * renderer.renderMaxY * renderer.renderMinX + (double)aoXYZPNP * (1.0D - renderer.renderMaxY) * renderer.renderMinX + (double)aoXYZNNP * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
        float aoBL = (float)((double)aoXYZNPP * renderer.renderMinY * (1.0D - renderer.renderMinX) + (double)aoXYZPPP * renderer.renderMinY * renderer.renderMinX + (double)aoXYZPNP * (1.0D - renderer.renderMinY) * renderer.renderMinX + (double)aoXYZNNP * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
        float aoBR = (float)((double)aoXYZNPP * renderer.renderMinY * (1.0D - renderer.renderMaxX) + (double)aoXYZPPP * renderer.renderMinY * renderer.renderMaxX + (double)aoXYZPNP * (1.0D - renderer.renderMinY) * renderer.renderMaxX + (double)aoXYZNNP * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
        float aoTR = (float)((double)aoXYZNPP * renderer.renderMaxY * (1.0D - renderer.renderMaxX) + (double)aoXYZPPP * renderer.renderMaxY * renderer.renderMaxX + (double)aoXYZPNP * (1.0D - renderer.renderMaxY) * renderer.renderMaxX + (double)aoXYZNNP * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));

        int brXYZNPP = renderer.getAoBrightness(aoBrightnessXZNI, aoBrightnessXYZNPI, aoBrightnessYZPI, blockBrightness);
        int brXYZPPP = renderer.getAoBrightness(aoBrightnessYZPI, aoBrightnessXZPI, aoBrightnessXYZPPI, blockBrightness);
        int brXYZPNP = renderer.getAoBrightness(aoBrightnessYZNI, aoBrightnessXYZPNI, aoBrightnessXZPI, blockBrightness);
        int brXYZNNP = renderer.getAoBrightness(aoBrightnessXYZNNI, aoBrightnessXZNI, aoBrightnessYZNI, blockBrightness);

        renderer.brightnessTopLeft = renderer.mixAoBrightness(brXYZNPP, brXYZNNP, brXYZPNP, brXYZPPP, renderer.renderMaxY * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMaxY) * renderer.renderMinX, renderer.renderMaxY * renderer.renderMinX);
        renderer.brightnessBottomLeft = renderer.mixAoBrightness(brXYZNPP, brXYZNNP, brXYZPNP, brXYZPPP, renderer.renderMinY * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX), (1.0D - renderer.renderMinY) * renderer.renderMinX, renderer.renderMinY * renderer.renderMinX);
        renderer.brightnessBottomRight = renderer.mixAoBrightness(brXYZNPP, brXYZNNP, brXYZPNP, brXYZPPP, renderer.renderMinY * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMinY) * renderer.renderMaxX, renderer.renderMinY * renderer.renderMaxX);
        renderer.brightnessTopRight = renderer.mixAoBrightness(brXYZNPP, brXYZNNP, brXYZPNP, brXYZPPP, renderer.renderMaxY * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX), (1.0D - renderer.renderMaxY) * renderer.renderMaxX, renderer.renderMaxY * renderer.renderMaxX);

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.8F;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.8F;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.8F;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, icon);
        renderer.enableAO = false;
    }

    public static void renderFaceXNegAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        int xGrass = (renderer.renderMinX <= 0) ? x - 1 : x;

        boolean blocksGrassXYNP = !renderer.blockAccess.getBlock(xGrass, y + 1, z).getCanBlockGrass();
        boolean blocksGrassXYNN = !renderer.blockAccess.getBlock(xGrass, y - 1, z).getCanBlockGrass();
        boolean blocksGrassXZNN = !renderer.blockAccess.getBlock(xGrass, y, z - 1).getCanBlockGrass();
        boolean blocksGrassXZNP = !renderer.blockAccess.getBlock(xGrass, y, z + 1).getCanBlockGrass();

        if (renderer.renderMinX > 0)
            setupAOBrightnessXNeg(renderer, block, x, y, z, blocksGrassXYNP, blocksGrassXYNN, blocksGrassXZNN, blocksGrassXZNP);

        setupAOBrightnessXPos(renderer, block, x - 1, y, z, blocksGrassXYNP, blocksGrassXYNN, blocksGrassXZNN, blocksGrassXZNP);

        float xClamp = MathHelper.clamp_float((float)renderer.renderMinX, 0, 1);
        mixAOBrightnessLightValueX(xClamp, 1 - xClamp);

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMinX <= 0.0D || !renderer.blockAccess.getBlock(x - 1, y, z).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
        float aoXYZNNP = (aoLightValueScratchXYIN + aoLightValueScratchXYZINP + aoOpposingBlock + aoLightValueScratchXZIP) / 4.0F;
        float aoXYZNPP = (aoOpposingBlock + aoLightValueScratchXZIP + aoLightValueScratchXYIP + aoLightValueScratchXYZIPP) / 4.0F;
        float aoXYZNPN = (aoLightValueScratchXZIN + aoOpposingBlock + aoLightValueScratchXYZIPN + aoLightValueScratchXYIP) / 4.0F;
        float aoXYZNNN = (aoLightValueScratchXYZINN + aoLightValueScratchXYIN + aoLightValueScratchXZIN + aoOpposingBlock) / 4.0F;

        float aoTL = (float)((double)aoXYZNPP * renderer.renderMaxY * renderer.renderMaxZ + (double)aoXYZNPN * renderer.renderMaxY * (1.0D - renderer.renderMaxZ) + (double)aoXYZNNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ) + (double)aoXYZNNP * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
        float aoBL = (float)((double)aoXYZNPP * renderer.renderMaxY * renderer.renderMinZ + (double)aoXYZNPN * renderer.renderMaxY * (1.0D - renderer.renderMinZ) + (double)aoXYZNNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ) + (double)aoXYZNNP * (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
        float aoBR = (float)((double)aoXYZNPP * renderer.renderMinY * renderer.renderMinZ + (double)aoXYZNPN * renderer.renderMinY * (1.0D - renderer.renderMinZ) + (double)aoXYZNNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ) + (double)aoXYZNNP * (1.0D - renderer.renderMinY) * renderer.renderMinZ);
        float aoTR = (float)((double)aoXYZNPP * renderer.renderMinY * renderer.renderMaxZ + (double)aoXYZNPN * renderer.renderMinY * (1.0D - renderer.renderMaxZ) + (double)aoXYZNNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ) + (double)aoXYZNNP * (1.0D - renderer.renderMinY) * renderer.renderMaxZ);

        int brXYZNNP = renderer.getAoBrightness(aoBrightnessXYIN, aoBrightnessXYZINP, aoBrightnessXZIP, blockBrightness);
        int brXYZNPP = renderer.getAoBrightness(aoBrightnessXZIP, aoBrightnessXYIP, aoBrightnessXYZIPP, blockBrightness);
        int brXYZNPN = renderer.getAoBrightness(aoBrightnessXZIN, aoBrightnessXYZIPN, aoBrightnessXYIP, blockBrightness);
        int brXYZNNN = renderer.getAoBrightness(aoBrightnessXYZINN, aoBrightnessXYIN, aoBrightnessXZIN, blockBrightness);

        renderer.brightnessTopLeft = renderer.mixAoBrightness(brXYZNPP, brXYZNPN, brXYZNNN, brXYZNNP, renderer.renderMaxY * renderer.renderMaxZ, renderer.renderMaxY * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
        renderer.brightnessBottomLeft = renderer.mixAoBrightness(brXYZNPP, brXYZNPN, brXYZNNN, brXYZNNP, renderer.renderMaxY * renderer.renderMinZ, renderer.renderMaxY * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
        renderer.brightnessBottomRight = renderer.mixAoBrightness(brXYZNPP, brXYZNPN, brXYZNNN, brXYZNNP, renderer.renderMinY * renderer.renderMinZ, renderer.renderMinY * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ), (1.0D - renderer.renderMinY) * renderer.renderMinZ);
        renderer.brightnessTopRight = renderer.mixAoBrightness(brXYZNPP, brXYZNPN, brXYZNNN, brXYZNNP, renderer.renderMinY * renderer.renderMaxZ, renderer.renderMinY * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ), (1.0D - renderer.renderMinY) * renderer.renderMaxZ);

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.enableAO = false;
    }

    public static void renderFaceXPosAOPartial (RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon, float r, float g, float b) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        int xGrass = (renderer.renderMaxX >= 1) ? x + 1 : x;

        boolean blocksGrassXYNP = !renderer.blockAccess.getBlock(xGrass, y + 1, z).getCanBlockGrass();
        boolean blocksGrassXYNN = !renderer.blockAccess.getBlock(xGrass, y - 1, z).getCanBlockGrass();
        boolean blocksGrassXZNN = !renderer.blockAccess.getBlock(xGrass, y, z - 1).getCanBlockGrass();
        boolean blocksGrassXZNP = !renderer.blockAccess.getBlock(xGrass, y, z + 1).getCanBlockGrass();

        if (renderer.renderMaxX < 1)
            setupAOBrightnessXPos(renderer, block, x, y, z, blocksGrassXYNP, blocksGrassXYNN, blocksGrassXZNN, blocksGrassXZNP);

        setupAOBrightnessXNeg(renderer, block, x + 1, y, z, blocksGrassXYNP, blocksGrassXYNN, blocksGrassXZNN, blocksGrassXZNP);

        float xClamp = MathHelper.clamp_float((float)renderer.renderMaxX, 0, 1);
        mixAOBrightnessLightValueX(xClamp, 1 - xClamp);

        int blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        if (renderer.renderMaxX >= 1.0D || !renderer.blockAccess.getBlock(x + 1, y, z).isOpaqueCube())
            blockBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);

        float aoOpposingBlock = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
        float aoXYZPNP = (aoLightValueScratchXYIN + aoLightValueScratchXYZINP + aoOpposingBlock + aoLightValueScratchXZIP) / 4.0F;
        float aoXYZPNN = (aoLightValueScratchXYZINN + aoLightValueScratchXYIN + aoLightValueScratchXZIN + aoOpposingBlock) / 4.0F;
        float aoXYZPPN = (aoLightValueScratchXZIN + aoOpposingBlock + aoLightValueScratchXYZIPN + aoLightValueScratchXYIP) / 4.0F;
        float aoXYZPPP = (aoOpposingBlock + aoLightValueScratchXZIP + aoLightValueScratchXYIP + aoLightValueScratchXYZIPP) / 4.0F;

        float aoTL = (float)((double)aoXYZPNP * (1.0D - renderer.renderMinY) * renderer.renderMaxZ + (double)aoXYZPNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ) + (double)aoXYZPPN * renderer.renderMinY * (1.0D - renderer.renderMaxZ) + (double)aoXYZPPP * renderer.renderMinY * renderer.renderMaxZ);
        float aoBL = (float)((double)aoXYZPNP * (1.0D - renderer.renderMinY) * renderer.renderMinZ + (double)aoXYZPNN * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ) + (double)aoXYZPPN * renderer.renderMinY * (1.0D - renderer.renderMinZ) + (double)aoXYZPPP * renderer.renderMinY * renderer.renderMinZ);
        float aoBR = (float)((double)aoXYZPNP * (1.0D - renderer.renderMaxY) * renderer.renderMinZ + (double)aoXYZPNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ) + (double)aoXYZPPN * renderer.renderMaxY * (1.0D - renderer.renderMinZ) + (double)aoXYZPPP * renderer.renderMaxY * renderer.renderMinZ);
        float aoTR = (float)((double)aoXYZPNP * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ + (double)aoXYZPNN * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ) + (double)aoXYZPPN * renderer.renderMaxY * (1.0D - renderer.renderMaxZ) + (double)aoXYZPPP * renderer.renderMaxY * renderer.renderMaxZ);

        int brXYZPNP = renderer.getAoBrightness(aoBrightnessXYIN, aoBrightnessXYZINP, aoBrightnessXZIP, blockBrightness);
        int brXYZPNN = renderer.getAoBrightness(aoBrightnessXZIP, aoBrightnessXYIP, aoBrightnessXYZIPP, blockBrightness);
        int brXYZPPN = renderer.getAoBrightness(aoBrightnessXZIN, aoBrightnessXYZIPN, aoBrightnessXYIP, blockBrightness);
        int brXYZPPP = renderer.getAoBrightness(aoBrightnessXYZINN, aoBrightnessXYIN, aoBrightnessXZIN, blockBrightness);

        renderer.brightnessTopLeft = renderer.mixAoBrightness(brXYZPNP, brXYZPPP, brXYZPPN, brXYZPNN, (1.0D - renderer.renderMinY) * renderer.renderMaxZ, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ), renderer.renderMinY * (1.0D - renderer.renderMaxZ), renderer.renderMinY * renderer.renderMaxZ);
        renderer.brightnessBottomLeft = renderer.mixAoBrightness(brXYZPNP, brXYZPPP, brXYZPPN, brXYZPNN, (1.0D - renderer.renderMinY) * renderer.renderMinZ, (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ), renderer.renderMinY * (1.0D - renderer.renderMinZ), renderer.renderMinY * renderer.renderMinZ);
        renderer.brightnessBottomRight = renderer.mixAoBrightness(brXYZPNP, brXYZPPP, brXYZPPN, brXYZPNN, (1.0D - renderer.renderMaxY) * renderer.renderMinZ, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ), renderer.renderMaxY * (1.0D - renderer.renderMinZ), renderer.renderMaxY * renderer.renderMinZ);
        renderer.brightnessTopRight = renderer.mixAoBrightness(brXYZPNP, brXYZPPP, brXYZPPN, brXYZPNN, (1.0D - renderer.renderMaxY) * renderer.renderMaxZ, (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ), renderer.renderMaxY * (1.0D - renderer.renderMaxZ), renderer.renderMaxY * renderer.renderMaxZ);

        renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = r * 0.6F;
        renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = g * 0.6F;
        renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = b * 0.6F;

        renderer.colorRedTopLeft *= aoTL;
        renderer.colorGreenTopLeft *= aoTL;
        renderer.colorBlueTopLeft *= aoTL;
        renderer.colorRedBottomLeft *= aoBL;
        renderer.colorGreenBottomLeft *= aoBL;
        renderer.colorBlueBottomLeft *= aoBL;
        renderer.colorRedBottomRight *= aoBR;
        renderer.colorGreenBottomRight *= aoBR;
        renderer.colorBlueBottomRight *= aoBR;
        renderer.colorRedTopRight *= aoTR;
        renderer.colorGreenTopRight *= aoTR;
        renderer.colorBlueTopRight *= aoTR;

        renderer.enableAO = true;
        renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, icon);
        renderer.enableAO = false;
    }

    public static int mixAOBrightness (int brightTL, int brightBL, int brightBR, int brightTR, double lerpTB, double lerpLR)
    {
        double brightSkyL = (brightTL >> 16 & 255) * (1 - lerpTB) + (brightBL >> 16 & 255) * lerpTB;
        double brightSkyR = (brightTR >> 16 & 255) * (1 - lerpTB) + (brightBR >> 16 & 255) * lerpTB;
        int brightSky = (int)(brightSkyL * (1 - lerpLR) + brightSkyR * lerpLR) & 255;

        double brightBlkL = (brightTL & 255) * (1 - lerpTB) + (brightBL & 255) * lerpTB;
        double brightBlkR = (brightTR & 255) * (1 - lerpTB) + (brightBR & 255) * lerpTB;
        int brightBlk = (int)(brightBlkL * (1 - lerpLR) + brightBlkR * lerpLR) & 255;

        return brightSky << 16 | brightBlk;
    }

    public static int mixAOBrightness (int brightMin, int brightMax, float fMin, float fMax) {
        if (brightMin == 0)
            return 0;
        if (brightMax == 0)
            return 0;

        float brightSky = (brightMin >> 16 & 255) * fMin + (brightMax >> 16 & 255) * fMax;
        float brightBlk = (brightMin & 255) * fMin + (brightMax & 255) * fMax;

        return ((int)brightSky & 255) << 16 | ((int)brightBlk & 255);
    }*/
}
