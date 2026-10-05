package id.bubakangreen.app.ui.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.domain.model.QuizResultState
import id.bubakangreen.app.domain.model.QuizSession
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.EmptyState
import id.bubakangreen.app.ui.components.MandarinSpeakerButton
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantImage
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.SecondaryButton
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.theme.AquaAccent
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * PlantDetailScreen: Integrated botanical mini-lesson with optional Chinese audio
 * and interactive 5-question general knowledge quiz from question bank.
 */
@Composable
fun PlantDetailScreen(
    plantId: String,
    onNavigateBack: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLessonCompleted by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (uiState.quizSession != null) {
            viewModel.exitQuiz()
        } else {
            onNavigateBack()
        }
    }

    LaunchedEffect(plantId) {
        viewModel.loadPlantDetail(plantId)
    }

    LaunchedEffect(uiState.audioState) {
        if (uiState.audioState is AudioState.Error) {
            snackbarHostState.showSnackbar((uiState.audioState as AudioState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState.plant is UiState.Success && uiState.quizSession == null) {
                Surface(
                    color = id.bubakangreen.app.ui.theme.SurfaceCard,
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        if (isLessonCompleted) {
                            PrimaryButton(
                                text = "Kembali ke Katalog",
                                onClick = onNavigateBack,
                                height = 54.dp
                            )
                        } else {
                            PrimaryButton(
                                text = "Selesai Mengenal Tanaman",
                                onClick = { isLessonCompleted = true },
                                height = 54.dp
                            )
                        }
                    }
                }
            }
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

            // When quiz session is active, render interactive quiz view
            val session = uiState.quizSession
            if (session != null) {
                QuizInteractiveView(
                    session = session,
                    onSelectOption = { viewModel.selectOption(it) },
                    onConfirmAnswer = { viewModel.confirmAnswer() },
                    onNextQuestion = { viewModel.nextQuestion() },
                    onRetryQuiz = { viewModel.retryQuiz() },
                    onExitQuiz = { viewModel.exitQuiz() }
                )
            } else {
                when (val plantState = uiState.plant) {
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                cornerRadius = 24.dp
                            )
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                cornerRadius = 20.dp
                            )
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                cornerRadius = 20.dp
                            )
                        }
                    }

                    is UiState.Success -> {
                        val plant = plantState.data
                        val isScrolled by remember {
                            derivedStateOf { scrollState.value > 60 }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                // Hero plant photo
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 10f)
                                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                                        .background(PrimaryGreenLight)
                                ) {
                                    PlantImage(
                                        plant = plant,
                                        contentDescription = "Foto tanaman ${plant.nameId}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Content Column
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 20.dp)
                                ) {
                                // Plant name & scientific name
                                Text(
                                    text = plant.nameId,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                if (plant.nameLatin.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = plant.nameLatin,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Mandarin pronunciation pod (optional audio supported)
                                if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                    MandarinPod(
                                        hanzi = plant.nameMandarin.orEmpty(),
                                        pinyin = plant.pinyin.orEmpty(),
                                        audioState = uiState.audioState,
                                        hasAudio = uiState.hasChineseVoice,
                                        onPlayAudio = { viewModel.playMandarinAudio() }
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // Educational guide section with mascot
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Kenalan Lebih Dekat",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryGreenDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Panduan botani & perawatan",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    if (!isLessonCompleted) {
                                        Mascot(
                                            type = MascotType.LEARNING,
                                            size = 100.dp,
                                            baseRotation = -2f,
                                            animateIdle = true
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = plant.description.ifBlank {
                                        "Tanaman ini merupakan salah satu flora unggulan yang dibudidayakan di kebun binaan Kelurahan Bubakan untuk penghijauan serta pemenuhan tanaman obat keluarga (TOGA) mandiri."
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = TextPrimary
                                )

                                if (plant.characteristics.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BotanicalInfoCard(
                                        title = "Karakteristik Tanaman",
                                        icon = Icons.Default.Info,
                                        content = plant.characteristics
                                    )
                                }

                                val usefulBenefits = plant.commonUses.ifBlank { plant.benefits }
                                if (usefulBenefits.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BotanicalInfoCard(
                                        title = "Khasiat & Kegunaan",
                                        icon = Icons.Default.Spa,
                                        content = usefulBenefits
                                    )
                                }

                                val cultivation = plant.cultivationNotes.ifBlank { plant.plantingGuide }
                                if (cultivation.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BotanicalInfoCard(
                                        title = "Panduan Budidaya",
                                        icon = Icons.Default.Eco,
                                        content = cultivation
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Garden presence
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(PrimaryGreenLight.copy(alpha = 0.5f))
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = PrimaryGreenDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Lokasi di Bubakan",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryGreenDark
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Dapat ditemukan di kebun binaan Kelurahan Bubakan.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Interactive Quiz launcher card section
                                PlantQuizCardSection(
                                    canStartQuiz = uiState.canStartQuiz,
                                    questionCount = uiState.activeQuestions.size,
                                    onStartQuiz = { viewModel.startQuiz() }
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Completion state celebration
                                if (isLessonCompleted) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Mascot(
                                            type = MascotType.HAPPY,
                                            size = 140.dp,
                                            animateIdle = true
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Kamu sudah mengenal tanaman ini",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreenDark,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Terima kasih sudah belajar bersama Bubakan Green",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Pinned Floating Top Actions: Arrow & QR button (Never scroll away)
                        Surface(
                            color = if (isScrolled) BackgroundWarm.copy(alpha = 0.98f) else Color.Transparent,
                            shadowElevation = if (isScrolled) 4.dp else 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 4.dp,
                                    border = BorderStroke(1.dp, OutlineOrganic.copy(alpha = 0.6f)),
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clickable(onClick = onNavigateBack)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Kembali",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                if (isScrolled) {
                                    Text(
                                        text = plant.nameId,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                }

                                if (plant.isPublished) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White,
                                        shadowElevation = 4.dp,
                                        border = BorderStroke(1.dp, OutlineOrganic.copy(alpha = 0.6f)),
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clickable { showQrDialog = true }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Outlined.QrCode2,
                                                contentDescription = "Lihat Kode QR",
                                                tint = ForestGreen,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                } else if (isScrolled) {
                                    Spacer(modifier = Modifier.size(46.dp))
                                }
                            }
                        }
                    }
                }

                is UiState.Empty -> {
                        EmptyState(
                            title = "Informasi Belum Tersedia",
                            message = "Data tanaman ini belum lengkap.",
                            actionLabel = "Kembali ke Katalog",
                            onActionClick = onNavigateBack
                        )
                    }

                    is UiState.Error -> {
                        EmptyState(
                            title = "Oops! Ada Sedikit Kendala",
                            message = plantState.message,
                            actionLabel = "Coba Lagi",
                            onActionClick = { viewModel.loadPlantDetail(plantId) }
                        )
                    }
                }
            }
        }
    }

    if (showQrDialog && uiState.plant is UiState.Success) {
        val currentPlant = (uiState.plant as UiState.Success).data
        val canonicalUrl = remember(currentPlant.id) {
            id.bubakangreen.app.core.util.QrUrlBuilder.buildPlantUrl(currentPlant.id)
        }
        id.bubakangreen.app.ui.components.QrCodeDisplayDialog(
            title = currentPlant.nameId,
            subtitle = currentPlant.nameLatin,
            canonicalUrl = canonicalUrl,
            onDismiss = { showQrDialog = false }
        )
    }
}

/**
 * Quiz Entry Card inside the botanical guide.
 */
@Composable
private fun PlantQuizCardSection(
    canStartQuiz: Boolean,
    questionCount: Int,
    onStartQuiz: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canStartQuiz) Color(0xFFF1F8E9) else Color(0xFFF5F5F5)
        ),
        border = BorderStroke(
            1.dp,
            if (canStartQuiz) PrimaryGreen.copy(alpha = 0.5f) else BorderDivider
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (canStartQuiz) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = if (canStartQuiz) PrimaryGreenDark else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Uji Pengetahuan Tanaman",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (canStartQuiz) {
                            "Uji pemahamanmu tentang tanaman ini."
                        } else {
                            "Kuis belum tersedia untuk tanaman ini."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (canStartQuiz) {
                PrimaryButton(
                    text = "Mulai Kuis",
                    onClick = onStartQuiz,
                    height = 48.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada soal kuis aktif untuk tanaman ini.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Interactive Quiz View: Renders question 1 of 5 through question 5 of 5,
 * or the celebratory result view with scoring.
 */
@Composable
private fun QuizInteractiveView(
    session: QuizSession,
    onSelectOption: (String) -> Unit,
    onConfirmAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRetryQuiz: () -> Unit,
    onExitQuiz: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Quiz Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onExitQuiz),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Materi",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = if (session.isFinished) "Hasil Kuis" else "Kuis Tanaman",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreenDark
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (session.isFinished) {
            // Finished Result Card
            QuizResultCard(
                session = session,
                onRetryQuiz = onRetryQuiz,
                onExitQuiz = onExitQuiz
            )
        } else {
            val q = session.currentQuestion
            if (q != null) {
                // Progress Indicator
                val progress = (session.currentIndex + 1).toFloat() / session.totalQuestions.toFloat()
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Soal ${session.currentIndex + 1} dari ${session.totalQuestions}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreenDark
                        )
                        Text(
                            text = "${((progress) * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimaryGreen,
                        trackColor = Color(0xFFE0E0E0)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Question Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = q.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 24.sp,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Options A, B, C
                        QuizOptionItem(
                            letter = "A",
                            text = q.optionA,
                            isSelected = session.selectedOption == "A",
                            isConfirmed = session.isAnswerConfirmed,
                            isCorrect = q.correctAnswer == "A",
                            onSelect = { onSelectOption("A") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        QuizOptionItem(
                            letter = "B",
                            text = q.optionB,
                            isSelected = session.selectedOption == "B",
                            isConfirmed = session.isAnswerConfirmed,
                            isCorrect = q.correctAnswer == "B",
                            onSelect = { onSelectOption("B") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        QuizOptionItem(
                            letter = "C",
                            text = q.optionC,
                            isSelected = session.selectedOption == "C",
                            isConfirmed = session.isAnswerConfirmed,
                            isCorrect = q.correctAnswer == "C",
                            onSelect = { onSelectOption("C") }
                        )

                        // Explanation displayed after confirmation
                        AnimatedVisibility(
                            visible = session.isAnswerConfirmed && q.explanation.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Penjelasan:",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreen
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = q.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = 18.sp,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom confirmation or next button
                if (!session.isAnswerConfirmed) {
                    PrimaryButton(
                        text = "Konfirmasi Jawaban",
                        onClick = onConfirmAnswer,
                        enabled = session.selectedOption != null,
                        height = 52.dp
                    )
                } else {
                    val isLast = session.currentIndex + 1 >= session.totalQuestions
                    PrimaryButton(
                        text = if (isLast) "Lihat Hasil" else "Lanjut ke Soal Berikutnya",
                        onClick = onNextQuestion,
                        height = 52.dp
                    )
                }
            }
        }
    }
}

/**
 * Single Quiz Option Card (Touch target >= 48dp, clearly visible feedback states).
 */
@Composable
private fun QuizOptionItem(
    letter: String,
    text: String,
    isSelected: Boolean,
    isConfirmed: Boolean,
    isCorrect: Boolean,
    onSelect: () -> Unit
) {
    val (bgColor, borderColor, textColor) = when {
        isConfirmed -> {
            when {
                isCorrect -> Triple(Color(0xFFE8F5E9), ForestGreen, ForestGreen)
                isSelected && !isCorrect -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Color(0xFFC62828))
                else -> Triple(Color.White, BorderDivider, TextSecondary)
            }
        }
        isSelected -> Triple(PrimaryGreenLight.copy(alpha = 0.45f), PrimaryGreen, PrimaryGreenDark)
        else -> Triple(Color.White, BorderDivider, TextPrimary)
    }

    Surface(
        onClick = onSelect,
        enabled = !isConfirmed,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected || (isConfirmed && isCorrect)) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Letter Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isConfirmed && isCorrect) ForestGreen
                        else if (isConfirmed && isSelected && !isCorrect) Color(0xFFD32F2F)
                        else if (isSelected) PrimaryGreen
                        else Color(0xFFE0E0E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isConfirmed && isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Benar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (isConfirmed && isSelected && !isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salah",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || (isConfirmed && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Quiz Completion Result Card with score, encouraging mascot, and feedback.
 */
@Composable
private fun QuizResultCard(
    session: QuizSession,
    onRetryQuiz: () -> Unit,
    onExitQuiz: () -> Unit
) {
    val score = session.score
    val resultState = session.resultState

    val mascotType = when (resultState) {
        QuizResultState.PERFECT -> MascotType.HAPPY
        QuizResultState.GOOD -> MascotType.LEARNING
        QuizResultState.ENCOURAGEMENT -> MascotType.THINKING
    }

    val feedbackTitle = when (resultState) {
        QuizResultState.PERFECT -> "Luar Biasa, Sempurna"
        QuizResultState.GOOD -> "Bagus Sekali"
        QuizResultState.ENCOURAGEMENT -> "Tetap Semangat Belajar"
    }

    val feedbackDesc = when (resultState) {
        QuizResultState.PERFECT -> "Kamu berhasil menjawab seluruh pertanyaan dengan tepat."
        QuizResultState.GOOD -> "Kamu sudah memahami sebagian besar materi tanaman ini."
        QuizResultState.ENCOURAGEMENT -> "Pelajari kembali materi karakteristik tanaman dan coba lagi."
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Mascot(
                type = mascotType,
                size = 140.dp,
                animateIdle = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = feedbackTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryGreenDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Large Score Display
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$score",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ 100",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Text(
                text = "${session.correctCount} dari ${session.totalQuestions} pertanyaan dijawab dengan benar",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feedbackDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Coba Lagi",
                onClick = onRetryQuiz,
                height = 52.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Kembali ke Materi",
                onClick = onExitQuiz,
                height = 52.dp
            )
        }
    }
}

/**
 * Mandarin pronunciation pod — clean, teal-accented.
 */
@Composable
private fun MandarinPod(
    hanzi: String,
    pinyin: String,
    audioState: AudioState,
    hasAudio: Boolean,
    onPlayAudio: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AquaAccent.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PELAFALAN MANDARIN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    if (hanzi.isNotBlank()) {
                        Text(
                            text = hanzi,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreenDark
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (pinyin.isNotBlank()) {
                        Text(
                            text = pinyin,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }
            MandarinSpeakerButton(
                audioState = audioState,
                onClick = onPlayAudio,
                enabled = hasAudio
            )
        }
    }
}

@Composable
private fun BotanicalInfoCard(
    title: String,
    icon: ImageVector,
    content: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreenDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreenDark
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = TextPrimary
            )
        }
    }
}
