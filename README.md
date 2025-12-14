# Loadouts Manager - Destiny 2 Android App

Android application for managing Destiny 2 loadouts using the Bungie.net API.

## 🎯 Features

- **Full CRUD Operations**: Create, read, update, and delete loadouts per character
- **Equipment Management**: Manage weapons, armor, and cosmetics (ornaments & shaders)
- **Smart Item Search**: Automatic search across character inventories and vault
- **Loadout Equipping**: Equip complete loadouts with automatic item transfer
- **Vault Integration**: Always-online vault item viewing
- **Dynamic Theming**: Sci-fi dark theme with future subclass-based themes
- **Responsive UI**: Adaptive layouts with expandable item details

## 📁 Project Documentation

- **[IMPLEMENTATION_DETAILS.md](IMPLEMENTATION_DETAILS.md)** - Complete technical specifications
- **[BUNGIE_API_INTEGRATION.md](BUNGIE_API_INTEGRATION.md)** - API integration guide with examples
- **[USAGE_EXAMPLES.md](USAGE_EXAMPLES.md)** - Code examples for all components
- **[CHANGES_SUMMARY.md](CHANGES_SUMMARY.md)** - Recent implementation summary

## 🚀 Quick Start

### Prerequisites

1. Android Studio (Latest version)
2. Bungie.net Developer Account
3. Registered Bungie Application with API Key

### Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd Loadoutsmanager
```

2. **Configure API Key**

Create or edit `local.properties` in the project root:
```properties
bungie.api.key=YOUR_API_KEY_HERE
```

> **Note**: Currently using API Key only. OAuth2 Client ID/Secret will be added for write operations.

3. **Build the project**
```bash
./gradlew build
```

4. **Run on device/emulator**
```bash
./gradlew installDebug
```

## 🏗️ Architecture

```
app/
├── data/
│   ├── model/              # Data models (Loadout, Item, Character)
│   ├── api/                # Bungie API service and interceptors
│   └── repository/         # Data repositories and search service
├── presentation/
│   ├── ui/                 # Compose screens and components
│   └── viewmodel/          # ViewModels for state management
└── ui/
    └── theme/              # Dynamic theme system and colors
```

### Key Components

#### Data Models
- **DestinyLoadout**: Per-character loadouts with subclass info
- **LoadoutEquipment**: Structured slots for weapons and armor
- **DestinyItem**: Items with perks, stats, and cosmetics
- **ItemCosmetics**: Ornaments (skins) and shaders

#### Services
- **EquipmentSearchService**: Smart item search with priority order
  1. Target character inventory
  2. Other characters inventory
  3. Vault

#### UI Components
- **ItemCard**: Expandable item cards with stats and perks
- **LoadoutDetailScreen**: Dual-view loadout display (list/grid)
- **BungieItemIcon**: Async image loading from Bungie CDN
- **Dynamic Themes**: 6 theme variants (Default + 5 subclasses)

## 🎨 Theming System

### Default Theme (Sci-Fi Dark)
```kotlin
LoadoutsManagerTheme {
    // Sci-fi dark theme with cyan accents
    YourContent()
}
```

### Subclass Themes (Future)
```kotlin
LoadoutsManagerTheme(theme = LoadoutTheme.Solar) {
    // Orange/fire theme for Solar builds
}
```

Available themes: Default, Solar, Arc, Void, Stasis, Strand

## 📡 API Integration

### Bungie.net Platform
- **Base URL**: `https://www.bungie.net/Platform`
- **Authentication**: API Key (header: `X-API-Key`)
- **Documentation**: https://bungie-net.github.io/

### Key Endpoints
- Profile & Characters
- Inventories (character & vault)
- Item Transfer
- Item Equipping

See [BUNGIE_API_INTEGRATION.md](BUNGIE_API_INTEGRATION.md) for detailed API documentation.

## 🛠️ Tech Stack

### Core
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system

### Network
- **Retrofit** 2.9.0 - HTTP client
- **OkHttp** 4.12.0 - Interceptors & logging
- **Moshi** 1.15.0 - JSON parsing
- **Coil** 2.5.0 - Image loading

### Architecture
- **Coroutines** 1.7.3 - Async operations
- **StateFlow** - Reactive state
- **ViewModel** - UI state management
- **Repository Pattern** - Data layer abstraction

### Future
- **Room** - Local database (planned)
- **AppAuth** 0.11.1 - OAuth2 (configured, not yet used)

## 📱 Usage Examples

### Creating a Loadout
```kotlin
val loadout = DestinyLoadout(
    id = UUID.randomUUID().toString(),
    name = "PvP Build",
    characterId = "123456",
    subclassHash = 2550323932, // Arc Titan
    equipment = LoadoutEquipment(
        kineticWeapon = myHandCannon,
        energyWeapon = mySniper,
        // ... other slots
    )
)
```

### Searching for Items
```kotlin
val result = equipmentSearchService.findItem(
    itemHash = 1234567890,
    targetCharacterId = "char1",
    allCharacterInventories = inventories,
    vaultInventory = vault
)

// Result includes: item, location, transfer needed
```

See [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md) for complete examples.

## 🔄 Development Status

### ✅ Completed
- [x] Project structure and dependencies
- [x] Data models with cosmetics support
- [x] Dynamic theme system (6 variants)
- [x] Equipment search service
- [x] Expandable item card UI
- [x] Loadout detail screen (dual-view)
- [x] Bungie image loading helper
- [x] API key configuration

### 🚧 In Progress
- [ ] Bungie API integration
- [ ] Item transfer implementation
- [ ] Loadout equipping flow
- [ ] Local database (Room)

### 📋 Planned
- [ ] Complete OAuth2 flow (for write operations)
- [ ] Manifest integration (item names/icons)
- [ ] CRUD UI for loadouts
- [ ] Character selection
- [ ] Vault management UI
- [ ] Subclass-based theme switching
- [ ] Loading states and error handling
- [ ] Offline mode

## 🤝 Contributing

This is a personal project, but suggestions and feedback are welcome!

## 📄 License

This project is for educational purposes. Destiny 2 and related content are trademarks of Bungie, Inc.

## ⚠️ Disclaimer

This is an unofficial third-party application and is not affiliated with or endorsed by Bungie, Inc.

---

**Built with ❤️ for Guardians**
