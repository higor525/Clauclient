package com.clauclient;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;

public class CrosshairGui extends GuiScreen {
    private final GuiScreen parent;
    private boolean paint, dragging;
    private int cell, gx, gy, bx;

    public CrosshairGui(GuiScreen parent) {
        this.parent = parent;
    }

    private void layout() {
        int n = CrosshairData.N;
        cell = Math.max(4, Math.min(10, (height - 40) / n));
        int total = cell * n + 10 + 80;
        gx = (width - total) / 2;
        bx = gx + cell * n + 10;
        gy = (height - cell * n) / 2 + 6;
    }

    private int cellAt(int mx, int my) {
        int n = CrosshairData.N;
        if (mx < gx || my < gy || mx >= gx + cell * n || my >= gy + cell * n) return -1;
        return ((my - gy) / cell) * n + (mx - gx) / cell;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        layout();
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Desenhar crosshair", width / 2, 6, 0xFFFFFF);
        int n = CrosshairData.N;
        Module m = ModuleManager.crosshair;
        int col = 0xFF000000 | Ui.color(m.n("Cor"));
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int x = gx + c * cell, y = gy + r * cell;
                boolean on = CrosshairData.GRID[r * n + c];
                drawRect(x, y, x + cell - 1, y + cell - 1, on ? col : 0xFF22222C);
            }
        }
        String[] labels = {"Limpar", "Padrao", "Cor", "Pronto"};
        for (int i = 0; i < labels.length; i++) {
            int by = gy + i * 24;
            boolean hv = Ui.in(mx, my, bx, by, 80, 20);
            drawRect(bx, by, bx + 80, by + 20, hv ? 0xFF6B46C1 : 0xFF4A32A0);
            drawCenteredString(fontRendererObj, labels[i], bx + 40, by + 6, 0xFFFFFF);
        }
        drawRect(bx + 60, gy + 48 + 4, bx + 76, gy + 48 + 16, col);
        super.drawScreen(mx, my, pt);
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        if (button != 0) return;
        int i = cellAt(mx, my);
        if (i >= 0) {
            paint = !CrosshairData.GRID[i];
            CrosshairData.GRID[i] = paint;
            dragging = true;
            return;
        }
        for (int k = 0; k < 4; k++) {
            if (Ui.in(mx, my, bx, gy + k * 24, 80, 20)) {
                if (k == 0) {
                    for (int j = 0; j < CrosshairData.GRID.length; j++) CrosshairData.GRID[j] = false;
                } else if (k == 1) {
                    CrosshairData.reset();
                } else if (k == 2) {
                    Setting s = ModuleManager.crosshair.get("Cor");
                    s.num = (s.num + 1) % 8;
                } else {
                    mc.displayGuiScreen(parent);
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mx, int my, int button, long time) {
        if (!dragging) return;
        int i = cellAt(mx, my);
        if (i >= 0) CrosshairData.GRID[i] = paint;
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        dragging = false;
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
