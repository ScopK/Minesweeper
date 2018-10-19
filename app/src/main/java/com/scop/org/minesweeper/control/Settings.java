package com.scop.org.minesweeper.control;

import android.content.ContextWrapper;

public class Settings {

    public static final int DISABLED=3;
    public static final int EASY=0;
    public static final int NORMAL=1;
    public static final int HARD=2;
    public static final String FILENAME="state.save";

    private boolean showTime, firstOpen;
    private int discoveryMode;

    private static Settings settings;
    private Settings() {}
    public static Settings getInstance(){
        if (settings==null){
            settings = new Settings();
        }
        return settings;
    }

    public int getDiscoveryMode() {
        return discoveryMode;
    }
    public boolean isDiscoveryMode(int t) {
        return discoveryMode==t;
    }

    public void setDiscoveryMode(int discoveryMode) {
        this.discoveryMode = discoveryMode;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public boolean isFirstOpen() {
        return firstOpen;
    }

    public void setFirstOpen(boolean firstOpen) {
        this.firstOpen = firstOpen;
    }
}


