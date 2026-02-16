package com.example.myhealthlife.ui.home;

public class HomeCardState {

    public final int iconRes;
    public final int titleRes;
    public final String value;
    public final String unit ;

    public HomeCardState(
            int iconRes,
            int titleRes,
            String value,
            String unit
    ) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.value = value;
        this.unit = unit;
    }
}

