# Loadouts Manager - AI Coding Instructions

## Project Overview
Android application for managing Destiny 2 loadouts using the Bungie.net API.
- **Tech Stack**: Kotlin, Jetpack Compose, Retrofit, OkHttp, Moshi, Room, AppAuth.
- **Architecture**: Clean Architecture (Data, Domain, Presentation) with MVVM.
- **Dependency Injection**: Manual DI / Service Locator pattern via `LoadoutsApplication`.

## Critical Setup & Configuration
- **Secrets**: API credentials MUST be in `local.properties` (not committed).
  ```properties
  bungie.api.key=YOUR_KEY
  bungie.client.id=YOUR_ID
  bungie.client.secret=YOUR_SECRET
  ```
- **BuildConfig**: These properties are exposed via `BuildConfig` in `app/build.gradle.kts` and accessed in `BungieConfig.kt`.
- **Redirect URI**: `com.ads.loadoutsmanager://oauth2redirect` (Must match Bungie Developer Portal).

## Architecture & Patterns

### Dependency Injection
- **No Hilt/Dagger**: The project uses manual dependency injection.
- **Service Locator**: `LoadoutsApplication.kt` holds singleton instances (`tokenStorage`, `authRepository`, `networkModule`).
- **ViewModel Factories**: ViewModels are created using custom factories in `MainActivity` or Composable destinations, injecting dependencies from the Application class.
  ```kotlin
  // Example from MainActivity.kt
  val authViewModel: AuthViewModel by viewModels {
      val app = application as LoadoutsApplication
      AuthViewModel.Factory(app.tokenStorage, app.authRepository, this)
  }
  ```

### Authentication (OAuth2)
- **Flow**: `MainActivity` intercepts the OAuth redirect via `onNewIntent` and `checkIntent`.
- **Handling**: `AuthViewModel.handleAuthCallback` processes the response.
- **Storage**: Tokens are stored securely in `SecureTokenStorage` (EncryptedSharedPreferences).
- **Refresh**: `TokenRefreshAuthenticator` (OkHttp Interceptor) automatically refreshes expired tokens.

### Data Layer
- **API**: `BungieApiService` defines endpoints. `ApiResult` wrapper is used for error handling.
- **Database**: Room database (`LoadoutsDatabase`) caches loadouts.
- **Repositories**: Broker data between API and DB.
  - `LoadoutRepository`: Manages loadout CRUD operations.
  - `AuthRepository`: Manages user session and tokens.

### UI Layer (Jetpack Compose)
- **Entry Point**: `MainActivity` sets up the theme and handles high-level navigation (Auth vs Main content).
- **State Management**: ViewModels expose `StateFlow` (e.g., `_uiState.asStateFlow()`) collected as `state` in Composables.
- **Theme**: `LoadoutsManagerTheme` in `ui/theme`.

## Common Workflows

### Adding a New Feature
1.  **Data**: Define API endpoint in `BungieApiService` and/or DB entity in `database/entity`.
2.  **Repository**: Add method to Repository interface and implementation.
3.  **ViewModel**: Create/Update ViewModel to expose data via `StateFlow`.
4.  **UI**: Create Composable screen/component.
5.  **DI**: Update `LoadoutsApplication` or ViewModel Factory if new dependencies are needed.

### Debugging
- **after implementing a feature**: For each new feature, build and run the app to ensure no runtime errors.
- **Logs**: Use `Log.d("Tag", "Message")`.
- **Network**: OkHttp logging interceptor is enabled in debug builds. Check Logcat for raw JSON responses.
- **Auth Issues**: Check `AuthViewModel` logs and ensure `local.properties` is correct.
- **Building app**: for debug or release use the Gradle tasks using the java jdk in `C:\Program Files\Android\Android Studio\jbr`:
  - `./gradlew assembleDebug`
  - `./gradlew assembleRelease`

## Conventions
- **Coroutines**: Use `viewModelScope` for UI-related coroutines. Use `Dispatchers.IO` for DB/Network.
- **Error Handling**: Catch exceptions in Repository/ViewModel and emit error states to UI.
- **Naming**:
    - ViewModels: `FeatureViewModel`
    - Repositories: `FeatureRepository`
    - Composables: `FeatureScreen` or `FeatureComponent`
