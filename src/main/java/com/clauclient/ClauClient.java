package com.clauclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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
        if (mc.thePlayer == null) return;

        int y = 4;
        if (ModuleManager.get("FPS").enabled) {
            mc.fontRendererObj.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), 4, y, 0xFFFFFF);
            y += 10;
        }
        if (ModuleManager.get("Coordenadas").enabled) {
            String xyz = String.format("XYZ: %.0f %.0f %.0f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            mc.fontRendererObj.drawStringWithShadow(xyz, 4, y, 0xFFFFFF);
        }

        int ry = 4;
        int w = event.resolution.getScaledWidth();
        for (Module m : ModuleManager.modules) {
            if (m.enabled) {
                int tw = mc.fontRendererObj.getStringWidth(m.name);
                mc.fontRendererObj.drawStringWithShadow(m.name, w - tw - 4, ry, 0x55FFFF);
                ry += 10;
            }
        }
    }
}