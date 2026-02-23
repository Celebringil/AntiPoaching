let currentMap = {
    gridSize: 10,
    riskMap: [],
    animalMap: [],
    terrainMap: []
};

const API_ENDPOINT = 'https://hsd0dhxxq1.execute-api.us-east-2.amazonaws.com/Prod/optimize';

function generateRandomMap() {
    const gridSize = parseInt(document.getElementById('gridSize').value);
    currentMap.gridSize = gridSize;
    currentMap.riskMap = [];
    currentMap.animalMap = [];
    currentMap.terrainMap = [];

    for (let row = 0; row < gridSize; row++) {
        const riskRow = [];
        const animalRow = [];
        const terrainRow = [];

        for (let col = 0; col < gridSize; col++) {
            const isPassable = Math.random() > 0.1 ? 1 : 0;
            terrainRow.push(isPassable);

            if (isPassable) {
                const edgeFactor = Math.min(row, col, gridSize - 1 - row, gridSize - 1 - col) / (gridSize / 2);
                const baseRisk = Math.random();
                const risk = Math.max(0, Math.min(1, baseRisk * (1 - edgeFactor * 0.5)));
                riskRow.push(Math.round(risk * 100) / 100);
                const hasAnimal = Math.random() < 0.2 * (1 - edgeFactor * 0.3);
                animalRow.push(hasAnimal);
            } else {
                riskRow.push(0);
                animalRow.push(false);
            }
        }

        currentMap.riskMap.push(riskRow);
        currentMap.animalMap.push(animalRow);
        currentMap.terrainMap.push(terrainRow);
    }

    Visualizer.renderMap(currentMap, gridSize);
    document.getElementById('results').classList.add('hidden');
    document.getElementById('optimizedRow').classList.add('hidden');
    document.getElementById('randomRow').classList.add('hidden');
}

async function runOptimization() {
    const rangerCount = parseInt(document.getElementById('rangerCount').value);
    const maxSteps = parseInt(document.getElementById('maxSteps').value);
    if (currentMap.riskMap.length === 0) {
        alert('Please generate a map first!');
        return;
    }
    Visualizer.setLoading(true);
    try {
        const params = {
            gridSize: currentMap.gridSize,
            rangerCount: rangerCount,
            maxSteps: maxSteps,
            riskMap: currentMap.riskMap,
            animalMap: currentMap.animalMap,
            terrainMap: currentMap.terrainMap
        };
        const result = await fetch(API_ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ mode: 'optimized', ...params })
        }).then(r => r.json());
        await Visualizer.animateRoutes(result.routes, 50);
        Visualizer.updateStatsOptimized(result.stats);
    } catch (error) {
        alert('Optimization failed: ' + error.message);
        console.error(error);
    } finally {
        Visualizer.setLoading(false);
    }
}

async function runRandomPatrol() {
    const rangerCount = parseInt(document.getElementById('rangerCount').value);
    const maxSteps = parseInt(document.getElementById('maxSteps').value);
    if (currentMap.riskMap.length === 0) {
        alert('Please generate a map first!');
        return;
    }
    Visualizer.setLoading(true);
    try {
        const params = {
            gridSize: currentMap.gridSize,
            rangerCount: rangerCount,
            maxSteps: maxSteps,
            riskMap: currentMap.riskMap,
            animalMap: currentMap.animalMap,
            terrainMap: currentMap.terrainMap
        };
        const result = await fetch(API_ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ mode: 'random', ...params })
        }).then(r => r.json());
        await Visualizer.animateRoutes(result.routes, 50);
        Visualizer.updateStatsRandom(result.stats);
    } catch (error) {
        alert('Random patrol failed: ' + error.message);
        console.error(error);
    } finally {
        Visualizer.setLoading(false);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('generateBtn').addEventListener('click', generateRandomMap);
    document.getElementById('optimizeBtn').addEventListener('click', runOptimization);
    document.getElementById('randomPatrolBtn').addEventListener('click', runRandomPatrol);
    generateRandomMap();
});
