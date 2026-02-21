package antipoaching.model;

/**
 * Represents a single cell in the patrol grid
 */
public class Cell {
    private int row;
    private int col;
    private double riskLevel;      // 0.0 - 1.0, probability of poaching
    private boolean hasAnimal;      // Whether animals are present
    private boolean passable;       // Whether terrain is passable
    private int visitCount;         // Number of times visited by rangers

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.riskLevel = 0.0;
        this.hasAnimal = false;
        this.passable = true;
        this.visitCount = 0;
    }

    public Cell(int row, int col, double riskLevel, boolean hasAnimal, boolean passable) {
        this.row = row;
        this.col = col;
        this.riskLevel = riskLevel;
        this.hasAnimal = hasAnimal;
        this.passable = passable;
        this.visitCount = 0;
    }

    /**
     * Calculate the priority score for visiting this cell
     * Higher score = more important to patrol
     */
    public double calculateScore() {
        if (!passable) return -1;

        double animalBonus = hasAnimal ? 1.0 : 0.0;
        double visitPenalty = 1.0 / (visitCount + 1);

        return (riskLevel * 2 + animalBonus) * visitPenalty;
    }

    public void visit() {
        this.visitCount++;
    }

    // Get and Set
    public int getRow() { 
        return row; 
    }
    public int getCol() { 
        return col; 
    }

    public double getRiskLevel() { 
        return riskLevel; 
    }
    public void setRiskLevel(double riskLevel) { 
        this.riskLevel = riskLevel; 
    }

    public boolean hasAnimal() { 
        return hasAnimal; 
    }
    public void setHasAnimal(boolean hasAnimal) { 
        this.hasAnimal = hasAnimal; 
    }

    public boolean isPassable() { 
        return passable; 
    }
    public void setPassable(boolean passable) { 
        this.passable = passable; 
    }

    public int getVisitCount() { 
        return visitCount; 
    }

    @Override
    public String toString() {
        return String.format("Cell[%d,%d](risk=%.2f, animal=%b, passable=%b, visits=%d)",
            row, col, riskLevel, hasAnimal, passable, visitCount);
    }
}
