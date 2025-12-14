# Bungie API Integration Guide

This document provides detailed information about how the Loadouts Manager integrates with the Bungie.net API for Destiny 2.

## API Overview

- **Base URL**: `https://www.bungie.net/Platform/`
- **API Documentation**: [https://bungie-net.github.io/](https://bungie-net.github.io/)
- **Authentication**: OAuth2
- **Rate Limiting**: Be mindful of rate limits (typically 250 requests per second per API key)

## Authentication

### OAuth2 Flow

The app uses the Authorization Code flow:

1. **Authorization Request**
   - User is redirected to: `https://www.bungie.net/en/OAuth/Authorize`
   - Parameters: client_id, response_type=code, redirect_uri
   - Required scopes:
     - `ReadUserData`: Read basic user information
     - `ReadDestinyInventoryAndVault`: Read character inventory and vault
     - `MoveEquipDestinyItems`: Equip items and move them between characters/vault

2. **Token Exchange**
   - Endpoint: `https://www.bungie.net/platform/app/oauth/token/`
   - Exchange authorization code for access token and refresh token
   - Access tokens expire after 1 hour

3. **Token Refresh**
   - Use refresh token to obtain new access token
   - Refresh tokens expire after 90 days

### Required Headers

All API requests require:
```
X-API-Key: YOUR_API_KEY
Authorization: Bearer ACCESS_TOKEN
```

## Key Endpoints

### Get User Profile

```
GET /Destiny2/{membershipType}/Profile/{destinyMembershipId}/
```

**Query Parameters:**
- `components`: Comma-separated list of component IDs
  - `100`: Profiles
  - `200`: Characters
  - `201`: CharacterInventories
  - `205`: CharacterEquipment
  - `102`: ProfileInventories

**Response:**
Returns user profile with requested components.

### Get Character Equipment

```
GET /Destiny2/{membershipType}/Profile/{destinyMembershipId}/Character/{characterId}/
```

**Query Parameters:**
- `components`: `205` for CharacterEquipment

**Response:**
Returns currently equipped items for the character.

### Equip Single Item

```
POST /Destiny2/Actions/Items/EquipItem/
```

**Request Body:**
```json
{
  "itemId": "6917529123456789012",
  "characterId": "2305843009876543210",
  "membershipType": 3
}
```

**Response:**
```json
{
  "Response": 0,
  "ErrorCode": 1,
  "ThrottleSeconds": 0,
  "ErrorStatus": "Success"
}
```

### Equip Multiple Items

```
POST /Destiny2/Actions/Items/EquipItems/
```

**Request Body:**
```json
{
  "itemIds": [
    "6917529123456789012",
    "6917529123456789013",
    "6917529123456789014"
  ],
  "characterId": "2305843009876543210",
  "membershipType": 3
}
```

**Response:**
```json
{
  "Response": {
    "equipResults": [
      {
        "itemInstanceId": "6917529123456789012",
        "equipStatus": 1
      }
    ]
  },
  "ErrorCode": 1,
  "ErrorStatus": "Success"
}
```

### Transfer Item

```
POST /Destiny2/Actions/Items/TransferItem/
```

**Request Body:**
```json
{
  "itemReferenceHash": 3628991658,
  "stackSize": 1,
  "transferToVault": false,
  "itemId": "6917529123456789012",
  "characterId": "2305843009876543210",
  "membershipType": 3
}
```

**Parameters:**
- `transferToVault`: `true` to move to vault, `false` to move to character
- `stackSize`: Number of items to transfer (for stackable items)

## Membership Types

- `1`: Xbox
- `2`: PlayStation
- `3`: Steam
- `4`: Blizzard (deprecated)
- `5`: Stadia (deprecated)
- `6`: Epic Games
- `10`: Demon (internal)
- `254`: BungieNext (cross-save)

## Component IDs

Common component IDs for the `components` parameter:

- `100`: Profiles - Basic profile info
- `102`: ProfileInventories - Items in the profile-level inventory (vault)
- `103`: ProfileCurrencies - Currency information
- `200`: Characters - Character-level information
- `201`: CharacterInventories - Character inventory items
- `205`: CharacterEquipment - Currently equipped items
- `300`: ItemInstances - Detailed item instance data
- `302`: ItemPerks - Item perks and mods
- `304`: ItemStats - Item statistics
- `305`: ItemSockets - Item sockets and plugs
- `307`: ItemTalentGrids - Talent grids (for weapons/armor)

## Error Codes

Common error codes:

- `1`: Success
- `5`: SystemDisabled - The system is temporarily disabled
- `1601`: DestinyAccountNotFound - Account not found
- `1623`: DestinyItemNotFound - Item not found
- `1624`: DestinyItemActionForbidden - Action not allowed on this item
- `1625`: DestinyNoRoomInDestination - No space in destination
- `1626`: DestinyServiceFailure - Service failure
- `1628`: DestinyItemUnequippable - Item cannot be equipped
- `1629`: DestinyItemNotEquippedInSlot - Item not in expected slot
- `1630`: DestinyCannotPerformActionOnEquippedItem - Action requires item to be unequipped

## Best Practices

### Rate Limiting

- Implement exponential backoff for retries
- Cache responses when possible
- Batch operations when the API supports it

### Error Handling

```kotlin
when (response.body()?.ErrorCode) {
    1 -> {
        // Success
    }
    1625 -> {
        // No room in destination - handle vault full scenario
    }
    1628 -> {
        // Item cannot be equipped - inform user
    }
    else -> {
        // Generic error handling
    }
}
```

### Token Management

- Store tokens securely (use EncryptedSharedPreferences)
- Refresh tokens proactively before expiration
- Handle token revocation gracefully

### Data Freshness

- Profile data can become stale
- Re-fetch data after performing actions
- Consider implementing a refresh mechanism

## Example Loadout Equipping Flow

1. **Get Current Equipment**
   ```kotlin
   val currentEquipment = getCharacterEquipment(characterId)
   ```

2. **Check Item Locations**
   ```kotlin
   for (item in loadout.equipment) {
       if (item.location == ItemLocation.VAULT) {
           // Transfer from vault to character
           transferItem(item, toVault = false)
       }
   }
   ```

3. **Equip Items**
   ```kotlin
   equipItems(loadout.equipment.map { it.itemInstanceId })
   ```

4. **Verify Success**
   ```kotlin
   val newEquipment = getCharacterEquipment(characterId)
   // Compare with expected loadout
   ```

## Testing

### Test Endpoints

Bungie does not provide a sandbox environment. Testing must be done carefully:

- Use a test Bungie account if possible
- Test with non-critical items first
- Implement dry-run mode for destructive operations

### Logging

Enable HTTP logging in development:

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

## Resources

- [Bungie API Documentation](https://bungie-net.github.io/)
- [Destiny 2 API Discord](https://discord.gg/bungie)
- [Bungie Help Twitter](https://twitter.com/BungieHelp)
- [API Status Page](https://twitter.com/BungieHelp)

## Manifest Database

For detailed item information (names, descriptions, icons), you'll need to download and use the Destiny 2 Manifest:

```
GET /Destiny2/Manifest/
```

The manifest contains SQLite databases with all static Destiny 2 data. This is essential for:
- Item names and descriptions
- Item icons
- Stat definitions
- Bucket definitions (weapon/armor slots)

## Future Considerations

- Implement manifest database integration for item details
- Add support for mods and perks in loadouts
- Handle exotic weapon/armor restrictions
- Support for subclass configurations
- Loadout sharing via QR codes or links
