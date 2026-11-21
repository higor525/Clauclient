package com.clauclient;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.Display;

public class FpsBoost {
    private static Field animField;
    private static boolean animTried;
    private static int tick;
    private static int lastAdjust;
    private static int lastDrop = -100000;

    public static void freezeTextures(Minecraft mc) {
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

    public static void vsyncOff(Minecraft mc) {
        if (mc.gameSettings.enableVsync) {
            mc.gameSettings.enableVsync = false;
            Display.setVSyncEnabled(false);
        }
    }

    public static void dynamicDistance(Minecraft mc, int min, int max, int target) {
        tick++;
        if (mc.theWorld == null || mc.currentScreen != null) return;
        if (max < min) max = min;
        if (tick - lastAdjust < 100) return;
        int cur = mc.gameSettings.renderDistanceChunks;
        int fps = Minecraft.getDebugFPS();
        if (cur > max) {
            mc.gameSettings.renderDistanceChunks = max;
            lastAdjust = tick;
        } else if (cur < min) {
            mc.gameSettings.renderDistanceChunks = min;
            lastAdjust = tick;
        } else if (fps < target - 5 && cur > min) {
            mc.gameSettings.renderDistanceChunks = cur - 1;
            lastAdjust = tick;
            lastDrop = tick;
        } else if (fps >= target + 12 && cur < max && tick - lastDrop > 600) {
            mc.gameSettings.renderDistanceChunks = cur + 1;
            lastAdjust = tick;
        }
    }

    public static void mipmapZero(Minecraft mc) {
        if (mc.gameSettings.mipmapLevels != 0) {
            mc.gameSettings.mipmapLevels = 0;
            mc.refreshResources();
        }
    }
                                  }
