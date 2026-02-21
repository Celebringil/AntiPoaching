package antipoaching.simulation;

import antipoaching.model.Cell;
import antipoaching.model.Grid;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulates poaching risk before and after patrol optimization
 */
public class PoachingSimulator {

    private static final double PATROL_REDUCTION_FACTOR = 0.2;  // 80% risk reduction when patrolled
    private static final double HIGH_RISK_THRESHOLD = 0.7;

    /**
     * Calculate comprehensive statistics
     * @param grid The patrol grid after optimization
     * @return Map containing before/after risk statistics
     */
    public Map<String, Object> calculateStats(Grid grid) {
        int size = grid.getSize();
        Cell[][] cells = grid.getCells();

        double totalBeforeRisk = 0;
        double totalAfterRisk = 0;
        int passableCells = 0;
        int highRiskCells = 0;
        int coveredHighRiskCells = 0;
        int totalVisits = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = cells[row][col];

                if (!cell.isPassable()) continue;

                passableCells++;
                double risk = cell.getRiskLevel();
                totalBeforeRisk += risk;

                // Calculate after risk based on patrol coverage
                if (cell.getVisitCount() > 0) {
                    totalAfterRisk += risk * PATROL_REDUCTION_FACTOR;
                    totalVisits++;
                } else {
                    totalAfterRisk += risk;
                }

                // Track high risk coverage
                if (risk >= HIGH_RISK_THRESHOLD) {
                    highRiskCells++;
                    if (cell.getVisitCount() > 0) {
                        coveredHighRiskCells++;
                    }
                }
            }
        }

        // Calculate percentages
        double avgBeforeRisk = passableCells > 0 ? totalBeforeRisk / passableCells : 0;
        double avgAfterRisk = passableCells > 0 ? totalAfterRisk / passableCells : 0;
        double riskReduction = avgBeforeRisk > 0
            ? ((avgBeforeRisk - avgAfterRisk) / avgBeforeRisk) * 100
            : 0;
        double highRiskCoverage = highRiskCells > 0
            ? ((double) coveredHighRiskCells / highRiskCells) * 100
            : 100;
        double overallCoverage = passableCells > 0
            ? ((double) totalVisits / passableCells) * 100
            : 0;

        // Build result map
        Map<String, Object> stats = new HashMap<>();
        stats.put("beforeRisk", Math.round(avgBeforeRisk * 1000.0) / 1000.0);
        stats.put("afterRisk", Math.round(avgAfterRisk * 1000.0) / 1000.0);
        stats.put("riskReduction", String.format("%.0f%%", riskReduction));
        stats.put("highRiskCoverage", String.format("%.0f%%", highRiskCoverage));
        stats.put("overallCoverage", String.format("%.0f%%", overallCoverage));
        stats.put("cellsPatrolled", totalVisits);
        stats.put("totalCells", passableCells);
        stats.put("highRiskCells", highRiskCells);
        stats.put("coveredHighRiskCells", coveredHighRiskCells);

        return stats;
    }

    /**
     * Simulate poaching events based on current risk levels and patrol coverage
     * @param grid The patrol grid
     * @param simulationRuns Number of simulation iterations
     * @return Simulation results
     */
    public Map<String, Object> simulatePoaching(Grid grid, int simulationRuns) {
        int size = grid.getSize();
        Cell[][] cells = grid.getCells();

        int beforePoachingEvents = 0;
        int afterPoachingEvents = 0;
        int animalsAtRisk = 0;

        java.util.Random random = new java.util.Random();

        for (int run = 0; run < simulationRuns; run++) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    Cell cell = cells[row][col];

                    if (!cell.isPassable() || !cell.hasAnimal()) continue;

                    if (run == 0) animalsAtRisk++;

                    double risk = cell.getRiskLevel();

                    // Before patrol
                    if (random.nextDouble() < risk) {
                        beforePoachingEvents++;
                    }

                    // After patrol (reduced risk if patrolled)
                    double effectiveRisk = cell.getVisitCount() > 0
                        ? risk * PATROL_REDUCTION_FACTOR
                        : risk;
                    if (random.nextDouble() < effectiveRisk) {
                        afterPoachingEvents++;
                    }
                }
            }
        }

        Map<String, Object> results = new HashMap<>();
        results.put("simulationRuns", simulationRuns);
        results.put("animalsAtRisk", animalsAtRisk);
        results.put("expectedPoachingBefore", (double) beforePoachingEvents / simulationRuns);
        results.put("expectedPoachingAfter", (double) afterPoachingEvents / simulationRuns);
        results.put("animalsSaved",
            ((double) beforePoachingEvents - afterPoachingEvents) / simulationRuns);

        return results;
    }
}
