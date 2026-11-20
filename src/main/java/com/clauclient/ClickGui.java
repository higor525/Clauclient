package com.clauclient;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class ClickGui extends GuiScreen {
    private static final int STEP = 28;
    private int scroll, pressY, lastY;
    private boolean pressing, dragged;
    private int px, pw, py, top, bottom, cardW;

    private void layout() {
        pw = Math.min(width - 16, 320);
        px = (width - pw) / 2;
        py = 8;
        top = py + 28;
        bottom = height - 8;
        cardW = (pw - 12) / 2;
    }

    private int maxScroll() {
        int rows = (ModuleManager.modules.size() + 1) / 2;
        return Math.max(0, rows * STEP - (bottom - top));
    }

    private void clamp() {
        if (scroll > maxScroll()) scroll = maxScroll();
        if (scroll < 0) scroll = 0;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        layout();
        drawDefaultBackground();
        drawRect(px - 1, py - 1, px + pw + 1, bottom + 1, 0xFF3A2A66);
        drawRect(px, py, px + pw, bottom, 0xF00D0D14);
        drawRect(px, py, px + pw, py + 24, 0xFF171722);
        drawRect(px, py + 24, px + pw, py + 25, Ui.ACCENT);
        fontRendererObj.drawStringWithShadow("ClauClient", px + 8, py + 8, Ui.ACCENT);
        int bx = px + pw - 84, by = py + 4;
        boolean hb = Ui.in(mx, my, bx, by, 80, 16);
        drawRect(bx, by, bx + 80, by + 16, hb ? 0xFF6B46C1 : 0xFF4A32A0);
        drawCenteredString(fontRendererObj, "Editar HUD", bx + 40, by + 4, 0xFFFFFF);

        Ui.scissor(px, top, pw, bottom - top);
        int i = 0;
        for (Module m : ModuleManager.modules) {
            int cx = px + 4 + (i % 2) * (cardW + 4);
            int cy = top + (i / 2) * STEP - scroll;
            i++;
            if (cy + 24 < top || cy > bottom) continue;
            boolean hv = Ui.in(mx, my, cx, cy, cardW, 24) && my >= top && my < bottom;
            int bgc = m.enabled ? (hv ? 0xFF38296A : 0xFF2B1F4D) : (hv ? 0xFF262633 : 0xFF1A1A24);
            drawRect(cx, cy, cx + cardW, cy + 24, bgc);
            if (m.enabled) drawRect(cx, cy, cx + 2, cy + 24, Ui.ACCENT);
            fontRendererObj.drawStringWithShadow(m.name, cx + 8, cy + 8, 0xFFFFFF);
            boolean hasSet = !m.settings.isEmpty();
            int right = cx + cardW - (hasSet ? 24 : 6);
            String st = m.enabled ? "ON" : "OFF";
            fontRendererObj.drawStringWithShadow(st, right - fontRendererObj.getStringWidth(st), cy + 8, m.enabled ? 0x55FF55 : 0xFF5555);
            if (hasSet) {
                drawRect(cx + cardW - 20, cy + 2, cx + cardW - 2, cy + 22, 0x40FFFFFF);
                drawCenteredString(fontRendererObj, ">", cx + cardW - 11, cy + 8, 0xFFFFFF);
            }
        }
        Ui.endScissor();
        super.drawScreen(mx, my, pt);
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        if (button == 0) {
            pressing = true;
            dragged = false;
            pressY = my;
            lastY = my;
        }
    }

    @Override
    protected void mouseClickMove(int mx, int my, int button, long time) {
        if (!pressing) return;
        if (Math.abs(my - pressY) > 4) dragged = true;
        if (dragged) {
            scroll -= my - lastY;
            clamp();
        }
        lastY = my;
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        if (state == 0 && pressing && !dragged) click(mx, my);
        pressing = false;
    }

    private void click(int mx, int my) {
        layout();
        if (Ui.in(mx, my, px + pw - 84, py + 4, 80, 16)) {
            mc.displayGuiScreen(new HudEditor());
            return;
        }
        if (my < top || my >= bottom) return;
        int i = 0;
        for (Module m : ModuleManager.modules) {
            int cx = px + 4 + (i % 2) * (cardW + 4);
            int cy = top + (i / 2) * STEP - scroll;
            i++;
            if (Ui.in(mx, my, cx, cy, cardW, 24)) {
                if (!m.settings.isEmpty() && mx >= cx + cardW - 20) {
                    mc.displayGuiScreen(new SettingsGui(this, m));
                } else {
                    m.toggle();
                }
                return;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int d = Mouse.getEventDWheel();
        if (d != 0) {
            scroll += d > 0 ? -20 : 20;
            clamp();
        }
    }

    @Override
    public void onGuiClosed() {
        Config.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}