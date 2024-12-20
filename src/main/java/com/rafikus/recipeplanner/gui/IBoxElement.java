package com.rafikus.recipeplanner.gui;

import org.jetbrains.annotations.Nullable;

public interface IBoxElement extends IElement {
    int padding(ScreenDirection direction);

    void setPadding(ScreenDirection direction, int value);
}