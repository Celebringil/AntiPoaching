package antipoaching.algorithm;

import antipoaching.model.Cell;
import antipoaching.model.Grid;
import antipoaching.model.Ranger;

import java.util.List;

/**
 * Greedy strategy for patrol route optimization
 * At each step, choose the neighboring cell with the highest score
 */
public class GreedyStrategy {

    /**
     * Execute one step for a ranger using greedy selection
     * @param ranger The ranger to move
     * @param grid The patrol grid
     * @return true if move was successful, false if no valid moves
     */
    public boolean executeStep(Ranger ranger, Grid grid) {
        if (!ranger.canMove()) {
            return false;
        }

        List<Cell> neighbors = grid.getNeighbors(ranger.getCurrentRow(), ranger.getCurrentCol());

        if (neighbors.isEmpty()) {
            return false;
        }

        // Find the best neighbor based on score
        Cell bestCell = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Cell neighbor : neighbors) {
            double score = neighbor.calculateScore();
            if (score > bestScore) {
                bestScore = score;
                bestCell = neighbor;
            }
        }

        if (bestCell != null) {
            // Move ranger and mark cell as visited
            ranger.moveTo(bestCell.getRow(), bestCell.getCol());
            bestCell.visit();
            return true;
        }

        return false;
    }

    /**
     * Execute all steps for a ranger
     * @param ranger The ranger to move
     * @param grid The patrol grid
     */
    public void executeAllSteps(Ranger ranger, Grid grid) {
        // Mark starting cell as visited
        Cell startCell = grid.getCell(ranger.getCurrentRow(), ranger.getCurrentCol());
        if (startCell != null) {
            startCell.visit();
        }

        // Continue until ranger can't move anymore
        while (executeStep(ranger, grid)) {
            // Keep moving
        }
    }
}
