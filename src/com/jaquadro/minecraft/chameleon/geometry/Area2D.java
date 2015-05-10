package com.jaquadro.minecraft.chameleon.geometry;

public class Area2D
{
    public static final Area2D EMPTY = new Area2D(0, 0, 0, 0);

    private double x;
    private double y;
    private double w;
    private double h;

    public Area2D (double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public double getX () {
        return x;
    }

    public double getY () {
        return y;
    }

    public double getWidth () {
        return w;
    }

    public double getHeight () {
        return h;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;

        Area2D that = (Area2D)obj;
        return x == that.x && y == that.y && w == that.w && h == that.h;
    }

    @Override
    public int hashCode () {
        int hash = 23;
        hash = hash * 31 + (int)x;
        hash = hash * 31 + (int)y;
        hash = hash * 31 + (int)w;
        hash = hash * 31 + (int)h;

        return hash;
    }

    public static Area2D From (double startX, double startY, double stopX, double stopY) {
        return new Area2D(startX, startY, stopX - startX, stopY - startY);
    }
}
