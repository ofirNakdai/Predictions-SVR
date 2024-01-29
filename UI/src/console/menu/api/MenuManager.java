package console.menu.api;

import java.util.Collection;

public interface MenuManager {
    Menu createMenu(String menuName);
    void addMenu(Menu menuToAdd);
    Collection<Menu> getMenus();
    Menu getMenuByName(String menuName);
    void showMenuByName(String menuName);
}
