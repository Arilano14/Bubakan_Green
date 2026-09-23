package id.bubakangreen.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.ui.theme.OnPrimaryContainerDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.SurfaceWhite

/**
 * Filter chip for category selection (Semua, Urban Farming, Taman Toga).
 * Adheres to 48dp minimum touch target for accessibility.
 */
@Composable
fun CategoryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryContainerMint,
            selectedLabelColor = OnPrimaryContainerDark,
            containerColor = SurfaceWhite,
            labelColor = OnSurfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) PrimaryContainerMint else OutlineGrey
        ),
        modifier = modifier.heightIn(min = 48.dp)
    )
}
