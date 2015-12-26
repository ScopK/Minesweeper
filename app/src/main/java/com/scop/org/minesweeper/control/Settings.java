package com.scop.org.minesweeper.control;

/**
 * Created by Oscar on 26/12/2015.
 */
public class Settings {

    public static final int EASY=0;
    public static final int NORMAL=1;
    public static final int HARD=2;
    public static final int HARDEST=3;
    public static final String SAVE_STATE_PATH = "/sdcard/Minesweeper/savesstate.save";

    private boolean showTime;
    private int discoveryMode;

    private static Settings settings;
    private Settings() {}
    public Settings getInstance(){
        if (settings==null){
            settings = new Settings();
        }
        return settings;
    }

    public int getDiscoveryMode() {
        return discoveryMode;
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
}


