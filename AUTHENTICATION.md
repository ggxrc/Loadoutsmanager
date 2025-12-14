# Authentication Guide

## Overview

The Loadouts Manager app supports two authentication modes with the Bungie.net API:

1. **API Key Only** (Read-only mode)
2. **Full OAuth2** (Read/Write mode)

## Authentication Options

### Option 1: API Key Only (Recommended if you don't have OAuth credentials)

This mode allows you to:
- ✅ View your Destiny 2 profile and characters
- ✅ Browse your inventory and vault
- ✅ View saved loadouts
- ✅ Create and manage loadouts locally
- ❌ Cannot equip items via the API
- ❌ Cannot transfer items via the API

**Setup:**
1. Go to [Bungie Application Portal](https://www.bungie.net/en/Application)
2. Create or select your application
3. Copy your **API Key**
4. Open `BungieConfig.kt` and paste your API Key:
   ```kotlin
   const val API_KEY = "your_actual_api_key_here"
   ```
5. Leave `CLIENT_ID` and `CLIENT_SECRET` as placeholders

### Option 2: Full OAuth2 (For complete functionality)

This mode provides full access:
- ✅ All features from API Key mode
- ✅ Equip items and loadouts via the API
- ✅ Transfer items between characters and vault
- ✅ Automatic equipment management

**Setup:**
1. Go to [Bungie Application Portal](https://www.bungie.net/en/Application)
2. Create or select your application
3. Configure OAuth2 settings:
   - Set **Redirect URL**: `com.ads.loadoutsmanager://oauth2redirect`
   - Select scopes:
     - `ReadUserData`
     - `ReadDestinyInventoryAndVault`
     - `MoveEquipDestinyItems`
4. Copy your **API Key**, **OAuth Client ID**, and **OAuth Client Secret**
5. Open `BungieConfig.kt` and set all three:
   ```kotlin
   const val API_KEY = "your_actual_api_key_here"
   const val CLIENT_ID = "your_oauth_client_id_here"
   const val CLIENT_SECRET = "your_oauth_client_secret_here"
   ```

## Finding Your Credentials

### API Key
- Located in your Bungie application dashboard
- Labeled as "API Key" or "X-API-Key"
- A long alphanumeric string (e.g., `abc123def456...`)

### OAuth Client ID
- Found in the OAuth2 section of your application
- Labeled as "Client ID" or "OAuth Client ID"
- A numeric ID (e.g., `12345`)

### OAuth Client Secret
- Found in the OAuth2 section of your application
- Labeled as "Client Secret" or "OAuth Client Secret"
- A long alphanumeric string
- **Keep this secret!** Never share it or commit it to public repositories

## Security Considerations

### Client Secret Security

⚠️ **IMPORTANT**: The OAuth Client Secret should **never** be stored in a mobile app in production!

**Current Implementation (Development Only):**
- Client Secret is stored in `BungieConfig.kt` for development/testing
- This is acceptable for personal use or testing

**Production Best Practices:**
1. **Backend Proxy Approach** (Recommended):
   - Create a backend server to handle OAuth2 token exchange
   - Mobile app requests token from your backend
   - Backend uses Client Secret to get token from Bungie
   - Mobile app receives token without ever seeing the secret

2. **PKCE Flow** (Alternative):
   - Use Proof Key for Code Exchange (PKCE) for OAuth2
   - Eliminates the need for Client Secret in public clients
   - Check if Bungie API supports PKCE (as of writing, traditional OAuth2 is standard)

## Understanding the Difference

| Feature | API Key Only | Full OAuth2 |
|---------|-------------|-------------|
| Read inventory | ✅ | ✅ |
| Read vault | ✅ | ✅ |
| Read characters | ✅ | ✅ |
| Save loadouts locally | ✅ | ✅ |
| Equip items via API | ❌ | ✅ |
| Transfer items via API | ❌ | ✅ |
| User authentication | ❌ | ✅ |

## API Key vs OAuth Client ID

Many users confuse these two:

- **API Key**: Identifies your application to Bungie. Required for all API calls.
- **OAuth Client ID**: Used for user authentication and authorization. Allows acting on behalf of a user.

**You mentioned having only an API Key**: This is perfectly fine for read-only operations and local loadout management. You can still use most features of the app!

## What the User Has

Based on the problem statement, you have:
- ✅ API Key
- ❌ OAuth Client ID
- ❌ OAuth Client Secret

**This means you can use the app in API Key Only mode**, which still provides valuable functionality for managing loadouts locally and viewing your Destiny 2 data.

## Obtaining OAuth Credentials

If you want to enable full OAuth2 functionality later:

1. Return to [Bungie Application Portal](https://www.bungie.net/en/Application)
2. Edit your existing application
3. Look for "OAuth2" or "Client Credentials" section
4. If not visible, ensure your application type supports OAuth2
5. Configure the redirect URI: `com.ads.loadoutsmanager://oauth2redirect`
6. Save and note your Client ID and Client Secret

## Testing Your Configuration

The app provides helper methods to check your configuration:

```kotlin
// Check if API Key is set
BungieConfig.isApiKeyConfigured()

// Check if full OAuth2 is configured
BungieConfig.isOAuth2Configured()
```

The app will adapt its functionality based on what credentials are available.

## Troubleshooting

### "API Key Invalid"
- Verify you copied the entire API Key
- Check for extra spaces or characters
- Ensure your Bungie application is active

### "OAuth2 Authentication Failed"
- Verify Client ID and Secret are correct
- Check that redirect URI matches exactly
- Ensure all required scopes are enabled
- Verify your app has OAuth2 enabled in Bungie portal

### "Cannot Equip Items"
- This requires full OAuth2 configuration
- Check that you have Client ID and Secret set
- Verify the user has completed OAuth2 login flow

## References

- [Bungie API Documentation](https://bungie-net.github.io/)
- [Bungie Application Portal](https://www.bungie.net/en/Application)
- [OAuth 2.0 Specification](https://oauth.net/2/)
- [API_GUIDE.md](API_GUIDE.md) - Detailed API integration guide
