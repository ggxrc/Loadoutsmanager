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

1. Copy `local.properties.example` to `local.properties`
2. Replace the placeholder values with your actual Bungie API credentials:
   ```properties
   bungie.api.key=YOUR_ACTUAL_API_KEY
   bungie.client.id=YOUR_ACTUAL_CLIENT_ID
   bungie.client.secret=YOUR_ACTUAL_CLIENT_SECRET
   ```
3. Ensure the Android SDK path is correct for your system

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

### Database
- **Room 2.6.1**: Local persistence layer
- **KSP**: Kotlin Symbol Processing for Room

### Architecture
- **Kotlin Coroutines**: Asynchronous programming
- **Jetpack Compose**: Modern UI toolkit
- **ViewModel**: UI state management
- **StateFlow**: Reactive data streams
- **Material 3**: Design system

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
- Dependencies configuration (including Room database)
- Enhanced data models with cosmetics support
- API service interfaces
- Network layer with OAuth2
- Repository pattern with Room database integration
- ViewModel for state management
- Basic OAuth2 flow
- Dynamic theme system (subclass-based)
- Item card UI components
- Local database for offline storage (Room)
- Smart caching strategy (loadouts local, vault from API)
- Inventory checking flow optimization

🚧 **In Progress:**
- Manifest integration for item details
- Complete API response parsing
- OAuth2 backend proxy (for production security)

📋 **Planned:**
- Item icons and images from manifest
- Advanced filtering and search
- Loadout sharing
- Character switching UI
- Perks and stats display
- Vault management UI
- Theme animations

## New Features

### Dynamic Theming
The app features a dynamic theming system that adapts to the subclass of your loadout:
- **Solar**: Orange/fire color scheme
- **Arc**: Blue/electric color scheme
- **Void**: Purple/void color scheme
- **Stasis**: Ice/crystal color scheme
- **Strand**: Green/weave color scheme
- **Kinetic**: Gray/neutral color scheme
- **Base**: Dark sci-fi theme (default)

### Cosmetics Support
Loadouts now save and restore:
- Weapon ornaments (skins)
- Armor ornaments (skins)
- Shaders (tonalizadores) for each equipment piece

### Smart Caching
- **Loadouts**: Cached locally for offline access
- **Vault items**: Always fetched fresh from API
- **Equipped items**: Fetched in real-time

### Inventory Flow Optimization
When equipping loadouts, the app checks in order:
1. Target character inventory
2. Other characters' inventories  
3. Vault (last resort)

This minimizes API calls and transfer operations.

## Authentication Modes

The app supports two authentication modes:

### API Key Only (Read-Only)
- View inventory and vault
- Create and manage loadouts locally
- No item equipping or transferring via API

### Full OAuth2 (Read/Write)
- All read-only features
- Equip items via API
- Transfer items between characters and vault
- Full loadout management

See [AUTHENTICATION.md](AUTHENTICATION.md) for detailed setup instructions.

## License

This project is for educational purposes. Destiny 2 and related content are trademarks of Bungie, Inc.

## Disclaimer

This is an unofficial third-party application and is not affiliated with or endorsed by Bungie, Inc.
