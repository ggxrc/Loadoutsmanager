package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ads.loadoutsmanager.presentation.viewmodel.AuthViewModel
import net.openid.appauth.AuthorizationService

/**
 * Login screen with Bungie OAuth2 authentication
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    val authorizationService = remember { AuthorizationService(context) }
    
    DisposableEffect(Unit) {
        onDispose {
            authorizationService.dispose()
        }
    }
    
    // Handle authentication state
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            onAuthSuccess()
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title
            Text(
                text = "Loadouts Manager",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "for Destiny 2",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // State-based UI
            when (val state = authState) {
                is AuthViewModel.AuthState.NotAuthenticated -> {
                    LoginContent(
                        onLoginClick = {
                            authViewModel.startAuth(authorizationService)
                            // TODO: Launch browser for OAuth
                        }
                    )
                }
                
                is AuthViewModel.AuthState.Authenticating -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Authenticating...")
                }
                
                is AuthViewModel.AuthState.Authenticated -> {
                    Text("Welcome, ${state.displayName}!")
                }
                
                is AuthViewModel.AuthState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = {
                            authViewModel.startAuth(authorizationService)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginContent(onLoginClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sign in with your Bungie account to manage your Destiny 2 loadouts",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Sign in with Bungie")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You'll be redirected to Bungie.net to authorize this app",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Authentication Failed",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}