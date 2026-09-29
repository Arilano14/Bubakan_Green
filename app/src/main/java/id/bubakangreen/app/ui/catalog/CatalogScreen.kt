package id.bubakangreen.app.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.EmptyState
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantCard
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * CatalogScreen: Perpustakaan Botani Bubakan Green.
 * Refactored with Eco-Green design tokens, responsive search,
 * and mascot empty state integration.
 */
@Composable
fun CatalogScreen(
    onPlantClick: (String) -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Perpustakaan Botani",
                subtitle = "Ensiklopedi Tanaman Bubakan",
                canNavigateBack = false,
                onInfoClick = onInfoClick
            )
        },
        containerColor = BackgroundWarm,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineStatusBar(isOffline = uiState.isOffline)

            // Search Bar with rounded 18dp shape
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        text = "Cari jahe, temulawak, kencur, toga...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Ikon Cari",
                        tint = PrimaryGreen
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Hapus Pencarian",
                            tint = TextSecondary,
                            modifier = Modifier.clickable { viewModel.resetSearch() }
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = BorderDivider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Botanical List Area
            when (val plantsState = uiState.plants) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(4) {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(108.dp),
                                cornerRadius = 22.dp
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Koleksi Ensiklopedia (${plantsState.data.size} Tanaman)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(plantsState.data, key = { it.id }) { plant ->
                            PlantCard(
                                plant = plant,
                                onClick = { onPlantClick(plant.id) }
                            )
                        }
                    }
                }

                is UiState.Empty -> {
                    EmptyState(
                        title = "Tanaman Belum Ditemukan",
                        message = plantsState.message,
                        actionLabel = if (uiState.searchQuery.isNotBlank()) "Reset Pencarian" else null,
                        onActionClick = { viewModel.resetSearch() }
                    )
                }

                is UiState.Error -> {
                    EmptyState(
                        title = "Oops! Ada Sedikit Kendala",
                        message = plantsState.message,
                        actionLabel = "Coba Lagi",
                        onActionClick = { viewModel.loadAllPlants() }
                    )
                }
            }
        }
    }
}
