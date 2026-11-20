package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ClauItemRenderer extends ItemRenderer {
    private final Minecraft game;
    private ItemStack held;
    private int slot = -1;
    private float progress, prev;

    public ClauItemRenderer(Minecraft mc) {
        super(mc);
        this.game = mc;
    }

    @Override
    public void updateEquippedItem() {
        super.updateEquippedItem();
        EntityPlayerSP p = game.thePlayer;
        if (p == null) return;
        prev = progress;
        ItemStack cur = p.inventory.getCurrentItem();
        Module o = ModuleManager.oldAnim;
        boolean same;
        if (held == null && cur == null) {
            same = true;
        } else if (held == null || cur == null) {
            same = false;
        } else if (o.enabled && o.b("1.7 Item Update")) {
            same = (slot == p.inventory.currentItem && held == cur)
                    || (held != cur && held.getItem() == cur.getItem() && held.getItemDamage() == cur.getItemDamage());
            if (same) held = cur;
        } else {
            same = ItemStack.areItemStacksEqual(held, cur);
            if (same) held = cur;
        }
        float target = same ? 1.0F : 0.0F;
        float d = MathHelper.clamp_float(target - progress, -0.4F, 0.4F);
        progress += d;
        if (progress < 0.1F) {
            held = cur;
            slot = p.inventory.currentItem;
        }
    }

    @Override
    public void resetEquippedProgress() {
        super.resetEquippedProgress();
        progress = 0.0F;
    }

    @Override
    public void resetEquippedProgress2() {
        super.resetEquippedProgress2();
        progress = 0.0F;
    }

    private void base(float equip, float swing) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equip * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float a = MathHelper.sin(swing * swing * (float) Math.PI);
        float b = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        GlStateManager.rotate(a * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(b * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(b * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void block() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private void drink(EntityPlayerSP p, float pt) {
        float f = (float) p.getItemInUseCount() - pt + 1.0F;
        float f1 = f / (float) held.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
        if (f1 >= 0.8F) f2 = 0.0F;
        GlStateManager.translate(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float) Math.pow((double) f1, 27.0D);
        GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, 0.0F);
        GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    private void bow(EntityPlayerSP p, float pt) {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = (float) held.getMaxItemUseDuration() - ((float) p.getItemInUseCount() - pt + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;
        if (f1 > 1.0F) f1 = 1.0F;
        if (f1 > 0.1F) {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }
        GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }

    private void itemUsed(float swing) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swing * (float) Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void pose(Module o) {
        Item it = held.getItem();
        if (it instanceof ItemSword) {
            if (o.b("1.7 Sword Animation")) {
                GlStateManager.translate(-0.06F, 0.08F, 0.0F);
            }
        } else if (it instanceof ItemFishingRod) {
            if (o.b("1.7 Rod Animation")) {
                GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(-0.04F, 0.04F, 0.0F);
            }
        } else if (o.b("1.7 Item Held Animation")) {
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
        }
    }

    @Override
    public void renderItemInFirstPerson(float pt) {
        Module o = ModuleManager.oldAnim;
        EntityPlayerSP p = game.thePlayer;
        if (!o.enabled || p == null || game.theWorld == null || held == null || held.getItem() == Items.filled_map) {
            super.renderItemInFirstPerson(pt);
            return;
        }

        float equip = 1.0F - (prev + (progress - prev) * pt);
        float swing = p.getSwingProgress(pt);
        float pitch = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * pt;
        float yaw = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * pt;

        GlStateManager.pushMatrix();
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        int light = game.theWorld.getCombinedLight(new BlockPos(p.posX, p.posY + (double) p.getEyeHeight(), p.posZ), 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (light & 65535), (float) (light >> 16));

        float rp = p.prevRenderArmPitch + (p.renderArmPitch - p.prevRenderArmPitch) * pt;
        float ry = p.prevRenderArmYaw + (p.renderArmYaw - p.prevRenderArmYaw) * pt;
        GlStateManager.rotate((p.rotationPitch - rp) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((p.rotationYaw - ry) * 0.1F, 0.0F, 1.0F, 0.0F);

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        boolean left = o.b("Left Hand Animation");
        if (left) {
            GlStateManager.scale(-1.0F, 1.0F, 1.0F);
            GL11.glFrontFace(GL11.GL_CW);
        }

        boolean master = o.b("1.7 Swing Animation");
        if (p.getItemInUseCount() > 0) {
            EnumAction act = held.getItemUseAction();
            if (act == EnumAction.EAT || act == EnumAction.DRINK) {
                drink(p, pt);
                boolean on = act == EnumAction.EAT ? o.b("1.7 Eating Animation") : o.b("1.7 Drinking Animation");
                base(equip, (on || master) ? swing : 0.0F);
            } else if (act == EnumAction.BLOCK) {
                base(equip, (o.b("1.7 Blockhit") || master) ? swing : 0.0F);
                block();
            } else if (act == EnumAction.BOW) {
                base(equip, (o.b("1.7 Bow Animation") || master) ? swing : 0.0F);
                bow(p, pt);
            } else {
                base(equip, master ? swing : 0.0F);
            }
        } else {
            itemUsed(swing);
            base(equip, swing);
        }

        pose(o);
        this.renderItem(p, held, ItemCameraTransforms.TransformType.FIRST_PERSON);

        if (left) {
            GL11.glFrontFace(GL11.GL_CCW);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }
                              }
