package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class HudModule extends Module {
    public int x, y;

    public HudModule(String name, int x, int y) {
        super(name, true);
        this.x = x;
        this.y = y;
    }

    public abstract String getText();

    public int getWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(getText()) + 8;
    }

    public int getHeight() {
        return 14;
    }

    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        Gui.drawRect(x, y, x + getWidth(), y + getHeight(), 0x90000000);
        mc.fontRendererObj.drawStringWithShadow(getText(), x + 4, y + 3, 0xFFFFFF);
    }
}
