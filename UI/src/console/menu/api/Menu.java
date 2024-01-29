package console.menu.api;

import java.util.Collection;

public interface Menu {
    Collection<MenuItem> getItems();
    MenuItem getMenuItemBySerialNum(int itemSerial);
    void addItem(String itemTitle);
    void showMenu();
    String getMenuName();
    boolean isInRange(int choiceNum);
    int getValidInput();

}
