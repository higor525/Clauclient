package com.clauclient;

public abstract class Module {
    public final String name;
    public boolean enabled;

    public Module(String name) {
        this(name, false);
    }

    public Module(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
}
