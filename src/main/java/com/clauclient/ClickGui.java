package com.clauclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class ClickGui extends GuiScreen {
    private static final String[] CATS = {"Combat", "Visual", "Movement", "HUD", "Misc", "Config"};
    private static final String[][] INFO = {
        {"Hit Delay Fix", "Combat", "Remove o atraso entre os golpes."},
        {"Fullbright", "Visual", "Deixa tudo claro, mesmo no escuro."},
        {"No Hurt Cam", "Visual", "Tira o balanco da camera ao levar dano."},
        {"Zoom", "Visual", "Aproxima a visao com a tecla C."},
        {"Perspectiva", "Visual", "Camera 360 sem girar o boneco (tecla P)."},
        {"Old Animations", "Visual", "Animacoes antigas e posicao do item."},
        {"Crosshair", "Visual", "Mira personalizada desenhada por voce."},
        {"Sem Particulas", "Visual", "Remove todas as particulas."},
        {"Sprint", "Movement", "Corre automaticamente ao andar."},
        {"FPS", "HUD", "Mostra seus FPS na tela."},
        {"CPS", "HUD", "Mostra cliques por segundo."},
        {"Coordenadas", "HUD", "Mostra sua posicao XYZ."},
        {"Sidebar", "HUD", "Substitui a sidebar do servidor."},
        {"Potion Status", "HUD", "Mostra os efeitos de pocao ativos."},
        {"Otimizacao", "Misc", "Ajustes para ganhar FPS."}
    };

    private static final int ACCENT = 0xFFA66BFF;
    private static final int PURPLE = 0xFF9B30FF;
    private static int cat = 0;

    private static class Row {
        String name, desc;
        Module m;
        int action;
    }

    private int scroll, pressY, lastY;
    private boolean pressing, dragged;
    private int sx, sw, cx, cw, top, bottom, ly;

    private void layout() {
        sx = 8;
        sw = Math.min(90, width / 4);
        cx = sx + sw + 6;
        cw = width - cx - 8;
        top = 46;
        bottom = height - 34;
        ly = top + 28;
    }

    private List<Row> rows() {
        List<Row> list = new ArrayList<Row>();
        String c = CATS[cat];
        if (c.equals("Config")) {
            Row a = new Row();
            a.name = "Editar HUD";
            a.desc = "Arraste FPS, CPS e outros.";
            a.action = 1;
            list.add(a);
            Row b = new Row();
            b.name = "Salvar config";
            b.desc = "Salva tudo agora.";
            b.action = 2;
            list.add(b);
            return list;
        }
        for (Module m : ModuleManager.modules) {
            String mc = "Misc";
            String desc = "";
            for (String[] i : INFO) {
                if (i[0].equals(m.name)) {
                    mc = i[1];
                    desc = i[2];
                }
            }
            if (mc.equals(c)) {
                Row r = new Row();
                r.name = m.name;
                r.desc = desc;
                r.m = m;
                list.add(r);
            }
        }
        return list;
    }

    private int maxScroll() {
        return Math.max(0, rows().size() * 34 - (bottom - 4 - ly));
    }

    private void clamp() {
        if (scroll > maxScroll()) scroll = maxScroll();
        if (scroll < 0) scroll = 0;
    }

    private void box(int x, int y, int w, int h, int fill, int border) {
        drawRect(x, y, x + w, y + h, border);
        drawRect(x + 1, y + 1, x + w - 1, y + h - 1, fill);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        layout();
        drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(sx + 2, 8, 0);
        GlStateManager.scale(1.5f, 1.5f, 1f);
        fontRendererObj.drawStringWithShadow("Clau ", 0, 0, 0xFFFFFFFF);
        fontRendererObj.drawStringWithShadow("Client", fontRendererObj.getStringWidth("Clau "), 0, PURPLE);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        fontRendererObj.drawStringWithShadow("Mod Menu", sx + 2, 26, 0xFF9A9AAA);

        int bx = width - 28, by = 8;
        boolean hx = Ui.in(mx, my, bx, by, 20, 20);
        box(bx, by, 20, 20, hx ? 0xFF2A1250 : 0xFF0C0C12, 0xFF7C3AED);
        drawCenteredString(fontRendererObj, "X", bx + 10, by + 6, 0xFFFFFF);

        box(sx, top, sw, bottom - top, 0xFF0C0C12, 0xFF3B1A7A);
        for (int i = 0; i < CATS.length; i++) {
            int iy = top + 6 + i * 24;
            boolean sel = i == cat;
            boolean hv = Ui.in(mx, my, sx + 4, iy, sw - 8, 20);
            if (sel) box(sx + 4, iy, sw - 8, 20, 0xFF4C1D95, 0xFF7C3AED);
            else if (hv) box(sx + 4, iy, sw - 8, 20, 0xFF1A1030, 0xFF1A1030);
            fontRendererObj.drawStringWithShadow(CATS[i], sx + 12, iy + 6, sel ? 0xFFFFFFFF : 0xFFB8B8C8);
        }

        box(cx, top, cw, bottom - top, 0xFF0C0C12, 0xFF3B1A7A);
        fontRendererObj.drawStringWithShadow(CATS[cat], cx + 10, top + 8, 0xFFFFFFFF);
        drawRect(cx + 10, top + 21, cx + cw - 10, top + 22, PURPLE);

        List<Row> rs = rows();
        Ui.scissor(cx + 2, ly, cw - 4, bottom - 4 - ly);
        for (int i = 0; i < rs.size(); i++) {
            Row r = rs.get(i);
            int ry = ly + i * 34 - scroll;
            if (ry + 30 < ly || ry > bottom) continue;
            int x = cx + 8, w = cw - 22;
            boolean hv = Ui.in(mx, my, x, ry, w, 30) && my >= ly && my < bottom - 4;
            box(x, ry, w, 30, hv ? 0xFF181824 : 0xFF121218, 0xFF2A2A3A);
            fontRendererObj.drawStringWithShadow(r.name, x + 8, ry + 6, 0xFFFFFFFF);
            fontRendererObj.drawStringWithShadow(r.desc, x + 8, ry + 17, 0xFF8A8A9A);
            if (r.m != null) {
                int tx = x + w - 34, ty = ry + 9;
                boolean on = r.m.enabled;
                drawRect(tx, ty, tx + 26, ty + 12, on ? PURPLE : 0xFF444444);
                int kx = on ? tx + 15 : tx + 1;
                drawRect(kx, ty + 1, kx + 10, ty + 11, on ? 0xFFFFFFFF : 0xFFBBBBBB);
                if (!r.m.settings.isEmpty()) {
                    int gx = tx - 20;
                    box(gx, ry + 8, 16, 14, 0xFF2B1F4D, 0xFF7C3AED);
                    drawCenteredString(fontRendererObj, ">", gx + 8, ry + 11, 0xFFFFFF);
                }
            } else {
                int tx = x + w - 50;
                box(tx, ry + 7, 44, 16, 0xFF4C1D95, 0xFF7C3AED);
                drawCenteredString(fontRendererObj, "Abrir", tx + 22, ry + 11, 0xFFFFFF);
            }
        }
        Ui.endScissor();

        int ms = maxScroll();
        if (ms > 0) {
            int trackH = bottom - 4 - ly;
            int thumbH = Math.max(16, trackH * trackH / (trackH + ms));
            int thumbY = ly + (trackH - thumbH) * scroll / ms;
            drawRect(cx + cw - 8, thumbY, cx + cw - 4, thumbY + thumbH, PURPLE);
        }

        int vy = height - 28;
        boolean hv = Ui.in(mx, my, sx, vy, sw, 20);
        box(sx, vy, sw, 20, hv ? 0xFF2A1250 : 0xFF0C0C12, 0xFF7C3AED);
        drawCenteredString(fontRendererObj, "< Voltar", sx + sw / 2, vy + 6, 0xFFFFFF);

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
        if (dragged && mx >= cx) {
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
        if (Ui.in(mx, my, width - 28, 8, 20, 20) || Ui.in(mx, my, sx, height - 28, sw, 20)) {
            mc.displayGuiScreen(null);
            return;
        }
        for (int i = 0; i < CATS.length; i++) {
            if (Ui.in(mx, my, sx + 4, top + 6 + i * 24, sw - 8, 20)) {
                cat = i;
                scroll = 0;
                return;
            }
        }
        if (my < ly || my >= bottom - 4) return;
        List<Row> rs = rows();
        for (int i = 0; i < rs.size(); i++) {
            Row r = rs.get(i);
            int ry = ly + i * 34 - scroll;
            int x = cx + 8, w = cw - 22;
            if (!Ui.in(mx, my, x, ry, w, 30)) continue;
            if (r.m != null) {
                int tx = x + w - 34;
                if (!r.m.settings.isEmpty() && mx >= tx - 22 && mx < tx - 2) {
                    mc.displayGuiScreen(new SettingsGui(this, r.m));
                } else {
                    r.m.toggle();
                }
            } else if (r.action == 1) {
                mc.displayGuiScreen(new HudEditor());
            } else if (r.action == 2) {
                Config.save();
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
    public void onGuiClosed() {
        Config.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}