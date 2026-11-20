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
            if (m instanceof HudModule) {
                HudModule h = (HudModule) m;
                p.setProperty(h.name + ".x", String.valueOf(h.x));
                p.setProperty(h.name + ".y", String.valueOf(h.y));
                p.setProperty(h.name + ".on", String.valueOf(h.enabled));
            }
        }
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
            for (Module m : ModuleManager.modules) {
                if (m instanceof HudModule) {
                    HudModule h = (HudModule) m;
                    h.x = Integer.parseInt(p.getProperty(h.name + ".x", String.valueOf(h.x)));
                    h.y = Integer.parseInt(p.getProperty(h.name + ".y", String.valueOf(h.y)));
                    h.enabled = Boolean.parseBoolean(p.getProperty(h.name + ".on", "true"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
