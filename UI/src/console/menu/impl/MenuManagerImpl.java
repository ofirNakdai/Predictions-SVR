package console.menu.impl;

import console.menu.api.Menu;
import console.menu.api.MenuManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MenuManagerImpl implements MenuManager {
    private final Map<String, Menu> name2menu;

    public MenuManagerImpl() {
        this.name2menu = new HashMap<>();
    }

    @Override
    public Menu createMenu(String menuName) {
        return new MenuImpl(menuName);

    }

    @Override
    public void addMenu(Menu menuToAdd) {
        this.name2menu.put(menuToAdd.getMenuName(), menuToAdd);
    }

    @Override
    public Collection<Menu> getMenus() {
        return this.name2menu.values();
    }

    @Override
    public Menu getMenuByName(String menuName) {
        if(this.name2menu.containsKey(menuName))
            return this.name2menu.get(menuName);
        throw new IllegalArgumentException("No menu with name: " + menuName);
    }

    @Override
    public void showMenuByName(String menuName) {
        if(this.name2menu.containsKey(menuName))
            this.name2menu.get(menuName).showMenu();
        else{
            throw new IllegalArgumentException("No menu with name: " + menuName);
        }
    }
}
