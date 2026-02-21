# Anti-Poaching Patrol Optimizer API Specification

## Base URL
```
https://{api-gateway-id}.execute-api.{region}.amazonaws.com/prod
```

---

## Endpoints

### 1. Run Optimization

**POST** `/api/optimize`

Run the patrol route optimization algorithm.

#### Request Body
```json
{
  "gridSize": 10,
  "rangerCount": 3,
  "maxSteps": 20,
  "riskMap": [[0.1, 0.8, ...], ...],
  "animalMap": [[true, false, ...], ...],
  "terrainMap": [[1, 1, 0, ...], ...]
}
```

| Field | Type | Description |
|-------|------|-------------|
| gridSize | number | Size of the grid (e.g., 10 for 10x10) |
| rangerCount | number | Number of rangers to deploy |
| maxSteps | number | Maximum steps per ranger |
| riskMap | number[][] | 2D array of risk values (0.0-1.0) |
| animalMap | boolean[][] | 2D array indicating animal presence |
| terrainMap | number[][] | 2D array (1=passable, 0=impassable) |

#### Response (200 OK)
```json
{
  "routes": [
    {
      "rangerId": 0,
      "path": [[0, 0], [0, 1], [1, 1], ...]
    }
  ],
  "coverage": [[0, 1, 2, ...], ...],
  "stats": {
    "beforeRisk": 0.45,
    "afterRisk": 0.12,
    "riskReduction": "73%",
    "highRiskCoverage": "95%",
    "overallCoverage": "67%",
    "cellsPatrolled": 45,
    "totalCells": 90
  }
}
```

---

### 2. Save Map

**POST** `/api/maps`

Save a map configuration to DynamoDB.

#### Request Body
```json
{
  "name": "Serengeti Sector A",
  "gridSize": 10,
  "riskMap": [[...]],
  "animalMap": [[...]],
  "terrainMap": [[...]]
}
```

#### Response (201 Created)
```json
{
  "mapId": "abc123-...",
  "createdAt": "2025-02-21T10:00:00Z"
}
```

---

### 3. Get All Maps

**GET** `/api/maps`

Retrieve all saved maps.

#### Response (200 OK)
```json
[
  {
    "mapId": "abc123-...",
    "name": "Serengeti Sector A",
    "gridSize": 10,
    "createdAt": "2025-02-21T10:00:00Z"
  }
]
```

---

### 4. Get Map by ID

**GET** `/api/maps/{mapId}`

Retrieve a specific map.

#### Response (200 OK)
```json
{
  "mapId": "abc123-...",
  "name": "Serengeti Sector A",
  "gridSize": 10,
  "riskMap": [[...]],
  "animalMap": [[...]],
  "terrainMap": [[...]],
  "createdAt": "2025-02-21T10:00:00Z"
}
```

---

### 5. Save Result

**POST** `/api/results`

Save an optimization result.

#### Request Body
```json
{
  "mapId": "abc123-...",
  "rangerCount": 3,
  "routes": [...],
  "stats": {...}
}
```

#### Response (201 Created)
```json
{
  "resultId": "xyz789-...",
  "createdAt": "2025-02-21T10:05:00Z"
}
```

---

### 6. Get Result by ID

**GET** `/api/results/{resultId}`

Retrieve a specific optimization result.

#### Response (200 OK)
```json
{
  "resultId": "xyz789-...",
  "mapId": "abc123-...",
  "rangerCount": 3,
  "routes": [...],
  "stats": {...},
  "createdAt": "2025-02-21T10:05:00Z"
}
```

---

## Error Responses

All errors follow this format:

```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable message"
}
```

| Status Code | Error Code | Description |
|-------------|------------|-------------|
| 404 | NOT_FOUND | Resource not found |
| 500 | INTERNAL_ERROR | Server error |
| 500 | OPTIMIZATION_FAILED | Algorithm error |

---

## CORS

All endpoints support CORS with:
- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET, POST, OPTIONS`
- `Access-Control-Allow-Headers: Content-Type`
