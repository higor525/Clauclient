package com.clauclient;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

@Mod(modid = "clauclient", name = "ClauClient", version = "1.0")
public class ClauClient {

    public static ScoreObjective hiddenObjective;

    private KeyBinding menuKey, zoomKey, perspKey;

    private boolean zooming;
    private float savedFov;

    private boolean perspActive, swapped;
    private int savedView;
    private float camYaw, camPitch, rYaw, rPitch, rpYaw, rpPitch;

    private float smoothEye = 1.62f;
    private long lastNs;
    private boolean eyeTouched;

    private Field clickField;
    private boolean clickTried;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        menuKey = new KeyBinding("Abrir menu", Keyboard.KEY_K, "ClauClient");
        zoomKey = new KeyBinding("Zoom", Keyboard.KEY_C, "ClauClient");
        perspKey = new KeyBinding("Perspectiva", Keyboard.KEY_P, "ClauClient");
        ClientRegistry.registerKeyBinding(menuKey);
        ClientRegistry.registerKeyBinding(zoomKey);
        ClientRegistry.registerKeyBinding(perspKey);
        MinecraftForge.EVENT_BUS.register(this);
MinecraftForge.EVENT_BUS.register(new MenuEvents());
        Config.load();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Config.save();
            }
        }));
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.dwheel != 0 && zooming) {
            ModuleManager.zoom.get("Zoom").inc(event.dwheel > 0 ? 1 : -1);
            event.setCanceled(true);
            return;
        }
        if (event.buttonstate) {
            if (event.button == 0) Cps.left.add(System.currentTimeMillis());
            else if (event.button == 1) Cps.right.add(System.currentTimeMillis());
        }
    }

    private void fixHitDelay(Minecraft mc) {
        if (!clickTried) {
            clickTried = true;
            try {
                clickField = ReflectionHelper.findField(Minecraft.class, "leftClickCounter", "field_71429_W");
            } catch (Exception e) {
                clickField = null;
            }
        }
        if (clickField != null) {
            try {
                clickField.setInt(mc, 0);
            } catch (Exception e) {
                clickField = null;
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();

        if (menuKey.isPressed() && mc.currentScreen == null) {
            mc.displayGuiScreen(new ClickGui());
        }

        if (ModuleManager.hitFix.enabled) fixHitDelay(mc);

        for (Module m : ModuleManager.modules) {
            if (m.enabled) m.onTick();
        }

        if (perspKey.isPressed() && mc.thePlayer != null && ModuleManager.persp.enabled) {
            if (!perspActive) {
                camYaw = mc.thePlayer.rotationYaw;
                camPitch = mc.thePlayer.rotationPitch;
                savedView = mc.gameSettings.thirdPersonView;
                mc.gameSettings.thirdPersonView = 1;
                perspActive = true;
            } else {
                stopPersp(mc);
            }
        }
        if (perspActive && !ModuleManager.persp.enabled) stopPersp(mc);
    }

    private void stopPersp(Minecraft mc) {
        perspActive = false;
        mc.gameSettings.thirdPersonView = savedView;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.thePlayer;

        if (e.phase == TickEvent.Phase.END) {
            if (swapped) {
                if (p != null) {
                    camYaw = p.rotationYaw;
                    camPitch = p.rotationPitch;
                    p.rotationYaw = rYaw;
                    p.rotationPitch = rPitch;
                    p.prevRotationYaw = rpYaw;
                    p.prevRotationPitch = rpPitch;
                }
                swapped = false;
            }
            return;
        }

        if (ModuleManager.noPart.enabled && mc.theWorld != null) {
            mc.effectRenderer.clearEffects(mc.theWorld);
        }

        boolean z = ModuleManager.zoom.enabled && zoomKey.isKeyDown() && mc.currentScreen == null;
        if (z) {
            if (!zooming) {
                savedFov = mc.gameSettings.fovSetting;
                zooming = true;
            }
            mc.gameSettings.fovSetting = savedFov / (float) ModuleManager.zoom.n("Zoom");
        } else if (zooming) {
            mc.gameSettings.fovSetting = savedFov;
            zooming = false;
        }

        long now = System.nanoTime();
        float dt = (now - lastNs) / 1.0e9f;
        lastNs = now;
        if (dt > 0.1f || dt < 0f) dt = 0.1f;

        if (p == null) return;

        if (ModuleManager.hurt.enabled) p.hurtTime = 0;

        Module old = ModuleManager.oldAnim;
        if (old.enabled && old.b("Sneak suave")) {
            float target = p.isSneaking() ? 1.54f : 1.62f;
            float k = 1f - (float) Math.exp(-old.n("Velocidade do sneak") * dt);
            smoothEye += (target - smoothEye) * k;
            p.eyeHeight = smoothEye + (p.isSneaking() ? 0.08f : 0f);
            eyeTouched = true;
        } else if (eyeTouched) {
            p.eyeHeight = 1.62f;
            smoothEye = 1.62f;
            eyeTouched = false;
        }

        if (perspActive) {
            rYaw = p.rotationYaw;
            rPitch = p.rotationPitch;
            rpYaw = p.prevRotationYaw;
            rpPitch = p.prevRotationPitch;
            p.rotationYaw = camYaw;
            p.prevRotationYaw = camYaw;
            p.rotationPitch = camPitch;
            p.prevRotationPitch = camPitch;
            swapped = true;
        }
    }

    @SubscribeEvent
    public void onHand(RenderHandEvent e) {
        Module o = ModuleManager.oldAnim;
        if (!o.enabled) return;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if (p == null) return;
        GlStateManager.translate((float) o.n("Item X"), (float) o.n("Item Y"), (float) o.n("Item Z"));
        float sc = (float) o.n("Item tamanho");
        GlStateManager.scale(sc, sc, sc);
        if (p.isUsingItem() && p.getHeldItem() != null && p.getHeldItem().getItem() instanceof ItemSword) {
            GlStateManager.translate((float) o.n("Bloqueio X"), (float) o.n("Bloqueio Y"), 0f);
        }
    }

    @SubscribeEvent
    public void onPre(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            if (ModuleManager.sidebar.enabled && mc.theWorld != null) {
                Scoreboard sb = mc.theWorld.getScoreboard();
                hiddenObjective = sb.getObjectiveInDisplaySlot(1);
                if (hiddenObjective != null) sb.setObjectiveInDisplaySlot(1, null);
            }
        } else if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && ModuleManager.crosshair.enabled) {
            if (mc.gameSettings.thirdPersonView == 0 && !mc.gameSettings.showDebugInfo) {
                Module c = ModuleManager.crosshair;
                CrosshairData.draw(event.resolution.getScaledWidth(), event.resolution.getScaledHeight(),
                        (float) c.n("Escala"), 0xFF000000 | Ui.color(c.n("Cor")), c.b("Contorno"));
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPost(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && hiddenObjective != null) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                mc.theWorld.getScoreboard().setObjectiveInDisplaySlot(1, hiddenObjective);
            }
            hiddenObjective = null;
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen instanceof HudEditor) return;
        for (Module m : ModuleManager.modules) {
            if (m instanceof HudModule && m.enabled) {
                ((HudModule) m).render();
            }
        }
    }
}