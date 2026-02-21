package antipoaching;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.*;

/**
 * Service class for DynamoDB operations
 */
public class DynamoDBService {

    private static final String MAPS_TABLE = "AntiPoachingMaps";
    private static final String RESULTS_TABLE = "PatrolResults";

    private final DynamoDbClient dynamoDb;
    private final Gson gson;

    public DynamoDBService() {
        this.dynamoDb = DynamoDbClient.builder()
            .region(Region.US_EAST_1)  // Change to your region
            .build();
        this.gson = new Gson();
    }

    /**
     * Save a map configuration
     */
    public Map<String, Object> saveMap(JsonObject mapData) {
        String mapId = UUID.randomUUID().toString();
        String timestamp = Instant.now().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("mapId", AttributeValue.builder().s(mapId).build());
        item.put("name", AttributeValue.builder()
            .s(mapData.has("name") ? mapData.get("name").getAsString() : "Untitled Map")
            .build());
        item.put("gridSize", AttributeValue.builder()
            .n(String.valueOf(mapData.get("gridSize").getAsInt()))
            .build());
        item.put("riskMap", AttributeValue.builder()
            .s(mapData.get("riskMap").toString())
            .build());
        item.put("animalMap", AttributeValue.builder()
            .s(mapData.get("animalMap").toString())
            .build());
        item.put("terrainMap", AttributeValue.builder()
            .s(mapData.get("terrainMap").toString())
            .build());
        item.put("createdAt", AttributeValue.builder().s(timestamp).build());

        PutItemRequest request = PutItemRequest.builder()
            .tableName(MAPS_TABLE)
            .item(item)
            .build();

        dynamoDb.putItem(request);

        Map<String, Object> result = new HashMap<>();
        result.put("mapId", mapId);
        result.put("createdAt", timestamp);
        return result;
    }

    /**
     * Get all maps
     */
    public List<Map<String, Object>> getAllMaps() {
        ScanRequest request = ScanRequest.builder()
            .tableName(MAPS_TABLE)
            .build();

        ScanResponse response = dynamoDb.scan(request);

        List<Map<String, Object>> maps = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            maps.add(itemToMap(item));
        }

        return maps;
    }

    /**
     * Get a map by ID
     */
    public Map<String, Object> getMap(String mapId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("mapId", AttributeValue.builder().s(mapId).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(MAPS_TABLE)
            .key(key)
            .build();

        GetItemResponse response = dynamoDb.getItem(request);

        if (!response.hasItem()) {
            return null;
        }

        return itemToMap(response.item());
    }

    /**
     * Save an optimization result
     */
    public Map<String, Object> saveResult(JsonObject resultData) {
        String resultId = UUID.randomUUID().toString();
        String timestamp = Instant.now().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("resultId", AttributeValue.builder().s(resultId).build());
        item.put("mapId", AttributeValue.builder()
            .s(resultData.has("mapId") ? resultData.get("mapId").getAsString() : "unknown")
            .build());
        item.put("rangerCount", AttributeValue.builder()
            .n(String.valueOf(resultData.get("rangerCount").getAsInt()))
            .build());
        item.put("routes", AttributeValue.builder()
            .s(resultData.get("routes").toString())
            .build());
        item.put("stats", AttributeValue.builder()
            .s(resultData.get("stats").toString())
            .build());
        item.put("createdAt", AttributeValue.builder().s(timestamp).build());

        PutItemRequest request = PutItemRequest.builder()
            .tableName(RESULTS_TABLE)
            .item(item)
            .build();

        dynamoDb.putItem(request);

        Map<String, Object> result = new HashMap<>();
        result.put("resultId", resultId);
        result.put("createdAt", timestamp);
        return result;
    }

    /**
     * Get a result by ID
     */
    public Map<String, Object> getResult(String resultId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("resultId", AttributeValue.builder().s(resultId).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(RESULTS_TABLE)
            .key(key)
            .build();

        GetItemResponse response = dynamoDb.getItem(request);

        if (!response.hasItem()) {
            return null;
        }

        return itemToMap(response.item());
    }

    /**
     * Convert DynamoDB item to Map
     */
    private Map<String, Object> itemToMap(Map<String, AttributeValue> item) {
        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            AttributeValue value = entry.getValue();
            if (value.s() != null) {
                // Try to parse as JSON if it looks like JSON
                String strValue = value.s();
                if (strValue.startsWith("[") || strValue.startsWith("{")) {
                    try {
                        map.put(entry.getKey(), gson.fromJson(strValue, Object.class));
                    } catch (Exception e) {
                        map.put(entry.getKey(), strValue);
                    }
                } else {
                    map.put(entry.getKey(), strValue);
                }
            } else if (value.n() != null) {
                map.put(entry.getKey(), Double.parseDouble(value.n()));
            }
        }

        return map;
    }
}
