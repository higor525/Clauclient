package com.clauclient;

import java.io.IOException;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;

public class ClauMainMenu extends GuiScreen {
    private static final String[] LABELS = {"Singleplayer", "Multiplayer", "Options...", "Quit Game"};
    private int bx, bw, by;
    private static final int BH = 20;
    private static final int GAP = 4;

    private void layout() {
        bw = Math.min(180, width - 40);
        bx = (width - bw) / 2;
        by = height / 2 - 8;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        layout();
        Logo.background(width, height);
        Logo.drawCrown(width / 2, Math.max(4, height / 2 - 100), 2);
        Logo.draw(width / 2, Math.max(20, height / 2 - 84), 3f);

        for (int i = 0; i < LABELS.length; i++) {
            int y = by + i * (BH + GAP);
            boolean hv = Ui.in(mx, my, bx, y, bw, BH);
            drawRect(bx, y, bx + bw, y + BH, 0xFF7C3AED);
            drawRect(bx + 1, y + 1, bx + bw - 1, y + BH - 1, hv ? 0xFF1A1030 : 0xFF0C0C12);
            drawCenteredString(fontRendererObj, LABELS[i], width / 2, y + 6, 0xFFFFFF);
        }

        fontRendererObj.drawStringWithShadow("Clau ", 4, height - 12, 0xFFFFFFFF);
        fontRendererObj.drawStringWithShadow("Client", 4 + fontRendererObj.getStringWidth("Clau "), height - 12, Logo.PURPLE);
        fontRendererObj.drawStringWithShadow("v1.0", 4 + fontRendererObj.getStringWidth("Clau Client "), height - 12, 0xFFB07CFF);
        String v = "Minecraft 1.8.9";
        fontRendererObj.drawStringWithShadow(v, width - fontRendererObj.getStringWidth(v) - 4, height - 12, 0xFFCCCCCC);
        super.drawScreen(mx, my, pt);
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        if (button != 0) return;
        layout();
        for (int i = 0; i < LABELS.length; i++) {
            int y = by + i * (BH + GAP);
            if (Ui.in(mx, my, bx, y, bw, BH)) {
                if (i == 0) mc.displayGuiScreen(new GuiSelectWorld(this));
                else if (i == 1) mc.displayGuiScreen(new GuiMultiplayer(this));
                else if (i == 2) mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                else mc.shutdown();
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char c, int k) throws IOException {
    }
      }
