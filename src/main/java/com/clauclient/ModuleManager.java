package com.clauclient;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.MinecraftForge;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<Module>();
    public static Module sprint, fullbright, hitFix, hurt, zoom, persp, oldAnim, crosshair, sidebar, potion, noPart, chat, blockOv, nametag, tab;
    public static Module effects, lazy, texfix, mem, reloadc, glint, shield;

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
            Setting a1v = num("Velocidade do blockhit", 1.3, 1.0, 2.0, 0.1);
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

            float acc;

            public void onTick() {
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayerSP p = mc.thePlayer;
                if (p == null || !b("1.7 Blockhit")) {
                    acc = 0f;
                    return;
                }
                if (p.isUsingItem() && p.getHeldItem() != null
                        && p.getHeldItem().getItem() instanceof ItemSword
                        && p.isSwingInProgress) {
                    acc += (float) (n("Velocidade do blockhit") - 1.0);
                    while (acc >= 1f) {
                        if (p.swingProgressInt < 5) p.swingProgressInt++;
                        acc -= 1f;
                    }
                } else {
                    acc = 0f;
                }
            }
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

        lazy = add(new Module("Lazy Chunk Loading", true) {
            Setting start = num("Distancia inicial", 2, 1, 6, 1);
            Setting iv = num("Intervalo (s)", 2, 1, 10, 1);
            public void onTick() {
                Perf.lazyTick(Minecraft.getMinecraft(), this);
            }
            public void onDisable() {
                Perf.lazyStop(Minecraft.getMinecraft());
            }
        });

        texfix = add(new Module("Tex Fix", true) {
            public void onTick() {
                Perf.texFreeze(Minecraft.getMinecraft());
            }
        });

        effects = add(new Module("Sem Efeitos", true) {
            Setting arrows = bool("Sem flechas", true);
            Setting body = bool("Sem flechas no corpo", true);
            Setting pfire = bool("Sem fogo em jogadores", true);
            Setting hfire = bool("Sem fogo na tela", true);
            Setting ach = bool("Sem conquistas", true);
            Setting leaves = bool("Folhas simples", false);
            public void onTick() {
                Perf.effectsTick(Minecraft.getMinecraft(), this);
            }
        });

        mem = add(new Module("Limpar Memoria", true) {
            Setting iv = num("Intervalo (min)", 5, 1, 30, 1);
            Setting now = action("Limpar agora", new Runnable() {
                public void run() {
                    Perf.gc();
                }
            });
            public void onTick() {
                Perf.gcTick(Minecraft.getMinecraft(), this);
            }
        });

        reloadc = add(new Module("Recarregar Chunk") {
            Setting go = action("Recarregar agora", new Runnable() {
                public void run() {
                    Perf.reloadChunks(Minecraft.getMinecraft());
                }
            });
        });

        glint = add(new Module("Glint Colorize", true) {
            Setting op = num("Opacidade", 0.35, 0.1, 1.0, 0.05);
            Setting pot = bool("Cor da pocao", true);
            Setting fixo = num("Cor fixa", 9, 0, 9, 1);
        });

        shield = add(new Module("Escudo", true) {
            Setting sc = num("Tamanho", 1.0, 0.5, 2.0, 0.1);
            Setting oy = num("Posicao Y", 0, -4, 4, 0.5);
            Setting oz = num("Posicao Z", 0, -3, 3, 0.5);
            public void onTick() {
                CosmeticEvents.installShield();
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

        MinecraftForge.EVENT_BUS.register(new CosmeticEvents());
    }
}