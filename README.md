# Harmony Wildlife Sanctuary

A wildlife conservation website with an AI-powered anti-poaching patrol optimizer tool, built for the 2026 EmPHackathon.

## Pages

| Page | File | Description |
|------|------|-------------|
| Home | `frontend/index.html` | Landing page with video banner and link to the Patrol Optimizer |
| Our Wildlife | `frontend/animals.html` | Gallery of animals at the sanctuary |
| About Us | `frontend/about.html` | Information about the sanctuary team |
| Patrol Optimizer | `frontend/patrol-optimizer.html` | Interactive tool for generating and comparing ranger patrol routes |

## Running the Frontend Locally

No build step required. Open `frontend/index.html` directly in a browser:

```
open frontend/index.html
```

Or serve from any static file server, for example:

```bash
python3 -m http.server 8000 --directory frontend
```

Then visit `http://localhost:8000`.

## Deploying the Backend (AWS Lambda)

The patrol optimization algorithms run as an AWS Lambda function using AWS SAM.

**Prerequisites:** AWS CLI and AWS SAM CLI installed and configured.

```bash
cd backend/optimizer
sam build
sam deploy --guided
```

Follow the prompts during `sam deploy --guided` to set your stack name, region, and other options.

## Connecting Frontend to Backend

After deployment, SAM will output the API Gateway URL. Paste it into `frontend/js/main.js`:

```js
const API_ENDPOINT = 'https://YOUR_API_GATEWAY_URL/optimize';
```

Replace `https://YOUR_API_GATEWAY_URL/optimize` with the actual URL from the deploy output.
