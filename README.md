# Anti-Poaching Patrol Optimizer

A web application that optimizes ranger patrol routes to protect wildlife from poaching. Built for Wildlife Conservation Hackathon.

## Overview

This project uses a **greedy optimization algorithm** to generate optimal patrol routes for rangers, maximizing coverage of high-risk areas while considering:
- Poaching risk levels
- Animal habitat locations
- Terrain accessibility

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   S3 + CDN      │────▶│  API Gateway    │────▶│     Lambda      │
│   (Frontend)    │     │                 │     │  (Java 11)      │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
                                                         ▼
                                                ┌─────────────────┐
                                                │    DynamoDB     │
                                                │  (Data Store)   │
                                                └─────────────────┘
```

## Project Structure

```
AntiPoaching/
├── frontend/                 # Static website (→ S3)
│   ├── index.html
│   ├── css/style.css
│   └── js/
│       ├── main.js          # Main application logic
│       ├── api.js           # API client
│       └── visualizer.js    # Grid visualization
│
├── lambda/                   # AWS Lambda functions
│   ├── patrol-optimizer/    # Route optimization (Java)
│   └── data-manager/        # DynamoDB operations (Java)
│
├── infrastructure/          # AWS SAM template
│   └── template.yaml
│
└── docs/
    └── api-spec.md          # API documentation
```

## Algorithm

The patrol optimizer uses a **Greedy Strategy**:

1. **Starting Position Selection**: Rangers are placed at strategic positions (corners + high-risk areas)

2. **Step-by-Step Movement**: Each ranger moves to the neighboring cell with the highest score:
   ```
   Score = (RiskLevel × 2 + AnimalBonus) × VisitPenalty
   ```
   - Higher risk = higher priority
   - Cells with animals get bonus
   - Already-visited cells get penalized

3. **Result Calculation**: Compare before/after poaching risk to show effectiveness

## Quick Start

### Prerequisites
- Java 11+
- Maven
- AWS CLI configured
- AWS SAM CLI (optional, for deployment)

### Build Lambda Functions

```bash
# Build patrol-optimizer
cd lambda/patrol-optimizer
mvn clean package

# Build data-manager
cd ../data-manager
mvn clean package
```

### Local Testing

1. Open `frontend/index.html` in a browser
2. The frontend includes a mock optimizer for testing without AWS
3. Click "Generate Random Map" then "Run Optimization"

### Deploy to AWS

#### Option 1: Manual Deployment

1. **Create DynamoDB Tables**
   - `AntiPoachingMaps` (Primary Key: `mapId`)
   - `PatrolResults` (Primary Key: `resultId`)

2. **Create Lambda Functions**
   - Upload `patrol-optimizer-1.0.0.jar`
   - Upload `data-manager-1.0.0.jar`
   - Set handler: `antipoaching.Handler::handleRequest`

3. **Create API Gateway**
   - Create REST API
   - Add routes as per `docs/api-spec.md`
   - Connect to Lambda functions

4. **Deploy Frontend**
   - Create S3 bucket with static website hosting
   - Upload `frontend/` contents
   - Update `API.BASE_URL` in `api.js`

#### Option 2: SAM Deployment

```bash
cd infrastructure
sam build
sam deploy --guided
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/optimize` | Run optimization algorithm |
| POST | `/api/maps` | Save map configuration |
| GET | `/api/maps` | List all maps |
| GET | `/api/maps/{id}` | Get specific map |
| POST | `/api/results` | Save optimization result |
| GET | `/api/results/{id}` | Get specific result |

See [API Specification](docs/api-spec.md) for details.

## Technologies

- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Backend**: Java 11, AWS Lambda
- **Database**: Amazon DynamoDB
- **API**: Amazon API Gateway
- **Hosting**: Amazon S3 + CloudFront

## Future Improvements

- [ ] Add more optimization algorithms (A*, Genetic Algorithm)
- [ ] Real-time collaboration
- [ ] Historical data analysis
- [ ] Mobile app
- [ ] Integration with GPS tracking

## License

MIT License - Feel free to use for educational purposes.

## Hackathon

Built for Wildlife Conservation Hackathon 2025.
