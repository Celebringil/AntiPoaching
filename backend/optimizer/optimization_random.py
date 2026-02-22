import json
import random
import math


def get_neighbors(row, col, params):
    """Get valid 4-directional neighboring cells (greedy patrol)."""
    neighbors = []
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1)]

    for dr, dc in directions:
        nr = row + dr
        nc = col + dc

        if (0 <= nr < params['gridSize'] and
                0 <= nc < params['gridSize'] and
                params['terrainMap'][nr][nc] == 1):
            neighbors.append((nr, nc))

    return neighbors


def get_neighbors8(row, col, params):
    """Get valid 8-directional neighboring cells (random patrol)."""
    neighbors = []
    directions = [
        (-1, 0), (1, 0), (0, -1), (0, 1),
        (-1, -1), (-1, 1), (1, -1), (1, 1)
    ]

    for dr, dc in directions:
        nr = row + dr
        nc = col + dc

        if (0 <= nr < params['gridSize'] and
                0 <= nc < params['gridSize'] and
                params['terrainMap'][nr][nc] == 1):
            neighbors.append((nr, nc))

    return neighbors


def calculate_stats(params, coverage):
    """Calculate patrol statistics."""
    grid_size = params['gridSize']
    total_risk = 0
    covered_risk = 0
    high_risk_cells = 0
    covered_high_risk = 0

    for row in range(grid_size):
        for col in range(grid_size):
            if params['terrainMap'][row][col] == 1:
                risk = params['riskMap'][row][col]
                total_risk += risk

                if risk >= 0.7:
                    high_risk_cells += 1
                    if coverage[row][col] > 0:
                        covered_high_risk += 1

                if coverage[row][col] > 0:
                    covered_risk += risk * 0.2  # 80% reduction
                else:
                    covered_risk += risk

    before_risk = total_risk / (grid_size * grid_size)
    after_risk = covered_risk / (grid_size * grid_size)
    reduction = round((before_risk - after_risk) / before_risk * 100) if before_risk > 0 else 0
    high_coverage = round((covered_high_risk / high_risk_cells) * 100) if high_risk_cells > 0 else 100

    return {
        'beforeRisk': before_risk,
        'afterRisk': after_risk,
        'riskReduction': str(reduction) + '%',
        'highRiskCoverage': str(high_coverage) + '%'
    }


def run_optimized_patrol(params):
    """Greedy patrol algorithm — Python translation of runLocalOptimization()."""
    grid_size = params['gridSize']
    routes = []
    coverage = [[0] * grid_size for _ in range(grid_size)]

    for r in range(params['rangerCount']):
        path = []

        # Find starting position (passable cell)
        while True:
            start_row = random.randint(0, grid_size - 1)
            start_col = random.randint(0, grid_size - 1)
            if params['terrainMap'][start_row][start_col] == 1:
                break

        current_row = start_row
        current_col = start_col
        path.append([current_row, current_col])
        coverage[current_row][current_col] += 1

        # Greedy walk
        for step in range(1, params['maxSteps']):
            neighbors = get_neighbors(current_row, current_col, params)
            if not neighbors:
                break

            best_neighbor = neighbors[0]
            best_score = float('-inf')

            for nr, nc in neighbors:
                risk = params['riskMap'][nr][nc]
                animal = 1 if params['animalMap'][nr][nc] else 0
                visited = coverage[nr][nc]
                score = (risk * 2 + animal) / (visited + 1)

                if score > best_score:
                    best_score = score
                    best_neighbor = (nr, nc)

            current_row, current_col = best_neighbor
            path.append([current_row, current_col])
            coverage[current_row][current_col] += 1

        routes.append({'rangerId': r, 'path': path})

    stats = calculate_stats(params, coverage)

    return {
        'routes': routes,
        'coverage': coverage,
        'stats': stats
    }


def run_random_patrol(params):
    """Random patrol algorithm — Python translation of runRandomPatrolAlgorithm()."""
    grid_size = params['gridSize']
    routes = []
    coverage = [[0] * grid_size for _ in range(grid_size)]

    for r in range(params['rangerCount']):
        path = []

        # Find starting position (passable cell)
        while True:
            start_row = random.randint(0, grid_size - 1)
            start_col = random.randint(0, grid_size - 1)
            if params['terrainMap'][start_row][start_col] == 1:
                break

        current_row = start_row
        current_col = start_col
        path.append([current_row, current_col])
        coverage[current_row][current_col] += 1

        # Pure random walk — randomly pick from 8 neighbors
        for step in range(1, params['maxSteps']):
            neighbors = get_neighbors8(current_row, current_col, params)
            if not neighbors:
                break

            next_cell = random.choice(neighbors)
            current_row, current_col = next_cell
            path.append([current_row, current_col])
            coverage[current_row][current_col] += 1

        routes.append({'rangerId': r, 'path': path})

    stats = calculate_stats(params, coverage)

    return {
        'routes': routes,
        'coverage': coverage,
        'stats': stats
    }


def lambda_handler(event, context):
    """AWS Lambda entry point."""
    cors_headers = {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Content-Type',
        'Access-Control-Allow-Methods': 'POST,OPTIONS'
    }

    # Handle CORS preflight
    if event.get('httpMethod') == 'OPTIONS':
        return {
            'statusCode': 200,
            'headers': cors_headers,
            'body': ''
        }

    try:
        body = json.loads(event.get('body', '{}'))

        mode = body.get('mode')
        params = {
            'gridSize': body['gridSize'],
            'rangerCount': body['rangerCount'],
            'maxSteps': body['maxSteps'],
            'riskMap': body['riskMap'],
            'animalMap': body['animalMap'],
            'terrainMap': body['terrainMap']
        }

        if mode == 'optimized':
            result = run_optimized_patrol(params)
        elif mode == 'random':
            result = run_random_patrol(params)
        else:
            return {
                'statusCode': 400,
                'headers': cors_headers,
                'body': json.dumps({'error': 'Invalid mode. Use "optimized" or "random".'})
            }

        return {
            'statusCode': 200,
            'headers': cors_headers,
            'body': json.dumps(result)
        }

    except Exception as e:
        return {
            'statusCode': 500,
            'headers': cors_headers,
            'body': json.dumps({'error': str(e)})
        }
