package com.ads.loadoutsmanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ads.loadoutsmanager.presentation.ui.LoginScreen
import com.ads.loadoutsmanager.presentation.viewmodel.AuthViewModel
import com.ads.loadoutsmanager.ui.theme.LoadoutsManagerTheme
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

class MainActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels {
        val app = application as LoadoutsApplication
        AuthViewModel.Factory(app.tokenStorage, app.authRepository, this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle OAuth callback if launched via redirect
        checkIntent(intent)
        
        enableEdgeToEdge()
        setContent {
            LoadoutsManagerTheme {
                LoadoutsManagerApp(authViewModel)
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Needed if activity launchMode is singleTop or singleTask
        checkIntent(intent)
    }
    
    private fun checkIntent(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)
        
        if (response != null || ex != null) {
            val service = AuthorizationService(this)
            authViewModel.handleAuthCallback(service, response, ex)
        }
    }
}

@Composable
fun LoadoutsManagerApp(authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.collectAsState()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LoginScreen(
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onAuthSuccess = {
                // TODO: Navigate to loadout list
            }
        )
    }
}
