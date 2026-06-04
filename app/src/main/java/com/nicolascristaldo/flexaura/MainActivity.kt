package com.nicolascristaldo.flexaura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.features.auth.navigation.AuthGraph
import com.nicolascristaldo.flexaura.navigation.FlexAuraNavHost
import com.nicolascristaldo.flexaura.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlexAuraTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authState = viewModel.authState.collectAsStateWithLifecycle()
                    val navController = rememberNavController()

                    when (authState.value) {
                        is AuthState.Loading -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is AuthState.Authenticated -> {

                            FlexAuraNavHost(
                                navController = navController,
                                startDestination = HomeDestination
                            )
                        }

                        is AuthState.UnAuthenticated -> {
                            FlexAuraNavHost(
                                navController = navController,
                                startDestination = AuthGraph
                            )
                        }
                    }
                }
            }
        }
    }
}