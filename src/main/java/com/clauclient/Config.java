package com.clauclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public class Config {
    private static File file() {
        return new File(Minecraft.getMinecraft().mcDataDir, "clauclient.properties");
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : ModuleManager.modules) {
            p.setProperty(m.name + ".on", String.valueOf(m.enabled));
            if (m instanceof HudModule) {
                HudModule h = (HudModule) m;
                p.setProperty(m.name + ".x", String.valueOf(h.x));
                p.setProperty(m.name + ".y", String.valueOf(h.y));
            }
            for (Setting s : m.settings) {
                String key = m.name + "." + s.name;
                if (s.type == Setting.BOOL) p.setProperty(key, String.valueOf(s.bool));
                else if (s.type == Setting.NUM) p.setProperty(key, String.valueOf(s.num));
                else if (s.type == Setting.TEXT) p.setProperty(key, s.text);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (boolean b : CrosshairData.GRID) sb.append(b ? '1' : '0');
        p.setProperty("Crosshair.grid", sb.toString());
        try {
            FileOutputStream out = new FileOutputStream(file());
            p.store(out, "ClauClient");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File f = file();
        if (!f.exists()) return;
        Properties p = new Properties();
        try {
            FileInputStream in = new FileInputStream(f);
            p.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (Module m : ModuleManager.modules) {
            try {
                String on = p.getProperty(m.name + ".on");
                if (on != null) m.enabled = Boolean.parseBoolean(on);
                if (m instanceof HudModule) {
                    HudModule h = (HudModule) m;
                    h.x = Integer.parseInt(p.getProperty(m.name + ".x", String.valueOf(h.x)));
                    h.y = Integer.parseInt(p.getProperty(m.name + ".y", String.valueOf(h.y)));
                }
                for (Setting s : m.settings) {
                    String v = p.getProperty(m.name + "." + s.name);
                    if (v == null) continue;
                    if (s.type == Setting.BOOL) s.bool = Boolean.parseBoolean(v);
                    else if (s.type == Setting.NUM) s.num = Double.parseDouble(v);
                    else if (s.type == Setting.TEXT) s.text = v;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String g = p.getProperty("Crosshair.grid");
        if (g != null && g.length() == CrosshairData.GRID.length) {
            for (int i = 0; i < g.length(); i++) CrosshairData.GRID[i] = g.charAt(i) == '1';
        }
    }
}