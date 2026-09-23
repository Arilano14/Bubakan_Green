package id.bubakangreen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.bubakangreen.app.ui.theme.BubakanGreenTheme

/**
 * Main Activity hosting BUBAKAN GREEN.
 * Connects the public Compose navigation shell, trilingual botanical encyclopedia,
 * community garden directory, and deep-link routing.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubakanGreenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubakanAppNavHost()
                }
            }
        }
    }
}
