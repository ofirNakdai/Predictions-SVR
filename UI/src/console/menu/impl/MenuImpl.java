package console.menu.impl;

import console.menu.api.Menu;
import console.menu.api.MenuItem;

import java.util.*;

public class MenuImpl implements Menu {
    final String menuName;
    final List<MenuItem> items;
    int itemsCounter;

    public MenuImpl(String menuName) {
        this.menuName = menuName;
        this.itemsCounter = 0;
        items = new ArrayList<>();
    }

    @Override
    public Collection<MenuItem> getItems() {
        return this.items;
    }

    @Override
    public MenuItem getMenuItemBySerialNum(int itemSerial) {
        for(MenuItem item : this.items){
            if(item.getSerialNum() == itemSerial)
                return item;
        }
        throw new IndexOutOfBoundsException("Serial number: " + itemSerial + " does not exist!");
    }

    @Override
    public void addItem(String itemTitle) {
        this.itemsCounter++;
        this.items.add(new MenuItemImpl(this.itemsCounter, itemTitle));
    }

    @Override
    public void showMenu() {
        this.items.forEach(MenuItem::showItem);
    }

    @Override
    public String getMenuName() {
        return this.menuName;
    }

    @Override
    public boolean isInRange(int choiceNum) {
        if(itemsCounter == 0)
            return false;
        else return choiceNum > 0 && choiceNum <= itemsCounter;
    }

    @Override
    public int getValidInput() {
        Scanner scanner;
        boolean isValid = false;
        int userChoice = -1;

        System.out.println("Please enter your choice: ");

        while (!isValid) {
            try {
                scanner = new Scanner(System.in);
                userChoice = scanner.nextInt();
                isValid = isInRange(userChoice);
                if(!isValid){
                    System.out.println("Number not in range, please enter a number between 1 - " + this.itemsCounter + ":");
                }
            }
            catch (InputMismatchException mismatchException) {
                System.out.println("Not a number, try again (digits only):");
            }
        }

        return userChoice;
    }
}
