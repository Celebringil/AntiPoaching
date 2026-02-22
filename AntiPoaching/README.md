# Anti-Poaching Patrol Optimizer

A web application that helps optimize ranger patrol routes to protect wildlife from poaching.

Built for **2026 EmPHackathon**.

## What It Does

This tool helps wildlife conservation teams plan better patrol routes by:

- **Visualizing risk areas** - Shows which areas have high poaching risk on a grid map
- **Optimizing patrol routes** - Automatically generates the best routes for rangers
- **Measuring effectiveness** - Shows how much the patrol reduces poaching risk

## How To Use

1. Set the grid size and number of rangers
2. Click "Generate Random Map" to create a patrol area
3. Click "Run Optimization" to generate patrol routes
4. View the results showing risk reduction

## Map Legend

| Color | Meaning |
|-------|---------|
| 🟥 Red | High poaching risk |
| 🟧 Orange | Medium risk |
| 🟩 Green | Low risk |
| 🟫 Brown | Impassable terrain |
| 🔵 Blue lines | Ranger patrol routes |

## Tech Stack

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (AWS Lambda)
- **Database**: Amazon DynamoDB
- **Hosting**: Amazon S3 + API Gateway

## Team

Built by our team for the 2026 EmPHackathon wildlife conservation challenge.
