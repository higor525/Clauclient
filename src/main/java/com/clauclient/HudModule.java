package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public abstract class HudModule extends Module {
    public int x, y;
    public final Setting bg = bool("Fundo", true);
    public final Setting bar = bool("Barra colorida", true);
    public final Setting barColor = num("Cor da barra", 4, 0, 7, 1);
    public final Setting scale = num("Escala", 1.0, 0.5, 3.0, 0.1);

    public HudModule(String name, int x, int y) {
        super(name, true);
        this.x = x;
        this.y = y;
    }

    public abstract int contentWidth();
    public abstract int contentHeight();
    public abstract void drawContent();

    public int getWidth() {
        return Math.max(1, (int) (contentWidth() * scale.num));
    }

    public int getHeight() {
        return Math.max(1, (int) (contentHeight() * scale.num));
    }

    protected void drawBackground(int w, int h) {
        if (bg.bool) Gui.drawRect(0, 0, w, h, 0x90000000);
        if (bar.bool) Gui.drawRect(0, 0, 2, h, 0xFF000000 | Ui.color(barColor.num));
    }

    public void render() {
        if (x < 0) {
            x = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - getWidth() - 4;
        }
        float s = (float) scale.num;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(s, s, 1);
        drawContent();
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }
}