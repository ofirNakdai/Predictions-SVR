package console.menu.impl;

import console.menu.api.MenuItem;

public class MenuItemImpl implements MenuItem {
    private final int id = -1;
    private final int serial;
    private final String title;

    public MenuItemImpl(int serial, String title) {
        this.serial = serial;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int getSerialNum() {
        return this.serial;
    }

    @Override
    public void showItem() {
        System.out.println("(" + this.serial + ") " + this.title);
    }
}
