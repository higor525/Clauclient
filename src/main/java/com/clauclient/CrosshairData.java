package com.clauclient;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class CrosshairData {
    public static final int N = 13;
    public static final boolean[] GRID = new boolean[N * N];

    static {
        reset();
    }

    public static void reset() {
        for (int i = 0; i < GRID.length; i++) GRID[i] = false;
        for (int k = 0; k < N; k++) {
            boolean arm = (k >= 2 && k <= 4) || (k >= 8 && k <= 10);
            if (arm) {
                GRID[6 * N + k] = true;
                GRID[k * N + 6] = true;
            }
        }
    }

    public static void draw(int sw, int sh, float scale, int color, boolean outline) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(sw / 2, sh / 2, 0);
        GlStateManager.scale(scale, scale, 1);
        if (outline) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    if (!GRID[r * N + c]) continue;
                    int x = c - 6, y = r - 6;
                    Gui.drawRect(x - 1, y, x, y + 1, 0xFF000000);
                    Gui.drawRect(x + 1, y, x + 2, y + 1, 0xFF000000);
                    Gui.drawRect(x, y - 1, x + 1, y, 0xFF000000);
                    Gui.drawRect(x, y + 1, x + 1, y + 2, 0xFF000000);
                }
            }
        }
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (GRID[r * N + c]) {
                    Gui.drawRect(c - 6, r - 6, c - 5, r - 5, color);
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }
}
