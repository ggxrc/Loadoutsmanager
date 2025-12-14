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

### 2. Configure Bungie API Credentials

Before building the app, you need to register it with Bungie:

1. Go to [Bungie Application Portal](https://www.bungie.net/en/Application)
2. Create a new application or select an existing one
3. Note your **API Key** and **OAuth Client ID**
4. Set the OAuth redirect URI to: `com.ads.loadoutsmanager://oauth2redirect`
5. Request the following OAuth scopes:
   - ReadUserData
   - ReadDestinyInventoryAndVault
   - MoveEquipDestinyItems

6. Open `app/src/main/java/com/ads/loadoutsmanager/data/api/BungieConfig.kt`
7. Replace the placeholder values:
   ```kotlin
   const val API_KEY = "your_actual_api_key_here"
   const val CLIENT_ID = "your_actual_client_id_here"
   ```

### 3. Build the Project

#### Using Android Studio

1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the cloned repository and select it
4. Wait for Gradle to sync
5. Click "Build" → "Make Project" or press Ctrl+F9 (Cmd+F9 on Mac)

#### Using Command Line

```bash
# Make gradlew executable (Unix/Linux/Mac)
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

#### Windows

```cmd
gradlew.bat assembleDebug
```

## Project Structure

```
app/src/main/java/com/ads/loadoutsmanager/
├── data/
│   ├── api/
│   │   ├── BungieApiService.kt      # Retrofit API interface
│   │   ├── BungieConfig.kt          # API configuration
│   │   ├── NetworkModule.kt         # Network setup
│   │   └── OAuth2Manager.kt         # OAuth2 authentication
│   ├── model/
│   │   ├── DestinyCharacter.kt      # Character model
│   │   ├── DestinyItem.kt           # Item model
│   │   └── DestinyLoadout.kt        # Loadout model
│   └── repository/
│       └── LoadoutRepository.kt     # Data management
├── presentation/
│   ├── ui/
│   │   ├── AuthenticationScreen.kt  # Login screen
│   │   └── LoadoutListScreen.kt     # Loadout list UI
│   └── viewmodel/
│       └── LoadoutViewModel.kt      # State management
├── ui/
│   └── theme/                       # App theme
└── MainActivity.kt                  # Entry point
```

## Dependencies

The project uses the following key dependencies:

- **Retrofit 2.9.0**: HTTP client for API calls
- **OkHttp 4.12.0**: HTTP client with interceptors
- **Moshi 1.15.0**: JSON parsing
- **AppAuth 0.11.1**: OAuth2 authentication
- **Jetpack Compose**: Modern UI toolkit
- **Kotlin Coroutines**: Asynchronous programming

All dependencies are configured in `gradle/libs.versions.toml`.

## Troubleshooting

### Gradle Sync Failed

- Make sure you have a stable internet connection
- Try "File" → "Invalidate Caches / Restart" in Android Studio
- Delete `.gradle` folder in project root and sync again

### Build Errors

- Ensure you have JDK 11 or higher installed
- Check that JAVA_HOME environment variable is set correctly
- Update Android Studio to the latest version

### OAuth2 Not Working

- Verify your API credentials in `BungieConfig.kt`
- Check that redirect URI matches in both code and Bungie app configuration
- Ensure all required OAuth scopes are enabled in your Bungie app

### Network Errors

- Check internet connection
- Verify Bungie API is accessible (check [Bungie.net status](https://twitter.com/BungieHelp))
- Check logcat for detailed error messages

## Running the App

1. Connect an Android device or start an emulator
2. Click "Run" → "Run 'app'" or press Shift+F10 (Ctrl+R on Mac)
3. The app will install and launch on your device

## Testing

The app includes unit tests and instrumented tests:

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Next Steps

After successful build:

1. Launch the app
2. Click "Login with Bungie.net"
3. Authorize the app on Bungie.net
4. Start creating and managing loadouts!

## Support

For issues or questions:
- Check the [README.md](README.md) for feature documentation
- Review Bungie API documentation at [bungie-net.github.io](https://bungie-net.github.io/)
- Check the issue tracker on GitHub
