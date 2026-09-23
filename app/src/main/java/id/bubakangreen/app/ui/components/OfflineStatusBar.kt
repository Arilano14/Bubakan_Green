package id.bubakangreen.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.ui.theme.OnPrimaryContainerDark
import id.bubakangreen.app.ui.theme.PrimaryContainerMint

/**
 * Non-intrusive banner indicating offline cached presentation.
 * Uses soft natural mint wash instead of alarming red warnings.
 */
@Composable
fun OfflineStatusBar(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
    message: String = "📡 Mode Offline — Menampilkan data terakhir tersimpan"
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryContainerMint)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                color = OnPrimaryContainerDark,
                textAlign = TextAlign.Center
            )
        }
    }
}
