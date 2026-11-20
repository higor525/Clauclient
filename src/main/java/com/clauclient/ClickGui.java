package com.clauclient;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui extends GuiScreen {
    private static final int W = 140;
    private static final int ROW = 20;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int x = width / 2 - W / 2;
        int y = 30;

        drawRect(x, y, x + W, y + ROW, 0xFF1E90FF);
        drawCenteredString(fontRendererObj, "ClauClient", width / 2, y + 6, 0xFFFFFF);

        int i = 1;
        for (Module m : ModuleManager.modules) {
            int ry = y + ROW * i;
            boolean hover = mouseX >= x && mouseX <= x + W && mouseY >= ry && mouseY < ry + ROW;
            drawRect(x, ry, x + W, ry + ROW, hover ? 0xFF3A3A3A : 0xFF222222);
            fontRendererObj.drawStringWithShadow(m.name, x + 6, ry + 6, 0xFFFFFF);
            String st = m.enabled ? "ON" : "OFF";
            fontRendererObj.drawStringWithShadow(st,
                    x + W - 6 - fontRendererObj.getStringWidth(st), ry + 6,
                    m.enabled ? 0x55FF55 : 0xFF5555);
            i++;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button == 0) {
            int x = width / 2 - W / 2;
            int y = 30;
            int i = 1;
            for (Module m : ModuleManager.modules) {
                int ry = y + ROW * i;
                if (mouseX >= x && mouseX <= x + W && mouseY >= ry && mouseY < ry + ROW) {
                    m.toggle();
                }
                i++;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
