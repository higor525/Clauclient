package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Logo {
    public static final int PURPLE = 0xFF9B30FF;

    private static final String[] CROWN = {
        "#...#...#",
        "##..#..##",
        "###.#.###",
        "#########",
        "#########",
        ".#######."
    };

    public static void drawCrown(int cx, int y, int px) {
        int w = CROWN[0].length() * px;
        int x0 = cx - w / 2;
        for (int r = 0; r < CROWN.length; r++) {
            for (int c = 0; c < CROWN[r].length(); c++) {
                if (CROWN[r].charAt(c) == '#') {
                    Gui.drawRect(x0 + c * px, y + r * px, x0 + (c + 1) * px, y + (r + 1) * px, PURPLE);
                }
            }
        }
    }

    public static void draw(int cx, int y, float scale) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        String a = "\u00a7oClau ";
        String b = "\u00a7oClient";
        int wa = fr.getStringWidth(a);
        int wb = fr.getStringWidth(b);
        float total = (wa + wb) * scale;
        GlStateManager.pushMatrix();
        GlStateManager.translate(cx - total / 2f, y, 0);
        GlStateManager.scale(scale, scale, 1f);
        fr.drawStringWithShadow(a, 0, 0, 0xFFFFFFFF);
        fr.drawStringWithShadow(b, wa, 0, PURPLE);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        int uy = y + (int) (10 * scale) + 3;
        int half = (int) (total / 2f * 0.8f);
        Gui.drawRect(cx - half, uy, cx + half, uy + 2, PURPLE);
    }

    public static void background(int w, int h) {
        int steps = Math.max(1, h);
        for (int i = 0; i < steps; i++) {
            float t = i / (float) steps;
            int r = (int) (10 + t * 55);
            int g = (int) (6 + t * 18);
            int b = (int) (24 + t * 50);
            Gui.drawRect(0, i, w, i + 1, 0xFF000000 | (r << 16) | (g << 8) | b);
        }
    }
              }
