package com.ads.loadoutsmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.data.model.ItemLocation
import com.ads.loadoutsmanager.presentation.ui.AuthenticationScreen
import com.ads.loadoutsmanager.presentation.ui.LoadoutListScreen
import com.ads.loadoutsmanager.ui.theme.LoadoutsManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoadoutsManagerTheme {
                LoadoutsManagerApp()
            }
        }
    }
}

@Composable
fun LoadoutsManagerApp() {
    // State for authentication (simplified for demonstration)
    var isAuthenticated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Sample loadouts for demonstration
    val sampleLoadouts = remember {
        listOf(
            DestinyLoadout(
                id = "1",
                name = "PvE Endgame",
                description = "My go-to loadout for raids and dungeons",
                characterId = "char1",
                equipment = listOf(
                    DestinyItem("item1", 123456, 1, ItemLocation.EQUIPPED),
                    DestinyItem("item2", 234567, 2, ItemLocation.EQUIPPED),
                    DestinyItem("item3", 345678, 3, ItemLocation.EQUIPPED)
                ),
                isEquipped = true
            ),
            DestinyLoadout(
                id = "2",
                name = "Crucible Build",
                description = "Optimized for PvP combat",
                characterId = "char1",
                equipment = listOf(
                    DestinyItem("item4", 456789, 1, ItemLocation.VAULT),
                    DestinyItem("item5", 567890, 2, ItemLocation.VAULT),
                    DestinyItem("item6", 678901, 3, ItemLocation.VAULT)
                ),
                isEquipped = false
            ),
            DestinyLoadout(
                id = "3",
                name = "Gambit Setup",
                description = "Balanced for invading and PvE",
                characterId = "char1",
                equipment = listOf(
                    DestinyItem("item7", 789012, 1, ItemLocation.INVENTORY),
                    DestinyItem("item8", 890123, 2, ItemLocation.INVENTORY),
                    DestinyItem("item9", 901234, 3, ItemLocation.INVENTORY)
                ),
                isEquipped = false
            )
        )
    }
    
    if (isAuthenticated) {
        LoadoutListScreen(
            loadouts = sampleLoadouts,
            isLoading = isLoading,
            error = error,
            onLoadoutClick = { loadout ->
                // Handle loadout selection
            },
            onEquipLoadout = { loadout ->
                // Handle equip action
                error = "Equipping ${loadout.name}... (API integration required)"
            },
            onDeleteLoadout = { loadout ->
                // Handle delete action
                error = "Delete functionality requires implementation"
            },
            onCreateLoadout = {
                // Handle create action
                error = "Create functionality requires implementation"
            },
            onClearError = { error = null }
        )
    } else {
        AuthenticationScreen(
            isAuthenticated = isAuthenticated,
            isLoading = isLoading,
            error = error,
            onLoginClick = {
                // Simulate login (actual OAuth2 implementation required)
                isAuthenticated = true
                error = "OAuth2 authentication requires Bungie API configuration"
            },
            onLogoutClick = {
                isAuthenticated = false
            },
            onClearError = { error = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadoutsManagerAppPreview() {
    LoadoutsManagerTheme {
        LoadoutsManagerApp()
    }
}
