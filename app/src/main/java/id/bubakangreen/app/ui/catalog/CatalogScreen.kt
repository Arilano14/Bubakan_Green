package id.bubakangreen.app.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantCard
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateEmptyView
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * CatalogScreen: Botanical Learning Library of Kelurahan Bubakan.
 * Duolingo-inspired friendly search and tactile plant discovery.
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
                title = "Katalog Tanaman",
                subtitle = "Ensiklopedi Botani Bubakan",
                canNavigateBack = false,
                onInfoClick = onInfoClick
            )
        },
        containerColor = BackgroundVanilla,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineStatusBar(isOffline = uiState.isOffline)

            // Chunky Rounded Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        text = "Cari jahe, kunyit, bayam, toga...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceSageMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Ikon Cari",
                        tint = PrimarySeedlingGreen
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Hapus Pencarian",
                            tint = OnSurfaceSageMuted,
                            modifier = Modifier.clickable { viewModel.resetSearch() }
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCardWhite,
                    unfocusedContainerColor = SurfaceCardWhite,
                    focusedBorderColor = PrimarySeedlingGreen,
                    unfocusedBorderColor = OutlineOrganic,
                    focusedTextColor = OnSurfaceForestDark,
                    unfocusedTextColor = OnSurfaceForestDark
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
                                    .height(104.dp),
                                cornerRadius = 20.dp
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
                        items(plantsState.data, key = { it.id }) { plant ->
                            PlantCard(
                                plant = plant,
                                onClick = { onPlantClick(plant.id) }
                            )
                        }
                    }
                }

                is UiState.Empty -> {
                    StateEmptyView(
                        title = "Tanaman Belum Ditemukan",
                        message = plantsState.message,
                        actionLabel = if (uiState.searchQuery.isNotBlank()) "Reset Pencarian" else null,
                        onActionClick = { viewModel.resetSearch() }
                    )
                }

                is UiState.Error -> {
                    StateErrorView(
                        message = plantsState.message,
                        onRetry = { viewModel.loadAllPlants() }
                    )
                }
            }
        }
    }
}
