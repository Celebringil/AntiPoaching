package antipoaching;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AWS Lambda handler for data management operations (DynamoDB)
 */
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new Gson();
    private final DynamoDBService dynamoService = new DynamoDBService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        // Set CORS headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        response.setHeaders(headers);

        // Handle CORS preflight
        if ("OPTIONS".equals(input.getHttpMethod())) {
            response.setStatusCode(200);
            return response;
        }

        try {
            String path = input.getPath();
            String method = input.getHttpMethod();

            // Route handling
            if (path.startsWith("/api/maps")) {
                return handleMapsRoute(path, method, input, response, context);
            } else if (path.startsWith("/api/results")) {
                return handleResultsRoute(path, method, input, response, context);
            } else {
                return notFound(response);
            }

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return error(response, e.getMessage());
        }
    }

    /**
     * Handle /api/maps routes
     */
    private APIGatewayProxyResponseEvent handleMapsRoute(
            String path, String method,
            APIGatewayProxyRequestEvent input,
            APIGatewayProxyResponseEvent response,
            Context context) {

        // POST /api/maps - Create new map
        if ("POST".equals(method) && "/api/maps".equals(path)) {
            JsonObject body = gson.fromJson(input.getBody(), JsonObject.class);
            Map<String, Object> result = dynamoService.saveMap(body);
            response.setStatusCode(201);
            response.setBody(gson.toJson(result));
            return response;
        }

        // GET /api/maps - List all maps
        if ("GET".equals(method) && "/api/maps".equals(path)) {
            List<Map<String, Object>> maps = dynamoService.getAllMaps();
            response.setStatusCode(200);
            response.setBody(gson.toJson(maps));
            return response;
        }

        // GET /api/maps/{id} - Get specific map
        if ("GET".equals(method) && path.startsWith("/api/maps/")) {
            String mapId = path.substring("/api/maps/".length());
            Map<String, Object> map = dynamoService.getMap(mapId);

            if (map == null) {
                return notFound(response);
            }

            response.setStatusCode(200);
            response.setBody(gson.toJson(map));
            return response;
        }

        return notFound(response);
    }

    /**
     * Handle /api/results routes
     */
    private APIGatewayProxyResponseEvent handleResultsRoute(
            String path, String method,
            APIGatewayProxyRequestEvent input,
            APIGatewayProxyResponseEvent response,
            Context context) {

        // POST /api/results - Save result
        if ("POST".equals(method) && "/api/results".equals(path)) {
            JsonObject body = gson.fromJson(input.getBody(), JsonObject.class);
            Map<String, Object> result = dynamoService.saveResult(body);
            response.setStatusCode(201);
            response.setBody(gson.toJson(result));
            return response;
        }

        // GET /api/results/{id} - Get specific result
        if ("GET".equals(method) && path.startsWith("/api/results/")) {
            String resultId = path.substring("/api/results/".length());
            Map<String, Object> result = dynamoService.getResult(resultId);

            if (result == null) {
                return notFound(response);
            }

            response.setStatusCode(200);
            response.setBody(gson.toJson(result));
            return response;
        }

        return notFound(response);
    }

    private APIGatewayProxyResponseEvent notFound(APIGatewayProxyResponseEvent response) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "NOT_FOUND");
        error.put("message", "Resource not found");
        response.setStatusCode(404);
        response.setBody(gson.toJson(error));
        return response;
    }

    private APIGatewayProxyResponseEvent error(APIGatewayProxyResponseEvent response, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "INTERNAL_ERROR");
        error.put("message", message);
        response.setStatusCode(500);
        response.setBody(gson.toJson(error));
        return response;
    }
}
