package com.example.myhealthlife.ui;

import com.example.myhealthlife.R;

public enum HomeNav {

    HOME(R.id.homeFragment, false),
    SLEEP(R.id.sleepFragment, true),
    BLOOD(R.id.bloodFragment, true),
    SUGAR(R.id.sugarFragment, true),
    TEMP(R.id.tempetureFragment, true),
    FAT(R.id.trigFragment, true),
    URIC(R.id.uricAcidFragment, true),
    HEART(R.id.heartFragment, true);

    private final int destinationId;
    private final boolean showsTopBar;

    HomeNav(int destinationId, boolean showsTopBar) {
        this.destinationId = destinationId;
        this.showsTopBar = showsTopBar;
    }

    public static boolean showsTopBar(int destinationId) {
        for (HomeNav nav : values()) {
            if (nav.destinationId == destinationId) {
                return nav.showsTopBar;
            }
        }
        return false;
    }

}



