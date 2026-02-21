package antipoaching.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ranger who patrols the grid
 */
public class Ranger {
    private int id;
    private int currentRow;
    private int currentCol;
    private int maxSteps;
    private int stepsTaken;
    private List<int[]> path;  // History of positions visited

    public Ranger(int id, int startRow, int startCol, int maxSteps) {
        this.id = id;
        this.currentRow = startRow;
        this.currentCol = startCol;
        this.maxSteps = maxSteps;
        this.stepsTaken = 0;
        this.path = new ArrayList<>();

        // Record starting position
        this.path.add(new int[]{startRow, startCol});
    }

    /**
     * Move ranger to a new position
     */
    public boolean moveTo(int row, int col) {
        if (stepsTaken >= maxSteps) {
            return false;
        }

        this.currentRow = row;
        this.currentCol = col;
        this.path.add(new int[]{row, col});
        this.stepsTaken++;

        return true;
    }

    /**
     * Check if ranger can still move
     */
    public boolean canMove() {
        return stepsTaken < maxSteps;
    }

    /**
     * Get remaining steps
     */
    public int getRemainingSteps() {
        return maxSteps - stepsTaken;
    }

    /**
     * Get path as a list of coordinate pairs
     */
    public List<int[]> getPath() {
        return new ArrayList<>(path);
    }

    /**
     * Convert path to a format suitable for JSON
     */
    public List<List<Integer>> getPathAsList() {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] pos : path) {
            List<Integer> point = new ArrayList<>();
            point.add(pos[0]);
            point.add(pos[1]);
            result.add(point);
        }
        return result;
    }

    // Getters
    public int getId() { return id; }
    public int getCurrentRow() { return currentRow; }
    public int getCurrentCol() { return currentCol; }
    public int getMaxSteps() { return maxSteps; }
    public int getStepsTaken() { return stepsTaken; }

    @Override
    public String toString() {
        return String.format("Ranger[%d] at (%d,%d), steps: %d/%d",
            id, currentRow, currentCol, stepsTaken, maxSteps);
    }
}
