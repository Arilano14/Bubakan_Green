package id.bubakangreen.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.ErrorRed
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceWhite

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPicDashboard: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToPicDashboard -> onNavigateToPicDashboard()
                is LoginNavigationEvent.NavigateToAdminDashboard -> onNavigateToAdminDashboard()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertifocal(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Beranda",
                        tint = OnSurfaceDark
                    )
                }
                Text(
                    text = "Masuk Petugas & Admin",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botanical Lock Icon Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimaryContainerMint,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PrimaryForest,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BUBAKAN GREEN",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryForest,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "Kelurahan Bubakan, Mijen",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masuk menggunakan kredensial resmi untuk mengelola data kebun dan tanaman.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = OnSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Banner
            if (uiState.errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email Petugas / Admin") },
                placeholder = { Text("contoh: pic@bubakan.id") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    focusedLabelColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Kata Sandi") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.signIn()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                            tint = OnSurfaceVariant
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    focusedLabelColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button (Min 48dp touch target)
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn()
                },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryForest,
                    contentColor = OnPrimaryWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = OnPrimaryWhite,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Masuk ke Sistem",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Demo Quick-Fill Section for Reviewers
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Akses Cepat Pengujian (Reviewer)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.onEmailChange("pic.kebun@bubakan.id")
                                viewModel.onPasswordChange("password123")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Akun PIC",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.onEmailChange("admin.kelurahan@bubakan.id")
                                viewModel.onPasswordChange("password123")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Akun Admin",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}
