package com.clauclient;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<Module>();
    public static Module sprint, fullbright, hitFix, hurt, zoom, persp, oldAnim, crosshair, sidebar, potion, noPart, optim, plus, chat, render, blockOv, nametag, tab;

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

        persp = add(new Module("Perspectiva") {
            Setting hold = bool("Modo Hold", false);
        });

        oldAnim = add(new Module("Old Animations") {
            Setting sneak = bool("Sneak suave", true);
            Setting vel = num("Velocidade do sneak", 8, 1, 30, 1);
            Setting a1 = bool("1.7 Blockhit", true);
            Setting a2 = bool("1.7 Sword Animation", true);
            Setting a3 = bool("1.7 Rod Animation", true);
            Setting a4 = bool("1.7 Bow Animation", true);
            Setting a5 = bool("1.7 Eating Animation", true);
            Setting a6 = bool("1.7 Drinking Animation", true);
            Setting a7 = bool("1.7 Swing Animation", false);
            Setting a8 = bool("1.7 Item Held Animation", true);
            Setting a9 = bool("1.7 Item Update", true);
            Setting a11 = bool("No Inventory Move", true);
            Setting b1 = bool("1.7 Damage (breve)", false);
            Setting b2 = bool("1.7 Hearts (breve)", false);
            Setting b3 = bool("1.7 Glint (breve)", false);
            Setting b4 = bool("1.7 Place (breve)", false);
            Setting b5 = bool("1.7 Build (breve)", false);
            Setting b6 = bool("1.7 Debug (breve)", false);
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

        noPart = add(new Module("Sem Particulas") {
            public void onTick() {
                Minecraft.getMinecraft().gameSettings.particleSetting = 2;
            }
        });

        optim = add(new Module("Otimizacao", true) {
            Setting shadows = bool("Sem sombra de entidades", true);
            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                if (b("Sem sombra de entidades")) mc.gameSettings.entityShadows = false;
            }
        });

        plus = add(new Module("Otimizacao Plus", true) {
            Setting tex = bool("Congelar texturas", true);
            Setting vs = bool("Vsync desligado", true);
            Setting dyn = bool("Distancia dinamica", true);
            Setting tgt = num("FPS alvo", 45, 20, 120, 5);
            Setting dmin = num("Distancia minima", 3, 2, 8, 1);
            Setting dmax = num("Distancia maxima", 6, 3, 16, 1);
            Setting bob = bool("Sem view bobbing", false);
            Setting mip = bool("Mipmap zero", false);

            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                if (b("Congelar texturas")) FpsBoost.freezeTextures(mc);
                if (b("Vsync desligado")) FpsBoost.vsyncOff(mc);
                if (b("Sem view bobbing")) mc.gameSettings.viewBobbing = false;
                if (b("Mipmap zero")) FpsBoost.mipmapZero(mc);
                if (b("Distancia dinamica")) {
                    FpsBoost.dynamicDistance(mc, (int) n("Distancia minima"), (int) n("Distancia maxima"), (int) n("FPS alvo"));
                }
            }
        });

        render = add(new Module("Render", true) {
            Setting fire = bool("Sem fogo na tela", true);
            Setting gfx = bool("Graficos rapidos", true);
            Setting menus = bool("FPS baixo em menus", true);
            Setting lim = num("Limite de FPS", 60, 30, 260, 10);
            Setting stands = bool("Esconder armor stands", false);
            Setting mobs = bool("Esconder mobs longe", false);
            Setting md = num("Distancia de mobs", 24, 8, 64, 4);

            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                if (b("Graficos rapidos")) {
                    mc.gameSettings.fancyGraphics = false;
                    mc.gameSettings.ambientOcclusion = 0;
                }
                int f = (int) n("Limite de FPS");
                if (b("FPS baixo em menus") && mc.currentScreen != null) f = Math.min(f, 30);
                mc.gameSettings.limitFramerate = f;
            }
        });

        blockOv = add(new Module("Block Overlay") {
            Setting cor = num("Cor", 9, 0, 9, 1);
            Setting fill = bool("Preenchimento", true);
            Setting fo = num("Opacidade preenchimento", 0.25, 0.0, 1.0, 0.05);
            Setting line = bool("Contorno", true);
            Setting lo = num("Opacidade contorno", 1.0, 0.1, 1.0, 0.1);
            Setting w = num("Espessura", 2, 1, 6, 1);
        });

        nametag = add(new Module("Nametag") {
            Setting bg = bool("Fundo", true);
            Setting cor = num("Cor do fundo", 8, 0, 9, 1);
            Setting op = num("Opacidade do fundo", 0.25, 0.0, 1.0, 0.05);
            Setting sh = bool("Sombra no texto", false);
            Setting sc = num("Escala", 1.0, 0.5, 2.0, 0.1);
            Setting hp = bool("Mostrar vida", false);
            Setting self = bool("Mostrar propria (F5)", false);
        });

        tab = add(new Module("Tab Ping") {
            Setting bg = bool("Fundo", true);
            Setting op = num("Opacidade do fundo", 0.5, 0.0, 1.0, 0.05);
            Setting ping = bool("Mostrar ping", true);
            Setting heads = bool("Mostrar cabecas", true);
        });

        chat = add(new Module("Chat", true) {
            Setting bg = bool("Fundo", true);
            Setting op = num("Opacidade do fundo", 0.5, 0.0, 1.0, 0.05);
            Setting fit = bool("Fundo ajustado ao texto", false);
            Setting shadow = bool("Sombra do texto", true);
            Setting size = num("Tamanho", 1.0, 0.5, 2.0, 0.1);
            Setting secs = num("Tempo na tela (s)", 10, 3, 30, 1);
            Setting always = bool("Sempre visivel", false);
            Setting time = bool("Horario", false);
        });

        tab.enabled = false;
        nametag.enabled = false;
        blockOv.enabled = false;

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