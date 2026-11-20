package com.clauclient;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionHud extends HudModule {
    private static final ResourceLocation INV = new ResourceLocation("textures/gui/container/inventory.png");
    private static final Gui GUI = new Gui();
    public final Setting icons = bool("Icones", true);
    public final Setting dur = bool("Mostrar duracao", true);

    private static class Row {
        String name, dur;
        int icon;
        Row(String n, String d, int i) { name = n; dur = d; icon = i; }
    }

    public PotionHud() {
        super("Potion Status", 4, 70);
    }

    private List<Row> rows() {
        List<Row> r = new ArrayList<Row>();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            for (PotionEffect e : mc.thePlayer.getActivePotionEffects()) {
                int a = e.getAmplifier();
                String lvl = "";
                if (a >= 1 && a <= 9) lvl = " " + I18n.format("enchantment.level." + (a + 1));
                else if (a > 9) lvl = " " + (a + 1);
                int icon = -1;
                int id = e.getPotionID();
                if (id >= 0 && id < Potion.potionTypes.length) {
                    Potion p = Potion.potionTypes[id];
                    if (p != null && p.hasStatusIcon()) icon = p.getStatusIconIndex();
                }
                r.add(new Row(I18n.format(e.getEffectName()) + lvl, Potion.getDurationString(e), icon));
            }
        }
        if (r.isEmpty() && mc.currentScreen instanceof HudEditor) {
            r.add(new Row("Speed II", "1:30", -1));
            r.add(new Row("Strength", "0:45", -1));
        }
        return r;
    }

    private int rowH() {
        return (icons.bool || dur.bool) ? 20 : 13;
    }

    public int contentWidth() {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int w = 1;
        int off = icons.bool ? 24 : 5;
        for (Row r : rows()) {
            int t = fr.getStringWidth(r.name);
            if (dur.bool) t = Math.max(t, fr.getStringWidth(r.dur));
            w = Math.max(w, off + t + 6);
        }
        return w;
    }

    public int contentHeight() {
        int n = rows().size();
        return n == 0 ? 1 : n * rowH();
    }

    public void drawContent() {
        List<Row> rs = rows();
        if (rs.isEmpty()) return;
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        int w = contentWidth();
        int rh = rowH();
        drawBackground(w, rs.size() * rh);
        int off = icons.bool ? 24 : 5;
        for (int i = 0; i < rs.size(); i++) {
            Row r = rs.get(i);
            int ry = i * rh;
            if (icons.bool && r.icon >= 0) {
                mc.getTextureManager().bindTexture(INV);
                GlStateManager.color(1, 1, 1, 1);
                GUI.drawTexturedModalRect(4, ry + 1, r.icon % 8 * 18, 198 + r.icon / 8 * 18, 18, 18);
            }
            int ty = dur.bool ? ry + 2 : ry + (rh - 8) / 2;
            fr.drawStringWithShadow(r.name, off, ty, 0xFFFFFFFF);
            if (dur.bool) fr.drawStringWithShadow(r.dur, off, ry + 11, 0xFFAAAAAA);
        }
    }
                       }
