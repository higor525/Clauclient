package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "clauclient", name = "ClauClient", version = "1.0")
public class ClauClient {

    private KeyBinding menuKey;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        menuKey = new KeyBinding("Abrir menu", Keyboard.KEY_K, "ClauClient");
        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(this);
        Config.load();
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.buttonstate) {
            if (event.button == 0) Cps.left.add(System.currentTimeMillis());
            else if (event.button == 1) Cps.right.add(System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();

        if (menuKey.isPressed() && mc.currentScreen == null) {
            mc.displayGuiScreen(new ClickGui());
        }

        for (Module m : ModuleManager.modules) {
            if (m.enabled) m.onTick();
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

        int ry = 4;
        int w = event.resolution.getScaledWidth();
        for (Module m : ModuleManager.modules) {
            if (m.enabled && !(m instanceof HudModule)) {
                int tw = mc.fontRendererObj.getStringWidth(m.name);
                mc.fontRendererObj.drawStringWithShadow(m.name, w - tw - 4, ry, 0x55FFFF);
                ry += 10;
            }
        }
    }
}