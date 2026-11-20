package com.clauclient;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<Module>();
    public static Module sprint, fullbright, hitFix, hurt, zoom, persp, oldAnim, crosshair, sidebar, potion;

    public static Module get(String name) {
        for (Module m : modules) {
            if (m.name.equals(name)) return m;
        }
        return null;
    }

    private static <T extends Module> T add(T m) {
        modules.add(m);
        return m;
    }

    static {
        sprint = add(new Module("Sprint") {
            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null && mc.thePlayer.moveForward > 0
                        && !mc.thePlayer.isSneaking()
                        && !mc.thePlayer.isCollidedHorizontally) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        });

        fullbright = add(new Module("Fullbright") {
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

        hitFix = add(new Module("Hit Delay Fix", true) {});
        hurt = add(new Module("No Hurt Cam") {});

        zoom = add(new Module("Zoom") {
            Setting z = num("Zoom", 4, 1.5, 30, 0.5);
        });

        persp = add(new Module("Perspectiva") {});

        oldAnim = add(new Module("Old Animations") {
            Setting sneak = bool("Sneak suave", true);
            Setting vel = num("Velocidade do sneak", 8, 1, 30, 1);
        });

        crosshair = add(new Module("Crosshair") {
            Setting cor = num("Cor", 0, 0, 7, 1);
            Setting escala = num("Escala", 1, 1, 4, 1);
            Setting contorno = bool("Contorno", true);
            Setting desenhar = action("Desenhar crosshair", new Runnable() {
                public void run() {
                    Minecraft mc = Minecraft.getMinecraft();
                    mc.displayGuiScreen(new CrosshairGui(mc.currentScreen));
                }
            });
        });

        sidebar = add(new SidebarHud());
        sidebar.enabled = false;

        potion = add(new PotionHud());

        add(new TextHud("FPS", 4, 4) {
            public String getValue() {
                return String.valueOf(Minecraft.getDebugFPS());
            }
        });

        add(new TextHud("CPS", 4, 22) {
            Setting both = bool("Mostrar direito", true);
            public String getValue() {
                int l = Cps.count(Cps.left);
                if (!both.bool) return String.valueOf(l);
                return l + " | " + Cps.count(Cps.right);
            }
        });

        add(new TextHud("Coordenadas", 4, 40) {
            public String getValue() {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer == null) return "-";
                return String.format("%.0f %.0f %.0f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
        });
    }
}