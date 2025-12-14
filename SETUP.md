# Setup and Build Instructions

## Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or higher
- Internet connection for downloading dependencies

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/ggxrc/Loadoutsmanager.git
cd Loadoutsmanager
```

### 2. Configure Bungie API Key

Before building the app, you need to register it with Bungie:

1. Go to [Bungie Application Portal](https://www.bungie.net/en/Application)
2. Create a new application or select an existing one
3. Note your **API Key**
4. (Optional for future) Note your **OAuth Client ID** and **Client Secret** for write operations

#### Add API Key to Project

Create or edit `local.properties` in the project root:

```properties
# Bungie API Configuration
bungie.api.key=YOUR_API_KEY_HERE
```

> **Important**: The `local.properties` file is gitignored. Never commit your API keys!

The API key is automatically loaded via BuildConfig:
```kotlin
BuildConfig.BUNGIE_API_KEY
```

### 3. OAuth2 Configuration (Future - For Write Operations)

When implementing item transfer and equipping:

1. Set the OAuth redirect URI to: `com.ads.loadoutsmanager://oauth2redirect`
2. Request the following OAuth scopes:
   - `ReadUserData`
   - `ReadDestinyInventoryAndVault`
   - `MoveEquipDestinyItems`

3. Add to `local.properties`:
```properties
bungie.oauth.client.id=YOUR_CLIENT_ID
bungie.oauth.client.secret=YOUR_CLIENT_SECRET
```

### 4. Build the Project

#### Using Android Studio

1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the cloned repository and select it
4. Wait for Gradle to sync
5. Click "Build" → "Make Project" or press Ctrl+F9 (Cmd+F9 on Mac)

#### Using Command Line

##### Unix/Linux/Mac
```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run on connected device or emulator
./gradlew installDebug
```

##### Windows
```cmd
# Build debug APK
gradlew.bat assembleDebug

# Install on device
gradlew.bat installDebug
```

## Project Structure

```
app/src/main/java/com/ads/loadoutsmanager/
├── data/
│   ├── api/
│   │   ├── BungieApiService.kt          # Retrofit API interface
│   │   ├── BungieConfig.kt              # API configuration
│   │   ├── BungieApiKeyInterceptor.kt   # API key injection
│   │   ├── NetworkModule.kt             # Network setup
│   │   └── OAuth2Manager.kt             # OAuth2 (future)
│   ├── model/
│   │   ├── DestinyCharacter.kt          # Character model
│   │   ├── DestinyItem.kt               # Item with cosmetics
│   │   └── DestinyLoadout.kt            # Loadout with equipment
│   └── repository/
│       ├── LoadoutRepository.kt         # Loadout data management
│       ├── BungieRepository.kt          # Bungie API calls
│       └── EquipmentSearchService.kt    # Smart item search
├── presentation/
│   ├── ui/
│   │   ├── AuthenticationScreen.kt      # Login screen
│   │   ├── LoadoutListScreen.kt         # Loadout list UI
│   │   ├── LoadoutDetailScreen.kt       # Loadout detail (dual-view)
│   │   ├── ItemCard.kt                  # Expandable item card
│   │   └── BungieImageLoader.kt         # Bungie CDN image loader
│   └── viewmodel/
│       └── LoadoutViewModel.kt          # State management
├── ui/
│   └── theme/
│       ├── Color.kt                     # Color definitions
│       ├── DynamicTheme.kt              # Theme system
│       ├── Theme.kt                     # Theme provider
│       └── Type.kt                      # Typography
└── MainActivity.kt                      # Entry point
```

## Dependencies

The project uses the following key dependencies (see `gradle/libs.versions.toml`):

### Network
- **Retrofit** 2.9.0 - HTTP client for API calls
- **OkHttp** 4.12.0 - HTTP client with interceptors
- **Moshi** 1.15.0 - JSON parsing
- **Coil** 2.5.0 - Image loading from Bungie CDN

### UI
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Material Design components

### Architecture
- **Kotlin Coroutines** 1.7.3 - Asynchronous programming
- **ViewModel** - UI state management
- **StateFlow** - Reactive data streams

### Authentication (Configured, Not Yet Used)
- **AppAuth** 0.11.1 - OAuth2 client

## Troubleshooting

### Gradle Sync Failed

- Make sure you have a stable internet connection
- Try "File" → "Invalidate Caches / Restart" in Android Studio
- Delete `.gradle` folder in project root and sync again

### Build Errors

- Ensure you have JDK 11 or higher installed
- Check that JAVA_HOME environment variable is set correctly
- Update Android Studio to the latest version
- Verify `local.properties` has the API key

### API Key Not Working

```kotlin
// Verify the key is loaded correctly
Log.d("API", "Key: ${BuildConfig.BUNGIE_API_KEY}")
```

- Check `local.properties` format is correct
- Ensure the file is in project root (same level as `build.gradle.kts`)
- Clean and rebuild: `./gradlew clean build`

### Network Errors

- Check internet connection
- Verify Bungie API is accessible (check [Bungie.net status](https://twitter.com/BungieHelp))
- Check logcat for detailed error messages
- Verify API key is valid on Bungie.net developer portal

### Compose Preview Not Working

- Make sure you have the latest Compose compiler
- Try "Build" → "Clean Project" then "Build" → "Rebuild Project"
- Invalidate caches and restart Android Studio

## Running the App

### On Physical Device

1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Click "Run" → "Run 'app'" or press Shift+F10 (Ctrl+R on Mac)

### On Emulator

1. Open AVD Manager (Tools → AVD Manager)
2. Create a new virtual device or use existing
3. Start the emulator
4. Click "Run" → "Run 'app'"

## Testing

The app includes unit tests and instrumented tests:

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test
./gradlew test --tests "EquipmentSearchServiceTest"
```

## Documentation

After successful build, explore these documents:

- **[README.md](README.md)** - Project overview and features
- **[IMPLEMENTATION_DETAILS.md](IMPLEMENTATION_DETAILS.md)** - Technical specifications
- **[BUNGIE_API_INTEGRATION.md](BUNGIE_API_INTEGRATION.md)** - API integration guide
- **[USAGE_EXAMPLES.md](USAGE_EXAMPLES.md)** - Code examples
- **[CHANGES_SUMMARY.md](CHANGES_SUMMARY.md)** - Recent changes

## Next Steps

After successful build:

1. ~~Launch the app~~
2. ~~Click "Login with Bungie.net"~~
3. ~~Authorize the app on Bungie.net~~
4. **Next**: Implement Bungie API integration (see BUNGIE_API_INTEGRATION.md)
5. **Next**: Implement loadout CRUD UI
6. **Next**: Implement item transfer and equipping

## Current Status

✅ **Ready**:
- Project builds successfully
- Theme system working
- UI components created
- Data models defined
- Search service implemented

🚧 **In Progress**:
- Bungie API integration
- Database implementation
- Complete UI flows

## Support

For issues or questions:
- Check the documentation in this repository
- Review Bungie API documentation at [bungie-net.github.io](https://bungie-net.github.io/)
- Check Bungie API status on Twitter: [@BungieHelp](https://twitter.com/BungieHelp)
