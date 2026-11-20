package com.clauclient;

public class Setting {
    public static final int BOOL = 0, NUM = 1, TEXT = 2, ACTION = 3;
    public final String name;
    public final int type;
    public boolean bool;
    public double num, min, max, step = 1;
    public String text = "";
    public Runnable action;

    public Setting(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public void inc(int dir) {
        double v = num + dir * step;
        v = Math.round(v / step) * step;
        if (v < min) v = min;
        if (v > max) v = max;
        num = v;
    }

    public String display() {
        if (type == BOOL) return bool ? "ON" : "OFF";
        if (type == NUM) {
            if (step >= 1) return String.valueOf((int) Math.round(num));
            return String.format("%.2f", num);
        }
        return text;
    }
}