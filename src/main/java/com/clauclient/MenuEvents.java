package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MenuEvents {

    @SubscribeEvent
    public void onOpen(GuiOpenEvent e) {
        if (e.gui instanceof GuiMainMenu) {
            e.gui = new ClauMainMenu();
        }
    }

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent.Post e) {
        GuiScreen g = e.gui;
        boolean world = g instanceof GuiDownloadTerrain;
        boolean conn = g instanceof GuiConnecting;
        if (!world && !conn) return;

        int w = g.width, h = g.height;
        Minecraft mc = Minecraft.getMinecraft();
        Logo.background(w, h);
        Logo.drawCrown(w / 2, h / 2 - 90, 2);
        Logo.draw(w / 2, h / 2 - 74, 3f);

        String msg = world ? "Carregando o mundo..." : "Conectando...";
        mc.fontRendererObj.drawStringWithShadow(msg, (w - mc.fontRendererObj.getStringWidth(msg)) / 2, h / 2 - 8, 0xFFFFFFFF);

        int bw = Math.min(200, w - 40);
        int bx = (w - bw) / 2;
        int by = h / 2 + 6;
        Gui.drawRect(bx, by, bx + bw, by + 10, 0xFF7C3AED);
        Gui.drawRect(bx + 1, by + 1, bx + bw - 1, by + 9, 0xFF0C0C12);
        float t = (System.currentTimeMillis() % 1800L) / 1800f;
        int seg = (bw - 2) / 3;
        int start = bx + 1 + (int) ((bw - 2 - seg) * t);
        Gui.drawRect(start, by + 1, start + seg, by + 9, Logo.PURPLE);

        String tip = "Dica: Jogue com o Clau Client e tenha uma experiencia ainda melhor!";
        java.util.List<String> lines = mc.fontRendererObj.listFormattedStringToWidth(tip, bw);
        int ty = by + 20;
        for (String s : lines) {
            mc.fontRendererObj.drawStringWithShadow(s, (w - mc.fontRendererObj.getStringWidth(s)) / 2, ty, 0xFFCCCCCC);
            ty += 10;
        }
    }
}
