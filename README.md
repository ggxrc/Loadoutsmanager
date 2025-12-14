# Loadouts Manager - Destiny 2 Android App

Android application for managing Destiny 2 loadouts using the Bungie.net API.

## Features

- **Full CRUD Operations**: Create, read, update, and delete loadouts
- **Equipment Management**: Manage equipped items and vault storage
- **Loadout Equipping**: Equip complete loadouts through the app
- **Vault Integration**: Store and retrieve loadouts from your Destiny 2 vault
- **OAuth2 Authentication**: Secure authentication with Bungie.net

## Setup

### Prerequisites

1. Android Studio
2. Bungie.net Developer Account
3. Registered Bungie Application

### Bungie API Configuration

1. Go to [https://www.bungie.net/en/Application](https://www.bungie.net/en/Application)
2. Create a new application or use an existing one
3. Note your **API Key** and **OAuth Client ID**
4. Configure the OAuth redirect URI: `com.ads.loadoutsmanager://oauth2redirect`
5. Request the following OAuth scopes:
   - `ReadUserData`
   - `ReadDestinyInventoryAndVault`
   - `MoveEquipDestinyItems`

### App Configuration

1. Open `app/src/main/java/com/ads/loadoutsmanager/data/api/BungieConfig.kt`
2. Replace the placeholder values:
   ```kotlin
   const val API_KEY = "your_actual_api_key"
   const val CLIENT_ID = "your_actual_client_id"
   ```

## Architecture

The app follows Clean Architecture principles with the following layers:

### Data Layer
- **Models**: Data classes for Destiny entities (`DestinyLoadout`, `DestinyItem`, `DestinyCharacter`)
- **API**: Retrofit service interfaces and network configuration
- **Repository**: Data management and business logic

### Domain Layer
- Use cases for loadout operations (planned for future implementation)

### Presentation Layer
- **ViewModels**: State management and UI logic
- **Composables**: Jetpack Compose UI components (to be implemented)

## API Integration

### Authentication Flow

The app uses OAuth2 for authentication:

1. User initiates login
2. App redirects to Bungie.net authorization page
3. User grants permissions
4. App receives authorization code
5. App exchanges code for access token
6. Access token is used for API requests

### Key API Endpoints

- `GET /Destiny2/{membershipType}/Profile/{membershipId}/` - Get user profile
- `GET /Destiny2/{membershipType}/Profile/{membershipId}/Character/{characterId}/` - Get character data
- `POST /Destiny2/Actions/Items/EquipItem/` - Equip single item
- `POST /Destiny2/Actions/Items/EquipItems/` - Equip multiple items
- `POST /Destiny2/Actions/Items/TransferItem/` - Transfer item to/from vault

## Dependencies

### Network
- **Retrofit**: HTTP client for API calls
- **OkHttp**: HTTP client with interceptors
- **Moshi**: JSON parsing

### Authentication
- **AppAuth**: OAuth2 and OpenID Connect client

### Architecture
- **Kotlin Coroutines**: Asynchronous programming
- **Jetpack Compose**: Modern UI toolkit
- **ViewModel**: UI state management
- **StateFlow**: Reactive data streams

## Usage

### Creating a Loadout

1. Ensure you're authenticated
2. Select your character
3. Choose the items you want in the loadout
4. Save the loadout with a name

### Equipping a Loadout

1. Select a saved loadout
2. Tap "Equip"
3. The app will:
   - Transfer items from vault if needed
   - Equip all items on your character

### Storing to Vault

1. Select an equipped loadout
2. Tap "Store in Vault"
3. All items will be transferred to your vault

## Development Status

✅ **Completed:**
- Project structure setup
- Dependencies configuration
- Data models
- API service interfaces
- Network layer with OAuth2
- Repository pattern implementation
- ViewModel for state management
- Basic OAuth2 flow

🚧 **In Progress:**
- UI implementation with Jetpack Compose
- Local database for offline storage
- Complete OAuth2 integration testing

📋 **Planned:**
- Advanced filtering and search
- Loadout sharing
- Character switching
- Item details view
- Vault management UI

## License

This project is for educational purposes. Destiny 2 and related content are trademarks of Bungie, Inc.

## Disclaimer

This is an unofficial third-party application and is not affiliated with or endorsed by Bungie, Inc.
