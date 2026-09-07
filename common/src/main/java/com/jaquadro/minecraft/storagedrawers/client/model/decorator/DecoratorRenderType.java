package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public enum DecoratorRenderType
{
    SOLID,
    CUTOUT,
    TRANSLUCENT;

    public static DecoratorRenderType fromItemType (ChunkSectionLayer renderType) {
        if (renderType == null)
            return null;

        return switch (renderType) {
            case SOLID -> DecoratorRenderType.SOLID;
            case CUTOUT -> DecoratorRenderType.CUTOUT;
            case TRANSLUCENT -> DecoratorRenderType.TRANSLUCENT;
            default -> null;
        };
    }

    public static DecoratorRenderType fromItemType (RenderType renderType) {
        // 26.1.2 merged the solid item sheet into the cutout one; SOLID geometry renders via cutout.
        if (renderType == Sheets.cutoutBlockSheet())
            return DecoratorRenderType.CUTOUT;
        if (renderType == Sheets.translucentBlockItemSheet())
            return DecoratorRenderType.TRANSLUCENT;
        return null;
    }

    public static ChunkSectionLayer toChunkType (DecoratorRenderType renderType) {
        if (renderType == null)
            return null;

        return switch (renderType) {
            case SOLID -> ChunkSectionLayer.SOLID;
            case CUTOUT -> ChunkSectionLayer.CUTOUT;
            case TRANSLUCENT -> ChunkSectionLayer.TRANSLUCENT;
        };
    }

    public static RenderType toItemType (DecoratorRenderType renderType) {
        if (renderType == null)
            return null;

        return switch (renderType) {
            case SOLID -> Sheets.cutoutBlockSheet();
            case CUTOUT -> Sheets.cutoutBlockSheet();
            case TRANSLUCENT -> Sheets.translucentBlockItemSheet();
        };
    }
}
