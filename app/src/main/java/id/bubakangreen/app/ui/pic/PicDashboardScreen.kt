package id.bubakangreen.app.ui.pic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.StatusPendingOrange
import id.bubakangreen.app.ui.theme.StatusVerifiedGreen
import id.bubakangreen.app.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicDashboardScreen(
    viewModel: PicDashboardViewModel,
    onNavigateToLocationForm: (String?) -> Unit,
    onNavigateToPlantForm: (String) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val session by viewModel.userSession.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard Petugas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        Text(
                            text = session?.displayName ?: session?.email ?: "Petugas Lapangan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.signOut(onSignedOut) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar dari Akun Petugas",
                            tint = OnSurfaceDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Action Banner
            Surface(
                color = SurfaceWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Kelola Kebun Warga",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                    Text(
                        text = "Daftarkan kebun baru atau perbarui tanaman di kebun binaan Anda.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onNavigateToLocationForm(null) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryForest,
                            contentColor = OnPrimaryWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daftarkan Kebun Baru",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Garden List State
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryForest)
                    }
                }
                is UiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceVariant
                            )
                        )
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = viewModel::loadData) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data, key = { it.id }) { location ->
                            PicLocationItem(
                                location = location,
                                onManagePlants = { onNavigateToPlantForm(location.id) },
                                onEditProfile = { onNavigateToLocationForm(location.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PicLocationItem(
    location: Location,
    onManagePlants: () -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type & RW Badge
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimaryContainerMint
                    ) {
                        Text(
                            text = if (location.type == LocationType.URBAN_FARMING) "Urban Farming" else "Taman Toga",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryForest,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BackgroundLight
                    ) {
                        Text(
                            text = "RW ${location.rw}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Status Badge
                val isPublished = location.status == LocationStatus.PUBLISHED
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPublished) PrimaryContainerMint else BackgroundLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPublished) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isPublished) StatusVerifiedGreen else StatusPendingOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPublished) "Terbit" else "Menunggu Review",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPublished) StatusVerifiedGreen else StatusPendingOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = location.address,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = OnSurfaceVariant
                ),
                maxLines = 1
            )

            if (location.rejectionNote != null && location.status != LocationStatus.PUBLISHED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Catatan Admin: ${location.rejectionNote}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit Profil",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Button(
                    onClick = onManagePlants,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryForest,
                        contentColor = OnPrimaryWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kelola Tanaman",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
