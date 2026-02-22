/**
 * API module for communicating with AWS Lambda via API Gateway
 */

const API = {
    // TODO: Replace with your API Gateway URL after deployment
    BASE_URL: 'https://YOUR_API_GATEWAY_ID.execute-api.YOUR_REGION.amazonaws.com/prod',

    /**
     * Run patrol optimization algorithm
     * @param {Object} params - Optimization parameters
     * @returns {Promise<Object>} - Optimization results
     */
    async optimize(params) {
        const response = await fetch(`${this.BASE_URL}/api/optimize`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(params)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Optimization failed');
        }

        return response.json();
    },

    /**
     * Save map configuration to DynamoDB
     * @param {Object} mapData - Map configuration
     * @returns {Promise<Object>} - Saved map with ID
     */
    async saveMap(mapData) {
        const response = await fetch(`${this.BASE_URL}/api/maps`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(mapData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save map');
        }

        return response.json();
    },

    /**
     * Get all saved maps
     * @returns {Promise<Array>} - List of maps
     */
    async getMaps() {
        const response = await fetch(`${this.BASE_URL}/api/maps`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to fetch maps');
        }

        return response.json();
    },

    /**
     * Get a specific map by ID
     * @param {string} mapId - Map ID
     * @returns {Promise<Object>} - Map data
     */
    async getMap(mapId) {
        const response = await fetch(`${this.BASE_URL}/api/maps/${mapId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to fetch map');
        }

        return response.json();
    },

    /**
     * Save optimization result
     * @param {Object} result - Optimization result
     * @returns {Promise<Object>} - Saved result with ID
     */
    async saveResult(result) {
        const response = await fetch(`${this.BASE_URL}/api/results`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(result)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save result');
        }

        return response.json();
    }
};
