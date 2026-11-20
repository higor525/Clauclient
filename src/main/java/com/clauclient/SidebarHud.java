package com.clauclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class SidebarHud extends HudModule {
    public final Setting nums = bool("Mostrar numeros", false);
    private String title;
    private final List<String> names = new ArrayList<String>();
    private final List<String> pts = new ArrayList<String>();

    public SidebarHud() {
        super("Sidebar", -1, 80);
    }

    private void refresh() {
        names.clear();
        pts.clear();
        title = null;
        Minecraft mc = Minecraft.getMinecraft();
        ScoreObjective obj = null;
        if (mc.theWorld != null) {
            obj = ClauClient.hiddenObjective;
            if (obj == null) obj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        }
        if (obj == null) {
            if (mc.currentScreen instanceof HudEditor) {
                title = "Sidebar";
                names.add("Exemplo 1");
                names.add("Exemplo 2");
                pts.add("2");
                pts.add("1");
            }
            return;
        }
        Scoreboard sb = obj.getScoreboard();
        Collection<Score> scores = sb.getSortedScores(obj);
        List<Score> list = new ArrayList<Score>();
        for (Score s : scores) {
            if (s.getPlayerName() != null && !s.getPlayerName().startsWith("#")) list.add(s);
        }
        if (list.size() > 15) list = list.subList(list.size() - 15, list.size());
        title = obj.getDisplayName();
        for (int i = list.size() - 1; i >= 0; i--) {
            Score s = list.get(i);
            ScorePlayerTeam team = sb.getPlayersTeam(s.getPlayerName());
            names.add(ScorePlayerTeam.formatPlayerName(team, s.getPlayerName()));
            pts.add(String.valueOf(s.getScorePoints()));
        }
    }

    public int contentWidth() {
        refresh();
        if (title == null) return 1;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int w = fr.getStringWidth(title);
        for (int i = 0; i < names.size(); i++) {
            int ww = fr.getStringWidth(names.get(i));
            if (nums.bool) ww += 8 + fr.getStringWidth(pts.get(i));
            if (ww > w) w = ww;
        }
        return w + 10;
    }

    public int contentHeight() {
        if (title == null) return 1;
        return 4 + 10 * (names.size() + 1);
    }

    public void drawContent() {
        refresh();
        if (title == null) return;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int w = contentWidth();
        int h = contentHeight();
        drawBackground(w, h);
        fr.drawStringWithShadow(title, (w - fr.getStringWidth(title)) / 2f, 3, 0xFFFFFFFF);
        for (int i = 0; i < names.size(); i++) {
            int ry = 3 + 10 * (i + 1);
            fr.drawStringWithShadow(names.get(i), 5, ry, 0xFFFFFFFF);
            if (nums.bool) {
                String p = pts.get(i);
                fr.drawStringWithShadow(p, w - 4 - fr.getStringWidth(p), ry, 0xFFFF5555);
            }
        }
    }
}
