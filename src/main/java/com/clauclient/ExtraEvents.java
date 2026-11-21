package com.clauclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ExtraEvents {

    private static final Comparator<NetworkPlayerInfo> CMP = new Comparator<NetworkPlayerInfo>() {
        public int compare(NetworkPlayerInfo a, NetworkPlayerInfo b) {
            String ta = a.getPlayerTeam() == null ? "" : a.getPlayerTeam().getRegisteredName();
            String tb = b.getPlayerTeam() == null ? "" : b.getPlayerTeam().getRegisteredName();
            int c = ta.compareTo(tb);
            if (c != 0) return c;
            String na = a.getGameProfile().getName();
            String nb = b.getGameProfile().getName();
            if (na == null) na = "";
            if (nb == null) nb = "";
            return na.compareToIgnoreCase(nb);
        }
    };

    // ---------- Render: esconder armor stands e mobs ----------
    @SubscribeEvent
    public void onLiving(RenderLivingEvent.Pre e) {
        Module r = ModuleManager.render;
        if (!r.enabled) return;
        EntityLivingBase en = e.entity;
        if (en instanceof EntityPlayer) return;
        if (en instanceof EntityArmorStand) {
            if (r.b("Esconder armor stands")) e.setCanceled(true);
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (r.b("Esconder mobs longe") && mc.thePlayer != null) {
            double lim = r.n("Distancia de mobs");
            if (en.getDistanceSqToEntity(mc.thePlayer) > lim * lim) e.setCanceled(true);
        }
    }

    // ---------- Render: sem fogo na tela ----------
    @SubscribeEvent
    public void onOverlay(RenderBlockOverlayEvent e) {
        Module r = ModuleManager.render;
        if (r.enabled && r.b("Sem fogo na tela") && e.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) {
            e.setCanceled(true);
        }
    }

    // ---------- Block Overlay ----------
    @SubscribeEvent
    public void onHighlight(DrawBlockHighlightEvent e) {
        Module m = ModuleManager.blockOv;
        if (!m.enabled) return;
        MovingObjectPosition t = e.target;
        if (t == null || t.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        BlockPos pos = t.getBlockPos();
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block.getMaterial() == Material.air || !mc.theWorld.getWorldBorder().contains(pos)) return;
        e.setCanceled(true);
        block.setBlockBoundsBasedOnState(mc.theWorld, pos);
        EntityPlayer p = e.player;
        float pt = e.partialTicks;
        double dx = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt;
        double dy = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt;
        double dz = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt;
        AxisAlignedBB bb = block.getSelectedBoundingBox(mc.theWorld, pos).expand(0.002D, 0.002D, 0.002D).offset(-dx, -dy, -dz);
        int rgb = Ui.color(m.n("Cor"));
        float r = ((rgb >> 16) & 255) / 255f;
        float g = ((rgb >> 8) & 255) / 255f;
        float b = (rgb & 255) / 255f;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        float fa = (float) m.n("Opacidade preenchimento");
        if (m.b("Preenchimento") && fa > 0.001f) fill(bb, r, g, b, fa);
        if (m.b("Contorno")) {
            GL11.glLineWidth((float) m.n("Espessura"));
            GlStateManager.color(r, g, b, (float) m.n("Opacidade contorno"));
            RenderGlobal.drawSelectionBoundingBox(bb);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private void fill(AxisAlignedBB b, float r, float g, float bl, float a) {
        double[][] f = {
            {b.minX, b.minY, b.minZ, b.maxX, b.minY, b.minZ, b.maxX, b.minY, b.maxZ, b.minX, b.minY, b.maxZ},
            {b.minX, b.maxY, b.minZ, b.minX, b.maxY, b.maxZ, b.maxX, b.maxY, b.maxZ, b.maxX, b.maxY, b.minZ},
            {b.minX, b.minY, b.minZ, b.minX, b.maxY, b.minZ, b.maxX, b.maxY, b.minZ, b.maxX, b.minY, b.minZ},
            {b.minX, b.minY, b.maxZ, b.maxX, b.minY, b.maxZ, b.maxX, b.maxY, b.maxZ, b.minX, b.maxY, b.maxZ},
            {b.minX, b.minY, b.minZ, b.minX, b.minY, b.maxZ, b.minX, b.maxY, b.maxZ, b.minX, b.maxY, b.minZ},
            {b.maxX, b.minY, b.minZ, b.maxX, b.maxY, b.minZ, b.maxX, b.maxY, b.maxZ, b.maxX, b.minY, b.maxZ}
        };
        Tessellator ts = Tessellator.getInstance();
        WorldRenderer w = ts.getWorldRenderer();
        w.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < f.length; i++) {
            for (int k = 0; k < 4; k++) {
                w.pos(f[i][k * 3], f[i][k * 3 + 1], f[i][k * 3 + 2]).color(r, g, bl, a).endVertex();
            }
        }
        ts.draw();
    }

    // ---------- Nametag ----------
    private boolean canShow(EntityPlayer en, EntityPlayerSP me) {
        boolean vis = !en.isInvisibleToPlayer(me);
        Team t = en.getTeam();
        if (t != null) {
            Team mine = me.getTeam();
            switch (t.getNameTagVisibility()) {
                case ALWAYS:
                    return vis;
                case NEVER:
                    return false;
                case HIDE_FOR_OTHER_TEAMS:
                    return mine == null ? vis : t.isSameTeam(mine) && (t.getSeeFriendlyInvisiblesEnabled() || vis);
                case HIDE_FOR_OWN_TEAM:
                    return mine == null ? vis : !t.isSameTeam(mine) && vis;
                default:
                    return true;
            }
        }
        return vis;
    }

    @SubscribeEvent
    public void onSpecials(RenderLivingEvent.Specials.Pre e) {
        Module m = ModuleManager.nametag;
        if (!m.enabled || !(e.entity instanceof EntityPlayer)) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP me = mc.thePlayer;
        if (me == null) return;
        EntityPlayer en = (EntityPlayer) e.entity;
        e.setCanceled(true);
        boolean self = en == me;
        if (self) {
            if (!m.b("Mostrar propria (F5)") || mc.gameSettings.thirdPersonView == 0) return;
        } else if (!canShow(en, me)) {
            return;
        }
        if (!Minecraft.isGuiEnabled() || en.riddenByEntity != null) return;

        boolean sneak = en.isSneaking();
        double d0 = en.getDistanceSqToEntity(me);
        float lim = sneak ? 32f : 64f;
        if (d0 >= lim * lim) return;

        String name = en.getDisplayName().getFormattedText();
        if (m.b("Mostrar vida")) name += " \u00a7c" + (int) Math.ceil(en.getHealth());

        double y = e.y;
        if (!self) {
            Scoreboard sb = en.getWorldScoreboard();
            ScoreObjective ob = sb.getObjectiveInDisplaySlot(2);
            if (ob != null && d0 < 100.0D * 100.0D) {
                Score sc = sb.getValueFromObjective(en.getName(), ob);
                drawTag(sc.getScorePoints() + " " + ob.getDisplayName(), e.x, y, e.z, en, m, sneak);
                y += mc.fontRendererObj.FONT_HEIGHT * 1.15D * 0.02666667D;
            }
        }
        drawTag(name, e.x, y, e.z, en, m, sneak);
    }

    private void drawTag(String str, double x, double y, double z, EntityPlayer en, Module m, boolean sneak) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        float f1 = 0.016666668F * 1.6F * (float) m.n("Escala");

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + en.height + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.translate(0.0F, 0.25F / f1, 0.0F);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int j = fr.getStringWidth(str) / 2;
        if (m.b("Fundo")) {
            int rgb = Ui.color(m.n("Cor do fundo"));
            float br = ((rgb >> 16) & 255) / 255f;
            float bg = ((rgb >> 8) & 255) / 255f;
            float bb = (rgb & 255) / 255f;
            float a = (float) m.n("Opacidade do fundo");
            if (a > 0.001f) {
                Tessellator ts = Tessellator.getInstance();
                WorldRenderer w = ts.getWorldRenderer();
                GlStateManager.disableTexture2D();
                w.begin(7, DefaultVertexFormats.POSITION_COLOR);
                w.pos(-j - 1, -1, 0.0D).color(br, bg, bb, a).endVertex();
                w.pos(-j - 1, 8, 0.0D).color(br, bg, bb, a).endVertex();
                w.pos(j + 1, 8, 0.0D).color(br, bg, bb, a).endVertex();
                w.pos(j + 1, -1, 0.0D).color(br, bg, bb, a).endVertex();
                ts.draw();
                GlStateManager.enableTexture2D();
            }
        }

        boolean sh = m.b("Sombra no texto");
        int x0 = -fr.getStringWidth(str) / 2;
        if (!sneak) {
            fr.drawString(str, x0, 0, 553648127, sh);
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fr.drawString(str, x0, 0, sneak ? 0x60FFFFFF : 0xFFFFFFFF, sh);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    // ---------- Tab com ping ----------
    @SubscribeEvent
    public void onOverlayPre(RenderGameOverlayEvent.Pre e) {
        if (e.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) return;
        Module m = ModuleManager.tab;
        if (!m.enabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;
        e.setCanceled(true);
        if (!mc.gameSettings.keyBindPlayerList.isKeyDown()) return;
        drawTab(mc, e.resolution.getScaledWidth(), m);
    }

    private int pingColor(int p) {
        if (p <= 0) return 0xFFAAAAAA;
        if (p < 80) return 0xFF55FF55;
        if (p < 150) return 0xFFFFFF55;
        if (p < 250) return 0xFFFFAA00;
        return 0xFFFF5555;
    }

    private void drawTab(Minecraft mc, int sw, Module m) {
        FontRenderer fr = mc.fontRendererObj;
        List<NetworkPlayerInfo> list = new ArrayList<NetworkPlayerInfo>(mc.getNetHandler().getPlayerInfoMap());
        Collections.sort(list, CMP);
        if (list.size() > 80) list = new ArrayList<NetworkPlayerInfo>(list.subList(0, 80));
        int n = list.size();
        if (n == 0) return;
        boolean showPing = m.b("Mostrar ping");
        boolean heads = m.b("Mostrar cabecas");
        int rows = Math.min(20, n);
        int cols = (n + rows - 1) / rows;

        String[] names = new String[n];
        String[] pings = new String[n];
        int[] colW = new int[cols];
        for (int i = 0; i < n; i++) {
            NetworkPlayerInfo info = list.get(i);
            names[i] = mc.ingameGUI.getTabList().getPlayerName(info);
            int p = info.getResponseTime();
            pings[i] = p <= 0 ? "?" : p + "ms";
            int w = fr.getStringWidth(names[i]) + (heads ? 10 : 0) + (showPing ? fr.getStringWidth(pings[i]) + 8 : 0);
            int c = i / rows;
            if (w > colW[c]) colW[c] = w;
        }
        int rowH = 10, pad = 3, gap = 6;
        int total = pad * 2 + (cols - 1) * gap;
        for (int c = 0; c < cols; c++) total += colW[c];
        int x0 = (sw - total) / 2;
        int y0 = 10;
        if (m.b("Fundo")) {
            int a = (int) (m.n("Opacidade do fundo") * 255);
            if (a > 0) Gui.drawRect(x0, y0 - 3, x0 + total, y0 + rows * rowH + 3, a << 24);
        }
        for (int i = 0; i < n; i++) {
            int c = i / rows, r = i % rows;
            int cx = x0 + pad;
            for (int k = 0; k < c; k++) cx += colW[k] + gap;
            int cy = y0 + r * rowH;
            NetworkPlayerInfo info = list.get(i);
            if (heads) {
                mc.getTextureManager().bindTexture(info.getLocationSkin());
                GlStateManager.color(1f, 1f, 1f, 1f);
                Gui.drawScaledCustomSizeModalRect(cx, cy, 8f, 8f, 8, 8, 8, 8, 64f, 64f);
            }
            fr.drawStringWithShadow(names[i], cx + (heads ? 10 : 0), cy, 0xFFFFFFFF);
            if (showPing) {
                fr.drawStringWithShadow(pings[i], cx + colW[c] - fr.getStringWidth(pings[i]), cy, pingColor(info.getResponseTime()));
            }
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
          }
