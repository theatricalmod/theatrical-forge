package com.georlegacy.general.theatrical.inventory.tabs.base;

import com.georlegacy.general.theatrical.inventory.tabs.GelTab;

public class TabManager {

    public TabManager() {
        gelTab = new GelTab();
    }

    private GelTab gelTab;

    public GelTab getGelTab() {
        return gelTab;
    }

}
