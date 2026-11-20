package com.clauclient;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ChatRender {
    private static Field linesField, scrollField;
    private static boolean tried;

    public static boolean ready() {
        if (!tried) {
            tried = true;
            try {
                Field byType = null;
                for (Field f : GuiNewChat.class.getDeclaredFields()) {
                    String n = f.getName();
                    if (n.equals("drawnChatLines") || n.equals("field_146253_i")) {
                        linesField = f;
                    } else if (List.class.isAssignableFrom(f.getType()) && f.getGenericType().toString().contains("ChatLine")) {
                        byType = f;
                    } else if (f.getType() == int.class && scrollField == null) {
                        scrollField = f;
                    }
                }
                if (linesField == null) linesField = byType;
                if (linesField != null) linesField.setAccessible(true);
                if (scrollField != null) scrollField.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                linesField = null;
                scrollField = null;
            }
        }
        return linesField != null && scrollField != null;
    }

    @SuppressWarnings("unchecked")
    public static void draw(GuiNewChat chat, int counter, Module c) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) return;

        List<ChatLine> lines;
        int scroll;
        try {
            lines = (List<ChatLine>) linesField.get(chat);
            scroll = scrollField.getInt(chat);
        } catch (Exception e) {
            return;
        }
        if (lines == null || lines.isEmpty()) return;

        FontRenderer fr = mc.fontRendererObj;
        int count = chat.getLineCount();
        boolean open = chat.getChatOpen();
        boolean always = c.b("Sempre visivel");
        boolean bg = c.b("Fundo");
        boolean fit = c.b("Fundo ajustado ao texto");
        boolean shadow = c.b("Sombra do texto");
        double bgOp = c.n("Opacidade do fundo");
        double fadeTicks = c.n("Tempo na tela (s)") * 20.0;
        float textOp = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
        float scale = chat.getChatScale();
        int width = MathHelper.ceiling_float_int((float) chat.getChatWidth() / scale);

        GlStateManager.pushMatrix();
        GlStateManager.translate(2.0F, 20.0F, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);

        for (int i = 0; i + scroll < lines.size() && i < count; i++) {
            ChatLine cl = lines.get(i + scroll);
            if (cl == null) continue;
            int age = counter - cl.getUpdatedCounter();
            if (age < fadeTicks || open || always) {
                double d = age / fadeTicks;
                d = 1.0D - d;
                d = d * 10.0D;
                d = MathHelper.clamp_double(d, 0.0D, 1.0D);
                d = d * d;
                int fa = (int) (255.0D * d);
                if (open || always) fa = 255;
                int a = (int) ((float) fa * textOp);
                if (a > 3) {
                    int y = -i * 9;
                    String s = cl.getChatComponent().getFormattedText();
                    if (bg) {
                        int bgA = (int) (bgOp * fa);
                        if (bgA > 0) {
                            int w = fit ? fr.getStringWidth(s) : width;
                            Gui.drawRect(0, y - 9, w + 4, y, bgA << 24);
                        }
                    }
                    GlStateManager.enableBlend();
                    int col = 16777215 + (a << 24);
                    if (shadow) fr.drawStringWithShadow(s, 0.0F, (float) (y - 8), col);
                    else fr.drawString(s, 0, y - 8, col);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
                      }
