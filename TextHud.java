package com.clauclient;

import net.minecraft.client.Minecraft;

public abstract class TextHud extends HudModule {
    public final Setting label, showLabel, labelAfter, textColor;

    public TextHud(String name, int x, int y) {
        super(name, x, y);
        label = text("Nome", name);
        showLabel = bool("Mostrar nome", true);
        labelAfter = bool("Nome depois do valor", false);
        textColor = num("Cor do texto", 0, 0, 7, 1);
    }

    public abstract String getValue();

    public String getText() {
        String v = getValue();
        String l = label.text;
        if (!showLabel.bool || l == null || l.length() == 0) return v;
        return labelAfter.bool ? v + " " + l : l + ": " + v;
    }

    public int contentWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(getText()) + 10;
    }

    public int contentHeight() {
        return 14;
    }

    public void drawContent() {
        drawBackground(contentWidth(), contentHeight());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(getText(), 5, 3, 0xFF000000 | Ui.color(textColor.num));
    }
}
