# Weather Service Implementation

## Overview
Comprehensive weather functionality with caching, geolocation, rate limiting, and error handling.

## Features

### ✅ Multi-City Support
- Pre-configured cities: Malaga, Madrid, Barcelona, Valencia, Seville
- Support for any city via API endpoint

### ✅ Geolocation Support
- Browser-based geolocation
- Permission handling for location access
- Fallback error messages

### ✅ Intelligent Caching
- Cache duration: 30 minutes (configurable)
- Automatic cache expiration
- Stale cache fallback on API errors

### ✅ Rate Limiting
- In-memory rate limiter
- Default: 60 requests per hour per city
- 429 status code when exceeded

### ✅ Error Handling
- Graceful API failure handling
- Stale cache fallback
- User-friendly error messages
- Detailed logging

## Backend Architecture

### Components

#### WeatherService
- Core business logic
- External API integration
- Cache management
- Rate limit enforcement

#### WeatherController
- REST endpoints
- Exception handling
- CORS configuration

#### WeatherCacheRepository
- JPA repository
- Cache persistence

#### RateLimiter
- Request tracking
- Time-window enforcement

### API Endpoints

#### Get Weather by City
```
GET /api/weather/{city}
```
**Example:**
```bash
curl http://localhost:8080/api/weather/Malaga
```

**Response:**
```json
{
  "name": "Malaga",
  "main": {
    "temp": 20.5,
    "feels_like": 19.0,
    "humidity": 65
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

#### Get Weather by Coordinates
```
GET /api/weather/coordinates?lat={latitude}&lon={longitude}
```
**Example:**
```bash
curl "http://localhost:8080/api/weather/coordinates?lat=36.7213&lon=-4.4214"
```

### Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Weather API configuration
weather.api.url=https://api.openweathermap.org/data/2.5
weather.api.key=${WEATHER_API_KEY:demo}
weather.cache.minutes=30
weather.ratelimit.max=60
weather.ratelimit.window=60
```

### Error Responses

#### Rate Limit Exceeded (429)
```json
{
  "error": "Rate limit exceeded for malaga. Try again later."
}
```

#### Service Unavailable (503)
```json
{
  "error": "Unable to fetch weather for London"
}
```

## Frontend Architecture

### Components

#### WeatherWidget
- City selection buttons
- Geolocation toggle
- Error and loading states
- Permission status display

#### WeatherCard
- Weather icon display
- Temperature and feels-like
- Weather description
- Humidity information

### Custom Hooks

#### useWeather
- Fetch weather by city
- Error handling
- Loading states

#### useWeatherByCoordinates
- Fetch weather by coordinates
- Geolocation integration

#### useGeolocation
- Browser geolocation API
- Permission management
- Error handling

## Testing

### Unit Tests
```bash
cd backend
mvn test -Dtest=WeatherServiceTest
```

**Coverage:**
- ✅ API fetch with empty cache
- ✅ Cache hit scenario
- ✅ Stale cache fallback
- ✅ API failure without cache
- ✅ Rate limit enforcement
- ✅ Geolocation by coordinates

### Integration Tests
```bash
mvn test -Dtest=WeatherControllerTest
```

**Coverage:**
- ✅ City endpoint success
- ✅ Coordinates endpoint success
- ✅ 503 error handling
- ✅ 429 rate limit handling

## Running the Application

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm run dev
```

### Environment Variables

Create a `.env.local` file in the frontend directory:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

For production, set the weather API key:
```bash
export WEATHER_API_KEY=your_openweathermap_api_key
```

## Browser Permissions

The geolocation feature requires user permission:

1. Click "Use My Location" button
2. Browser prompts for location access
3. Allow/Deny the request
4. If denied, a warning message appears with instructions

## Performance Considerations

- **Caching**: Reduces API calls by 30 minutes per city
- **Rate Limiting**: Prevents API quota exhaustion
- **Stale Cache**: Ensures availability during API outages
- **Geolocation Cache**: 5-minute browser cache for coordinates

## Security

- **CORS**: Configured for `http://localhost:3000`
- **API Key**: Stored in environment variables
- **Rate Limiting**: Prevents abuse
- **Input Validation**: City names are validated

## Future Enhancements

- [ ] Redis-based distributed caching
- [ ] Persistent rate limit storage
- [ ] Weather forecast (multi-day)
- [ ] Weather alerts and notifications
- [ ] Historical weather data
- [ ] Favorite cities list
- [ ] Unit conversion (Celsius/Fahrenheit)
