package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class Ui {
    public static final int[] PALETTE = {0xFFFFFF, 0xFF5555, 0x55FF55, 0x5555FF, 0x55FFFF, 0xFFFF55, 0xFF55FF, 0xFFAA00, 0x000000, 0x9B30FF};
    public static final int ACCENT = 0xFFA66BFF;

    public static int color(double i) {
        int k = (int) Math.round(i);
        if (k < 0) k = 0;
        if (k >= PALETTE.length) k = PALETTE.length - 1;
        return PALETTE[k];
    }

    public static void scissor(int x, int y, int w, int h) {
        Minecraft mc = Minecraft.getMinecraft();
        int f = new ScaledResolution(mc).getScaleFactor();
        if (h < 0) h = 0;
        if (w < 0) w = 0;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * f, mc.displayHeight - (y + h) * f, w * f, h * f);
    }

    public static void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}