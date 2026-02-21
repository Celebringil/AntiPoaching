package antipoaching.algorithm;

import antipoaching.model.Cell;
import antipoaching.model.Grid;
import antipoaching.model.Ranger;

import java.util.*;

/**
 * Main optimizer class that coordinates ranger patrol routes
 */
public class PatrolOptimizer {

    private Grid grid;
    private List<Ranger> rangers;
    private GreedyStrategy strategy;

    public PatrolOptimizer(Grid grid) {
        this.grid = grid;
        this.rangers = new ArrayList<>();
        this.strategy = new GreedyStrategy();
    }

    /**
     * Initialize rangers with strategic starting positions
     * @param rangerCount Number of rangers
     * @param maxSteps Maximum steps per ranger
     */
    public void initializeRangers(int rangerCount, int maxSteps) {
        rangers.clear();
        List<Cell> startPositions = findStartingPositions(rangerCount);

        for (int i = 0; i < rangerCount; i++) {
            Cell start = startPositions.get(i % startPositions.size());
            Ranger ranger = new Ranger(i, start.getRow(), start.getCol(), maxSteps);
            rangers.add(ranger);
        }
    }

    /**
     * Find good starting positions for rangers
     * Strategy: spread rangers across high-risk areas
     */
    private List<Cell> findStartingPositions(int count) {
        List<Cell> passableCells = grid.getPassableCells();

        // Sort by risk level (highest first)
        passableCells.sort((a, b) -> Double.compare(b.getRiskLevel(), a.getRiskLevel()));

        List<Cell> positions = new ArrayList<>();
        int gridSize = grid.getSize();

        // Try to spread rangers across the grid
        if (count >= 4) {
            // Use corners/edges for better coverage
            int[][] corners = {
                {0, 0}, {0, gridSize - 1},
                {gridSize - 1, 0}, {gridSize - 1, gridSize - 1}
            };

            for (int[] corner : corners) {
                Cell cell = findNearestPassable(corner[0], corner[1]);
                if (cell != null && !positions.contains(cell)) {
                    positions.add(cell);
                }
            }
        }

        // Fill remaining with high-risk cells
        for (Cell cell : passableCells) {
            if (positions.size() >= count) break;
            if (!positions.contains(cell)) {
                positions.add(cell);
            }
        }

        // If still not enough, use any passable cells
        if (positions.isEmpty()) {
            positions.addAll(passableCells.subList(0, Math.min(count, passableCells.size())));
        }

        return positions;
    }

    /**
     * Find nearest passable cell to given coordinates
     */
    private Cell findNearestPassable(int row, int col) {
        int gridSize = grid.getSize();

        // Spiral search outward
        for (int radius = 0; radius < gridSize; radius++) {
            for (int dr = -radius; dr <= radius; dr++) {
                for (int dc = -radius; dc <= radius; dc++) {
                    int r = row + dr;
                    int c = col + dc;
                    if (grid.isValidPosition(r, c)) {
                        Cell cell = grid.getCell(r, c);
                        if (cell.isPassable()) {
                            return cell;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Run the optimization algorithm
     */
    public void optimize() {
        // Run each ranger through the greedy strategy
        for (Ranger ranger : rangers) {
            strategy.executeAllSteps(ranger, grid);
        }
    }

    /**
     * Get all patrol routes
     */
    public List<Map<String, Object>> getRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();

        for (Ranger ranger : rangers) {
            Map<String, Object> route = new HashMap<>();
            route.put("rangerId", ranger.getId());
            route.put("path", ranger.getPathAsList());
            routes.add(route);
        }

        return routes;
    }

    /**
     * Get coverage matrix
     */
    public int[][] getCoverage() {
        return grid.getCoverageMatrix();
    }

    public Grid getGrid() { return grid; }
    public List<Ranger> getRangers() { return rangers; }
}
