package engine.world.grid.location;

public class GridLocation {
    int row;
    int col;

    public GridLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public GridLocation(){}

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
