package antipoaching;

import antipoaching.algorithm.PatrolOptimizer;
import antipoaching.model.Grid;
import antipoaching.simulation.PoachingSimulator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AWS Lambda handler for patrol optimization
 */
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new Gson();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        // Set CORS headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        response.setHeaders(headers);

        // Handle CORS preflight
        if ("OPTIONS".equals(input.getHttpMethod())) {
            response.setStatusCode(200);
            return response;
        }

        try {
            // Parse request body
            String body = input.getBody();
            JsonObject request = gson.fromJson(body, JsonObject.class);

            // Extract parameters
            int gridSize = request.get("gridSize").getAsInt();
            int rangerCount = request.get("rangerCount").getAsInt();
            int maxSteps = request.get("maxSteps").getAsInt();

            JsonArray riskMapJson = request.getAsJsonArray("riskMap");
            JsonArray animalMapJson = request.getAsJsonArray("animalMap");
            JsonArray terrainMapJson = request.getAsJsonArray("terrainMap");

            // Convert JSON arrays to Java arrays
            double[][] riskMap = new double[gridSize][gridSize];
            boolean[][] animalMap = new boolean[gridSize][gridSize];
            int[][] terrainMap = new int[gridSize][gridSize];

            for (int i = 0; i < gridSize; i++) {
                JsonArray riskRow = riskMapJson.get(i).getAsJsonArray();
                JsonArray animalRow = animalMapJson.get(i).getAsJsonArray();
                JsonArray terrainRow = terrainMapJson.get(i).getAsJsonArray();

                for (int j = 0; j < gridSize; j++) {
                    riskMap[i][j] = riskRow.get(j).getAsDouble();
                    animalMap[i][j] = animalRow.get(j).getAsBoolean();
                    terrainMap[i][j] = terrainRow.get(j).getAsInt();
                }
            }

            // Create grid and initialize
            Grid grid = new Grid(gridSize);
            grid.initializeFromMaps(riskMap, animalMap, terrainMap);

            // Run optimization
            PatrolOptimizer optimizer = new PatrolOptimizer(grid);
            optimizer.initializeRangers(rangerCount, maxSteps);
            optimizer.optimize();

            // Calculate statistics
            PoachingSimulator simulator = new PoachingSimulator();
            Map<String, Object> stats = simulator.calculateStats(grid);

            // Build response
            Map<String, Object> result = new HashMap<>();
            result.put("routes", optimizer.getRoutes());
            result.put("coverage", optimizer.getCoverage());
            result.put("stats", stats);

            response.setStatusCode(200);
            response.setBody(gson.toJson(result));

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "OPTIMIZATION_FAILED");
            error.put("message", e.getMessage());

            response.setStatusCode(500);
            response.setBody(gson.toJson(error));
        }

        return response;
    }
}
