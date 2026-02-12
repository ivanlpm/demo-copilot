# Backend API Documentation

## Duck APIs

### 1. Get Random Duck
Fetches a random duck from an external API and saves it to the database.

**Endpoint:** `GET /api/duck`

**Response:**
```json
{
  "url": "https://random-d.uk/api/1.jpg",
  "message": "Quack!"
}
```

### 2. Get Duck History
Returns a paginated list of all fetched ducks, ordered by creation date (newest first).

**Endpoint:** `GET /api/duck/history`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Page size

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "url": "https://random-d.uk/api/1.jpg",
      "message": "Quack!",
      "createdAt": "2026-02-12T09:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 3. Get Duck by ID
Retrieves a specific duck by its ID.

**Endpoint:** `GET /api/duck/{id}`

**Path Parameters:**
- `id` - Duck ID

**Response:**
```json
{
  "id": 1,
  "url": "https://random-d.uk/api/1.jpg",
  "message": "Quack!",
  "createdAt": "2026-02-12T09:30:00"
}
```

**Status Codes:**
- 200 OK - Duck found
- 404 Not Found - Duck not found

### 4. Delete Duck
Deletes a duck from the database.

**Endpoint:** `DELETE /api/duck/{id}`

**Path Parameters:**
- `id` - Duck ID

**Status Codes:**
- 204 No Content - Duck deleted successfully
- 404 Not Found - Duck not found

### 5. Get Duck Statistics
Returns statistics about the duck collection.

**Endpoint:** `GET /api/duck/statistics`

**Response:**
```json
{
  "totalFetched": 100,
  "fetchedToday": 5,
  "oldestDuck": "2026-01-01T10:00:00",
  "newestDuck": "2026-02-12T09:30:00"
}
```

## Weather APIs

### 1. Get Weather by City
Fetches current weather for a specific city with caching and rate limiting.

**Endpoint:** `GET /api/weather/{city}`

**Path Parameters:**
- `city` - City name

**Response:**
```json
{
  "name": "London",
  "main": {
    "temp": 15.5,
    "feels_like": 14.0,
    "humidity": 65,
    "temp_min": 13.0,
    "temp_max": 17.0
  },
  "weather": [
    {
      "main": "Clear",
      "description": "clear sky",
      "icon": "01d"
    }
  ],
  "cod": 200
}
```

**Status Codes:**
- 200 OK - Weather data retrieved
- 429 Too Many Requests - Rate limit exceeded
- 503 Service Unavailable - Weather API error

### 2. Get Weather by Coordinates
Fetches current weather for specific geographic coordinates.

**Endpoint:** `GET /api/weather/coordinates`

**Query Parameters:**
- `lat` - Latitude
- `lon` - Longitude

**Response:** Same as Get Weather by City

### 3. Get Cache Statistics
Returns statistics about the weather cache.

**Endpoint:** `GET /api/weather/cache/statistics`

**Response:**
```json
{
  "totalCached": 10,
  "activeCaches": 8,
  "expiredCaches": 2
}
```

### 4. Get Cache List
Returns a list of all cached weather entries.

**Endpoint:** `GET /api/weather/cache/list`

**Response:**
```json
[
  {
    "city": "london",
    "cachedAt": "2026-02-12T09:00:00",
    "expiresAt": "2026-02-12T09:30:00",
    "expired": false
  },
  {
    "city": "paris",
    "cachedAt": "2026-02-12T08:55:00",
    "expiresAt": "2026-02-12T09:25:00",
    "expired": false
  }
]
```

### 5. Invalidate Cache
Invalidates (deletes) the cache for a specific city.

**Endpoint:** `DELETE /api/weather/cache/{city}`

**Path Parameters:**
- `city` - City name

**Status Codes:**
- 204 No Content - Cache invalidated successfully
- 404 Not Found - Cache entry not found

## CORS Configuration

All endpoints allow cross-origin requests from `http://localhost:3000` for frontend development.

## Error Handling

All endpoints return structured error responses:

```json
{
  "error": "Error message description"
}
```

Common HTTP status codes:
- 200 OK - Successful request
- 204 No Content - Successful deletion
- 404 Not Found - Resource not found
- 429 Too Many Requests - Rate limit exceeded
- 503 Service Unavailable - External service error
