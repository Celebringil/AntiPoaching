package antipoaching.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the patrol area as a 2D grid of cells
 */
public class Grid {
    private int size;
    private Cell[][] cells;

    public Grid(int size) {
        this.size = size;
        this.cells = new Cell[size][size];

        // Initialize empty cells
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    /**
     * Initialize grid from map data
     */
    public void initializeFromMaps(double[][] riskMap, boolean[][] animalMap, int[][] terrainMap) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].setRiskLevel(riskMap[row][col]);
                cells[row][col].setHasAnimal(animalMap[row][col]);
                cells[row][col].setPassable(terrainMap[row][col] == 1);
            }
        }
    }

    /**
     * Get a cell at specific coordinates
     */
    public Cell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return cells[row][col];
        }
        return null;
    }

    /**
     * Check if coordinates are within grid bounds
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    /**
     * Get all valid neighboring cells (up, down, left, right)
     */
    public List<Cell> getNeighbors(int row, int col) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidPosition(newRow, newCol) && cells[newRow][newCol].isPassable()) {
                neighbors.add(cells[newRow][newCol]);
            }
        }

        return neighbors;
    }

    /**
     * Get all passable cells in the grid
     */
    public List<Cell> getPassableCells() {
        List<Cell> passable = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col].isPassable()) {
                    passable.add(cells[row][col]);
                }
            }
        }
        return passable;
    }

    /**
     * Calculate total risk across the grid
     */
    public double calculateTotalRisk() {
        double total = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (cells[row][col].isPassable()) {
                    total += cells[row][col].getRiskLevel();
                }
            }
        }
        return total;
    }

    /**
     * Get coverage matrix showing visit counts
     */
    public int[][] getCoverageMatrix() {
        int[][] coverage = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                coverage[row][col] = cells[row][col].getVisitCount();
            }
        }
        return coverage;
    }

    public int getSize() { 
        return size; 
    }
    public Cell[][] getCells() { 
        return cells; 
    }
}
