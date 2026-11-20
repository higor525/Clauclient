package com.clauclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cps {
    public static final List<Long> left = new ArrayList<Long>();
    public static final List<Long> right = new ArrayList<Long>();

    public static int count(List<Long> list) {
        long now = System.currentTimeMillis();
        Iterator<Long> it = list.iterator();
        while (it.hasNext()) {
            if (now - it.next() > 1000) it.remove();
        }
        return list.size();
    }
}
