package com.clauclient;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<Module>();

    public static Module get(String name) {
        for (Module m : modules) {
            if (m.name.equals(name)) return m;
        }
        return null;
    }

    static {
        modules.add(new Module("Sprint") {
            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null && mc.thePlayer.moveForward > 0
                        && !mc.thePlayer.isSneaking()
                        && !mc.thePlayer.isCollidedHorizontally) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        });

        modules.add(new Module("Fullbright") {
            float old = 1.0f;
            public void onEnable() {
                old = Minecraft.getMinecraft().gameSettings.gammaSetting;
            }
            public void onTick() {
                Minecraft.getMinecraft().gameSettings.gammaSetting = 100f;
            }
            public void onDisable() {
                Minecraft.getMinecraft().gameSettings.gammaSetting = old;
            }
        });

        modules.add(new Module("FPS", true) {});
        modules.add(new Module("Coordenadas", true) {});
    }
}
