package engine.world.grid;

import engine.entity.EntityDefinition;
import engine.entity.EntityInstance;
import engine.world.grid.location.GridLocation;

import java.util.ArrayList;

public class Grid {
    ArrayList<ArrayList<EntityInstance>> grid;
    private final int rows;
    private final int cols;

    public Grid(int rowSize, int colSize) {
        ArrayList<EntityInstance> row = null;
        this.rows = rowSize;
        this.cols = colSize;

        this.grid = new ArrayList<>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            row = new ArrayList<>(colSize);
            for (int j = 0; j < colSize; j++) {
                row.add(null); // Set each element to null initially
            }
            grid.add(row);
        }
    }

    public void set(EntityInstance newEntity, GridLocation location) {
        int targetRow = location.getRow();
        int targetCol = location.getCol();

        if (targetRow >= 0 && targetRow < rows && targetCol >= 0 && targetCol < cols) {
            grid.get(targetRow).set(targetCol, newEntity);
            if(newEntity != null){
//                System.out.println("Matrix cell at row " + targetRow + ", col " + targetCol + " set to " + newEntity.getName());
                newEntity.setGridLocation(location);
            }
        }
//        else {
//            System.out.println("Invalid cell coordinates.");
////            throw new InValidCellCoordinatesException(targetRow, targetCol, this.rows, this.cols);
//        }
    }

    public EntityInstance get(GridLocation location){
        int targetRow = location.getRow();
        int targetCol = location.getCol();

        return grid.get(targetRow).get(targetCol);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isOccupied(GridLocation location){
        return this.get(location) != null;
    }

    public void clearGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid.get(i).set(j, null);
            }
        }
    }
}
