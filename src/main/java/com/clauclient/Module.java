package com.clauclient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<Setting>();

    public Module(String name) {
        this(name, false);
    }

    public Module(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    protected Setting bool(String n, boolean def) {
        Setting s = new Setting(n, Setting.BOOL);
        s.bool = def;
        settings.add(s);
        return s;
    }

    protected Setting num(String n, double def, double min, double max, double step) {
        Setting s = new Setting(n, Setting.NUM);
        s.num = def;
        s.min = min;
        s.max = max;
        s.step = step;
        settings.add(s);
        return s;
    }

    protected Setting text(String n, String def) {
        Setting s = new Setting(n, Setting.TEXT);
        s.text = def;
        settings.add(s);
        return s;
    }

    protected Setting action(String n, Runnable r) {
        Setting s = new Setting(n, Setting.ACTION);
        s.action = r;
        settings.add(s);
        return s;
    }

    public Setting get(String n) {
        for (Setting s : settings) {
            if (s.name.equals(n)) return s;
        }
        return null;
    }

    public boolean b(String n) { return get(n).bool; }
    public double n(String n) { return get(n).num; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
}