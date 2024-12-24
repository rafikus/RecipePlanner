package com.rafikus.recipeplanner.utils;

import java.util.ArrayList;
import java.util.List;

public class MethodCollection {
    private final List<Runnable> methods;

    public MethodCollection() {
        this.methods = new ArrayList<>();
    }

    public void addMethod(Runnable method) {
        this.methods.add(method);
    }

    public void runMethods() {
        for (Runnable method : this.methods) {
            method.run();
        }
    }
}
