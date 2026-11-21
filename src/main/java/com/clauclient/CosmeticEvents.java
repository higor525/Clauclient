package com.clauclient;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CosmeticEvents {

    public static final ResourceLocation SHIELD = new ResourceLocation("clauclient", "textures/shield.png");
    private static boolean texChecked, hasTex;
    private static boolean installed;

    // ---------- Glint Colorize: slot da hotbar colorido ----------
    @SubscribeEvent
    public void onHotbar(RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        Module m = ModuleManager.glint;
        if (m == null || !m.enabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) return;
        int sw = e.resolution.getScaledWidth();
        int sh = e.resolution.getScaledHeight();
        int a = (int) (m.n("Opacidade") * 255);
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.inventory.mainInventory[i];
            if (s == null || !(s.getItem() instanceof ItemPotion)) continue;
            int rgb;
            if (m.b("Cor da pocao")) rgb = s.getItem().getColorFromItemStack(s, 0);
            else rgb = Ui.color(m.n("Cor fixa"));
            rgb &= 0xFFFFFF;
            int x = sw / 2 - 90 + i * 20 + 2;
            int y = sh - 16 - 3;
            Gui.drawRect(x, y, x + 16, y + 16, (a << 24) | rgb);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    // ---------- Escudo no braco ----------
    public static void installShield() {
        if (installed) return;
        installed = true;
        try {
            Map<String, RenderPlayer> map = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            for (RenderPlayer rp : map.values()) {
                rp.addLayer(new ShieldLayer(rp));
            }
        } catch (Exception ex) {
            System.out.println("[ClauClient] nao consegui instalar o escudo");
            ex.printStackTrace();
        }
    }

    private static boolean hasTexture() {
        if (!texChecked) {
            texChecked = true;
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(SHIELD).getInputStream().close();
                hasTex = true;
            } catch (Exception ex) {
                hasTex = false;
            }
        }
        return hasTex;
    }

    public static class ShieldLayer implements LayerRenderer<EntityLivingBase> {
        private final RenderPlayer rp;

        public ShieldLayer(RenderPlayer rp) {
            this.rp = rp;
        }

        public void doRenderLayer(EntityLivingBase e, float ls, float lsa, float pt, float age, float yaw, float pitch, float scale) {
            Module m = ModuleManager.shield;
            if (m == null || !m.enabled) return;
            if (e != Minecraft.getMinecraft().thePlayer || e.isInvisible()) return;

            ModelPlayer model = rp.getMainModel();
            float sc = (float) m.n("Tamanho");
            float px = 0.0625f;
            float x = 3.4f * px;
            float cy = (4.0f + (float) m.n("Posicao Y")) * px;
            float cz = (float) m.n("Posicao Z") * px;
            float hh = 4.0f * sc * px;
            float hw = 3.0f * sc * px;
            float y0 = cy - hh, y1 = cy + hh;
            float z0 = cz - hw, z1 = cz + hw;

            GlStateManager.pushMatrix();
            if (e.isSneaking()) GlStateManager.translate(0.0F, 0.2F, 0.0F);
            model.bipedLeftArm.postRender(0.0625F);

            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator ts = Tessellator.getInstance();
            WorldRenderer w = ts.getWorldRenderer();

            if (hasTexture()) {
                GlStateManager.color(1f, 1f, 1f, 1f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(SHIELD);
                w.begin(7, DefaultVertexFormats.POSITION_TEX);
                w.pos(x, y0, z0).tex(0, 0).endVertex();
                w.pos(x, y1, z0).tex(0, 1).endVertex();
                w.pos(x, y1, z1).tex(1, 1).endVertex();
                w.pos(x, y0, z1).tex(1, 0).endVertex();
                ts.draw();
            } else {
                GlStateManager.disableTexture2D();
                w.begin(7, DefaultVertexFormats.POSITION_COLOR);
                w.pos(x, y0, z0).color(0.61f, 0.19f, 1.0f, 1f).endVertex();
                w.pos(x, y1, z0).color(0.61f, 0.19f, 1.0f, 1f).endVertex();
                w.pos(x, y1, z1).color(0.61f, 0.19f, 1.0f, 1f).endVertex();
                w.pos(x, y0, z1).color(0.61f, 0.19f, 1.0f, 1f).endVertex();
                float ix = x + 0.002f;
                float m2 = 0.5f * px * sc;
                w.pos(ix, y0 + m2, z0 + m2).color(0.07f, 0.04f, 0.12f, 1f).endVertex();
                w.pos(ix, y1 - m2, z0 + m2).color(0.07f, 0.04f, 0.12f, 1f).endVertex();
                w.pos(ix, y1 - m2, z1 - m2).color(0.07f, 0.04f, 0.12f, 1f).endVertex();
                w.pos(ix, y0 + m2, z1 - m2).color(0.07f, 0.04f, 0.12f, 1f).endVertex();
                ts.draw();
                GlStateManager.enableTexture2D();
            }

            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.popMatrix();
        }

        public boolean shouldCombineTextures() {
            return false;
        }
    }
}