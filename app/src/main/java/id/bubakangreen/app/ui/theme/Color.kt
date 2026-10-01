package id.bubakangreen.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================================
// BUBAKAN GREEN — ECO-LEARNING COLOR SYSTEM
// Duolingo-inspired friendly, character-driven botanical palette
// ==========================================================

// Primary Botanical Greens
val ForestGreen = Color(0xFF265828)        // Deep rich structural evergreen
val LeafGreen = Color(0xFF4CAF50)          // Fresh lively chlorophyll green
val LeafGreenDark = Color(0xFF1E4620)      // High contrast rim and text accent
val LeafGreenLight = Color(0xFFE8F5E9)     // Soft tender sprout tint
val EcoMint = Color(0xFF81C784)            // Gentle garden mint

// Secondary Energetic Accents (Character / Mascot matching)
val WarmYellow = Color(0xFFFFCA28)         // Radiant friendly sun yellow
val WarmYellowLight = Color(0xFFFFF8E1)    // Soft buttercup pill container
val SoftOrange = Color(0xFFFF7043)         // Lively friendly orange (matches mascot energy)
val SoftOrangeLight = Color(0xFFFBE9E7)    // Soft peach card wash
val SoftCoral = Color(0xFFFF5252)          // Playful highlight coral

// Surfaces & Warm Botanical Paper Canvas
val BotanicalPaper = Color(0xFFFAF7EE)     // Warm organic cream paper style
val SurfaceCard = Color(0xFFFFFFFF)        // Clean white card surface
val SurfaceCardPressed = Color(0xFFF2ECE1) // Tactile pressed state
val BorderCard = Color(0xFFE2DAC8)         // Gentle organic outline
val DividerSoft = Color(0xFFEDE6D6)        // Subtle separation divider

// Deep High-Contrast Readable Typography
val TextPrimary = Color(0xFF1B2E1C)        // Deep forest botanical charcoal (accessible contrast)
val TextSecondary = Color(0xFF566957)      // Readable secondary description
val TextMuted = Color(0xFF7D8F7E)          // Small captions and metadata
val TextOnColor = Color(0xFFFFFFFF)        // Crisp white text on primary buttons

// Feedback & Status Colors
val NaturalGreen = Color(0xFF388E3C)       // Verified / published status
val AlertOrange = Color(0xFFF57C00)        // Pending / warning indicator
val FriendlyRed = Color(0xFFD32F2F)        // Approachable error feedback

// ==========================================================
// BACKWARD-COMPATIBLE ALIASES FOR COMPONENT STABILITY
// ==========================================================
val PrimaryGreen = LeafGreen
val PrimaryGreenDark = ForestGreen
val PrimaryGreenLight = LeafGreenLight
val BackgroundWarm = BotanicalPaper
val Surface = SurfaceCard
val BorderDivider = BorderCard
val MascotYellow = WarmYellow
val MascotOrange = SoftOrange
val AquaAccent = Color(0xFF4DB6AC)
val PinkAccent = SoftCoral

val StatusPublishedGreen = NaturalGreen
val StatusVerifiedGreen = NaturalGreen
val StatusPendingOrange = AlertOrange
val StatusReviewAmber = AlertOrange
val ErrorMaterialRed = FriendlyRed

val PrimarySeedlingGreen = LeafGreen
val PrimaryForest = ForestGreen
val PrimaryForestDark = ForestGreen
val OnPrimaryWhite = TextOnColor
val PrimaryContainerMint = LeafGreenLight
val OnPrimaryContainerDark = ForestGreen

val AccentSunnyGold = WarmYellow
val AccentSunnyContainer = WarmYellowLight
val OnAccentGoldDark = Color(0xFF4E342E)

val AccentDewTeal = AquaAccent
val AccentDewContainer = Color(0xFFE0F2F1)
val OnDewTealDark = Color(0xFF004D40)

val SecondarySage = ForestGreen
val OnSecondaryWhite = TextOnColor
val SecondaryContainer = EcoMint

val BackgroundVanilla = BotanicalPaper
val BackgroundLight = BotanicalPaper
val SurfaceCardWhite = SurfaceCard
val SurfaceWhite = SurfaceCard
val OnSurfaceForestDark = TextPrimary
val OnSurfaceDark = TextPrimary
val OnSurfaceSageMuted = TextSecondary
val OnSurfaceVariant = TextSecondary
val OutlineOrganic = BorderCard
val OutlineGrey = BorderCard
val ErrorRestrainedRed = FriendlyRed
val ErrorRed = FriendlyRed
