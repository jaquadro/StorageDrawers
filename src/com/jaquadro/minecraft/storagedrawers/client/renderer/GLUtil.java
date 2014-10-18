package com.jaquadro.minecraft.storagedrawers.client.renderer;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class GLUtil
{
    private GLUtil () { }

    public static List<int[]> makeGLState () {
        return new ArrayList<int[]>(8);
    }

    public static List<int[]> makeGLState (int[] saveList) {
        List<int[]> state = new ArrayList<int[]>(saveList.length);

        for (int i = 0, n = saveList.length; i < n; i++)
            state.add(new int[] { 0, 0 });

        return state;
    }

    public static List<int[]> saveGLState (int[] saveList) {
        List<int[]> state = makeGLState(saveList);
        saveGLStateCore(state, saveList);

        return state;
    }

    public static void saveGLState (List<int[]> state, int[] saveList) {
        if (state == null || saveList == null)
            return;

        for (int i = state.size(), n = saveList.length; i < n; i++)
            state.add(new int[2]);

        for (int i = 0, n = state.size(); i < n; i++)
            state.get(i)[0] = 0;

        saveGLStateCore(state, saveList);
    }

    private static void saveGLStateCore (List<int[]> state, int[] saveList) {
        for (int i = 0, n = saveList.length; i < n; i++) {
            int[] entry = state.get(i);
            entry[0] = saveList[i];
            entry[1] = GL11.glIsEnabled(entry[0]) ? 1 : 0;
        }
    }

    public static void restoreGLState (List<int[]> state) {
        if (state == null)
            return;

        for (int i = 0, n = state.size(); i < n; i++) {
            int[] entry = state.get(i);
            if (entry[0] == 0)
                break;

            if (entry[1] == 0)
                GL11.glDisable(entry[0]);
            else
                GL11.glEnable(entry[0]);
        }
    }
}
