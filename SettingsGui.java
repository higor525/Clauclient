package com.clauclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SettingsGui extends GuiScreen {
    private static final int ROW = 20;
    private final GuiScreen parent;
    private final Module module;
    private final Map<Setting, GuiTextField> fields = new HashMap<Setting, GuiTextField>();
    private int scroll, pressY, lastY;
    private boolean pressing, dragged;
    private int px, pw, top, bottom;

    public SettingsGui(GuiScreen parent, Module module) {
        this.parent = parent;
        this.module = module;
    }

    private void layout() {
        pw = Math.min(width - 16, 260);
        px = (width - pw) / 2;
        top = 34;
        bottom = height - 8;
    }

    private int maxScroll() {
        return Math.max(0, module.settings.size() * ROW - (bottom - top));
    }

    private void clamp() {
        if (scroll > maxScroll()) scroll = maxScroll();
        if (scroll < 0) scroll = 0;
    }

    @Override
    public void initGui() {
        layout();
        Keyboard.enableRepeatEvents(true);
        fields.clear();
        int id = 0;
        for (Setting s : module.settings) {
            if (s.type == Setting.TEXT) {
                GuiTextField f = new GuiTextField(id++, fontRendererObj, 0, 0, 104, 14);
                f.setMaxStringLength(16);
                f.setText(s.text);
                fields.put(s, f);
            }
        }
    }

    @Override
    public void updateScreen() {
        for (GuiTextField f : fields.values()) f.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        layout();
        drawDefaultBackground();
        drawRect(px - 1, 7, px + pw + 1, bottom + 1, 0xFF3A2A66);
        drawRect(px, 8, px + pw, bottom, 0xF00D0D14);
        drawRect(px, 8, px + pw, 30, 0xFF171722);
        drawRect(px, 30, px + pw, 31, Ui.ACCENT);
        boolean hv = Ui.in(mx, my, px + 4, 12, 44, 14);
        drawRect(px + 4, 12, px + 48, 26, hv ? 0xFF6B46C1 : 0xFF4A32A0);
        drawCenteredString(fontRendererObj, "Voltar", px + 26, 15, 0xFFFFFF);
        drawCenteredString(fontRendererObj, module.name, px + pw / 2 + 20, 15, Ui.ACCENT);

        Ui.scissor(px, top, pw, bottom - top);
        int cx = px + pw - 114;
        int i = 0;
        for (Setting s : module.settings) {
            int ry = top + i * ROW - scroll;
            i++;
            if (ry + ROW < top || ry > bottom) continue;
            drawRect(px + 4, ry + 1, px + pw - 4, ry + ROW - 1, 0xFF1A1A24);
            fontRendererObj.drawStringWithShadow(s.name, px + 10, ry + 6, 0xFFFFFF);
            if (s.type == Setting.BOOL) {
                drawCenteredString(fontRendererObj, s.bool ? "ON" : "OFF", cx + 57, ry + 6, s.bool ? 0x55FF55 : 0xFF5555);
            } else if (s.type == Setting.NUM) {
                drawCenteredString(fontRendererObj, "<", cx + 8, ry + 6, 0xFFFFFF);
                drawCenteredString(fontRendererObj, ">", cx + 106, ry + 6, 0xFFFFFF);
                if (s.name.startsWith("Cor")) {
                    drawRect(cx + 47, ry + 5, cx + 67, ry + 15, 0xFF000000 | Ui.color(s.num));
                } else {
                    drawCenteredString(fontRendererObj, s.display(), cx + 57, ry + 6, 0xFFFFFF);
                }
            } else if (s.type == Setting.TEXT) {
                GuiTextField f = fields.get(s);
                f.xPosition = cx + 4;
                f.yPosition = ry + 3;
                f.drawTextBox();
            } else {
                drawRect(cx + 4, ry + 3, cx + 108, ry + ROW - 3, 0xFF4A32A0);
                drawCenteredString(fontRendererObj, "Abrir", cx + 56, ry + 6, 0xFFFFFF);
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
        if (Ui.in(mx, my, px + 4, 12, 44, 14)) {
            mc.displayGuiScreen(parent);
            return;
        }
        for (GuiTextField f : fields.values()) f.mouseClicked(mx, my, 0);
        if (my < top || my > bottom) return;
        int cx = px + pw - 114;
        int i = 0;
        for (Setting s : module.settings) {
            int ry = top + i * ROW - scroll;
            i++;
            if (!Ui.in(mx, my, px + 4, ry, pw - 8, ROW)) continue;
            if (s.type == Setting.BOOL) {
                s.bool = !s.bool;
            } else if (s.type == Setting.NUM) {
                if (mx >= cx) s.inc(mx < cx + 57 ? -1 : 1);
            } else if (s.type == Setting.ACTION) {
                if (mx >= cx && s.action != null) s.action.run();
            }
            return;
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
    protected void keyTyped(char c, int k) throws IOException {
        for (Map.Entry<Setting, GuiTextField> e : fields.entrySet()) {
            GuiTextField f = e.getValue();
            if (f.isFocused()) {
                if (k == Keyboard.KEY_ESCAPE || k == Keyboard.KEY_RETURN) {
                    f.setFocused(false);
                    return;
                }
                f.textboxKeyTyped(c, k);
                e.getKey().text = f.getText();
                return;
            }
        }
        if (k == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
