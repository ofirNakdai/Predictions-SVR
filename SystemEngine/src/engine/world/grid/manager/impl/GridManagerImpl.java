package engine.world.grid.manager.impl;

import engine.entity.EntityInstance;
import engine.world.grid.Grid;
import engine.world.grid.location.GridLocation;
import engine.world.grid.manager.api.GridManagerAPI;

import java.util.*;

public class GridManagerImpl implements GridManagerAPI {
    private Grid grid = null;
    private List<GridLocation> nonOccupiedLocations = null;


    @Override
    public void setEmptyGrid(int rowSize, int colSize) {
        this.grid = new Grid(rowSize, colSize);

        nonOccupiedLocations = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                nonOccupiedLocations.add(new GridLocation(i,j));
            }
        }
    }

    private int getRandomEmptyLocationIndexInList(){
        Random random = new Random();

        // Check if there are empty locations
        if (nonOccupiedLocations.isEmpty()) {
            return -1; // No empty locations found
        }

        // Generate a random index from the list of empty indices
        return random.nextInt(nonOccupiedLocations.size());
    }
    @Override
    public void initEntitiesRandomly(Map<String, List<EntityInstance>> name2EntInstancesList) {
        name2EntInstancesList.values().forEach(entityList -> entityList.forEach(this::initEntityLocationRandomly));
    }

    @Override
    public void initEntityLocationRandomly(EntityInstance entity){
        int randomIndex = getRandomEmptyLocationIndexInList();

        if(randomIndex == -1)
            throw new RuntimeException("No more available locations when initiating location to entity: " + entity.getName());

        GridLocation randomLocation = nonOccupiedLocations.get(randomIndex);
        grid.set(entity, randomLocation);
        entity.setGridLocation(randomLocation);

        nonOccupiedLocations.remove(randomLocation);
    }

    @Override
    public void makeRandomMoveToAllEntities(Map<String, List<EntityInstance>> name2EntInstancesList) {
        Random random = new Random();
        List<GridLocation> availableLocationsToMove = new ArrayList<>();

        name2EntInstancesList.values().forEach(entityList -> {
            entityList.forEach(entity -> {

                int entRow = entity.getGridLocation().getRow();
                int entCol = entity.getGridLocation().getCol();
                int numRows = grid.getRows(); // Total number of rows
                int numCols = grid.getCols(); // Total number of columns

                availableLocationsToMove.clear();

                // Check if the cell above is null or on the opposite edge
                int aboveRow = (entRow == 0) ? numRows - 1 : entRow - 1;
                GridLocation above = new GridLocation(aboveRow, entCol);
                if(!grid.isOccupied(above)){
                    // Cell above is null
                    availableLocationsToMove.add(above);
                }

                // Check if the cell below is null or on the opposite edge
                int belowRow = (entRow == numRows - 1) ? 0 : entRow + 1;
                GridLocation below = new GridLocation(belowRow, entCol);
                if(!grid.isOccupied(below)){
                    // Cell below is null
                    availableLocationsToMove.add(below);
                }

                // Check if the cell to the right is null or on the opposite edge
                int rightCol = (entCol == numCols - 1) ? 0 : entCol + 1;
                GridLocation right = new GridLocation(entRow, rightCol);
                if(!grid.isOccupied(right)){
                    // Cell on the right is null
                    availableLocationsToMove.add(right);
                }

                // Check if the cell to the left is null or on the opposite edge
                int leftCol = (entCol == 0) ? numCols - 1 : entCol - 1;
                GridLocation left = new GridLocation(entRow, leftCol);
                if(!grid.isOccupied(left)){
                    // Cell on the left is null
                    availableLocationsToMove.add(left);
                }

                if(!availableLocationsToMove.isEmpty()){

                    int moveOptionsListSize = availableLocationsToMove.size();
                    int randMoveIndex = random.nextInt(moveOptionsListSize);
                    GridLocation newRandomLocation = availableLocationsToMove.get(randMoveIndex);
                    GridLocation oldLocation = entity.getGridLocation();

                    grid.set(entity, newRandomLocation);
                    grid.set(null, oldLocation);

                    // remove new location from non occupied list
                    nonOccupiedLocations.forEach(location -> {
                        if(location.equals(newRandomLocation)){
                            nonOccupiedLocations.remove(location);
                        }
                    });

                    nonOccupiedLocations.add(oldLocation);
                }
            });
        });



    }

    @Override
    public void setEntityInLocation(EntityInstance newEntity, GridLocation location) {
        this.grid.set(newEntity, location);
    }

    @Override
    public void replaceEntitiesInLocation(EntityInstance outEntity, EntityInstance inEntity, GridLocation location) {
        if(this.grid.get(location) != null && this.grid.get(location).getId() == outEntity.getId()){
            this.grid.set(inEntity, location);
        }
    }

    @Override
    public void clearLocation(GridLocation gridLocation) {
        this.grid.set(null, gridLocation);
        nonOccupiedLocations.add(gridLocation);
    }

    @Override
    public boolean isEnt1NearEnt2(EntityInstance ent1, EntityInstance ent2, int depth){
        int row = ent1.getGridLocation().getRow();
        int col = ent1.getGridLocation().getCol();

        int gridRows = grid.getRows();
        int gridCols = grid.getCols();

        Set<GridLocation> nearbyLocations = new HashSet<>();

        for (int i = 0; i <= depth ; i++) {
            nearbyLocations.add(new GridLocation((((row - depth) % gridRows) + gridRows) % gridRows, (((col - i) % gridCols) + gridCols) % gridCols));
            nearbyLocations.add(new GridLocation((((row - depth) % gridRows) + gridRows) % gridRows, (((col + i) % gridCols) + gridCols) % gridCols));

            nearbyLocations.add(new GridLocation((((row + depth) % gridRows) + gridRows) % gridRows, (((col - i) % gridCols) + gridCols) % gridCols));
            nearbyLocations.add(new GridLocation((((row + depth) % gridRows) + gridRows) % gridRows, (((col + i) % gridCols) + gridCols) % gridCols));

            nearbyLocations.add(new GridLocation((((row - i) % gridRows) + gridRows) % gridRows, (((col - depth) % gridCols) + gridCols) % gridCols));
            nearbyLocations.add(new GridLocation((((row + i) % gridRows) + gridRows) % gridRows, (((col - depth) % gridCols) + gridCols) % gridCols));

            nearbyLocations.add(new GridLocation((((row - i) % gridRows) + gridRows) % gridRows, (((col + depth) % gridCols) + gridCols) % gridCols));
            nearbyLocations.add(new GridLocation((((row + i) % gridRows) + gridRows) % gridRows, (((col + depth) % gridCols) + gridCols) % gridCols));
        }

        for (GridLocation location: nearbyLocations) {
            if (grid.get(location) == ent2)
                return true;
        }

        return false;
    }
}
