package com.clauclient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Perf {

    // ---------- Lazy Chunk Loading ----------
    private static Object lastWorld;
    private static boolean lazyActive;
    private static int lazyTarget, lazyTicks;

    public static void lazyTick(Minecraft mc, Module m) {
        if (mc.theWorld == null) {
            lastWorld = null;
            return;
        }
        if (mc.theWorld != lastWorld) {
            lastWorld = mc.theWorld;
            if (!lazyActive) {
                lazyTarget = mc.gameSettings.renderDistanceChunks;
                int start = (int) m.n("Distancia inicial");
                if (lazyTarget > start) {
                    mc.gameSettings.renderDistanceChunks = start;
                    lazyActive = true;
                    lazyTicks = 0;
                }
            }
        }
        if (lazyActive) {
            lazyTicks++;
            int step = (int) (m.n("Intervalo (s)") * 20);
            if (lazyTicks >= step) {
                lazyTicks = 0;
                int cur = mc.gameSettings.renderDistanceChunks;
                if (cur < lazyTarget) mc.gameSettings.renderDistanceChunks = cur + 1;
                if (cur + 1 >= lazyTarget) lazyActive = false;
            }
        }
    }

    public static void lazyStop(Minecraft mc) {
        if (lazyActive) {
            mc.gameSettings.renderDistanceChunks = lazyTarget;
            lazyActive = false;
        }
    }

    // ---------- Tex Fix (congelar texturas animadas) ----------
    private static Field animField;
    private static boolean animTried;

    public static void texFreeze(Minecraft mc) {
        if (!animTried) {
            animTried = true;
            try {
                animField = ReflectionHelper.findField(TextureMap.class, "listAnimatedSprites", "field_94258_i");
            } catch (Exception e) {
                animField = null;
            }
        }
        if (animField == null) return;
        try {
            TextureMap map = mc.getTextureMapBlocks();
            if (map == null) return;
            List<?> list = (List<?>) animField.get(map);
            if (list != null && !list.isEmpty()) list.clear();
        } catch (Exception e) {
            animField = null;
        }
    }

    // ---------- Limpar memoria ----------
    private static long lastGc = System.currentTimeMillis();

    public static void gc() {
        lastGc = System.currentTimeMillis();
        System.gc();
    }

    public static void gcTick(Minecraft mc, Module m) {
        long now = System.currentTimeMillis();
        long iv = (long) (m.n("Intervalo (min)") * 60000.0);
        if (now - lastGc >= iv && (mc.currentScreen != null || now - lastGc >= iv * 2)) {
            gc();
        }
    }

    // ---------- Recarregar chunks ----------
    public static void reloadChunks(Minecraft mc) {
        if (mc.renderGlobal != null && mc.theWorld != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    // ---------- Sem conquistas ----------
    private static boolean achDone;

    private static boolean blockAch() {
        Module m = ModuleManager.effects;
        return m != null && m.enabled && m.b("Sem conquistas");
    }

    private static class NoAch extends GuiAchievement {
        NoAch(Minecraft mc) {
            super(mc);
        }

        @Override
        public void displayAchievement(Achievement a) {
            if (!blockAch()) super.displayAchievement(a);
        }

        @Override
        public void updateAchievementWindow() {
            if (!blockAch()) super.updateAchievementWindow();
        }
    }

    // ---------- Sem Efeitos ----------
    public static void effectsTick(Minecraft mc, Module m) {
        if (!achDone && mc.guiAchievement != null) {
            achDone = true;
            mc.guiAchievement = new NoAch(mc);
        }
        if (m.b("Sem flechas") && mc.theWorld != null) {
            List<Entity> copy = new ArrayList<Entity>(mc.theWorld.loadedEntityList);
            for (Entity e : copy) {
                if (e instanceof EntityArrow) {
                    mc.theWorld.removeEntityFromWorld(e.getEntityId());
                }
            }
        }
        if (m.b("Folhas simples") && mc.gameSettings.fancyGraphics) {
            mc.gameSettings.fancyGraphics = false;
            if (mc.renderGlobal != null && mc.theWorld != null) mc.renderGlobal.loadRenderers();
        }
    }
              }
