package com.clauclient;

import java.io.IOException;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class HudEditor extends GuiScreen {
    private HudModule dragging;
    private int offX, offY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Arraste os itens para mudar a posicao", width / 2, 8, 0xFFFFFF);
        for (Module m : ModuleManager.modules) {
            if (m instanceof HudModule && m.enabled) {
                HudModule h = (HudModule) m;
                h.render();
                int c = (h == dragging) ? 0xFF55FFFF : 0xFFFFFFFF;
                Gui.drawRect(h.x - 1, h.y - 1, h.x + h.getWidth() + 1, h.y, c);
                Gui.drawRect(h.x - 1, h.y + h.getHeight(), h.x + h.getWidth() + 1, h.y + h.getHeight() + 1, c);
                Gui.drawRect(h.x - 1, h.y, h.x, h.y + h.getHeight(), c);
                Gui.drawRect(h.x + h.getWidth(), h.y, h.x + h.getWidth() + 1, h.y + h.getHeight(), c);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button == 0) {
            for (Module m : ModuleManager.modules) {
                if (m instanceof HudModule && m.enabled) {
                    HudModule h = (HudModule) m;
                    if (mouseX >= h.x && mouseX <= h.x + h.getWidth()
                            && mouseY >= h.y && mouseY <= h.y + h.getHeight()) {
                        dragging = h;
                        offX = mouseX - h.x;
                        offY = mouseY - h.y;
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
        if (dragging != null) {
            int nx = mouseX - offX;
            int ny = mouseY - offY;
            dragging.x = Math.max(0, Math.min(nx, width - dragging.getWidth()));
            dragging.y = Math.max(0, Math.min(ny, height - dragging.getHeight()));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = null;
        super.mouseReleased(mouseX, mouseY, state);
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
