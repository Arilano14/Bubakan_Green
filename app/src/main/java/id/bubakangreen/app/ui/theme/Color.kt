package id.bubakangreen.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================================
// BUBAKAN GREEN — ECO-GREEN COLOR SYSTEM
// Baseline Eco-Green identity with Mascot emotional energy
// ==========================================================

// Core Brand Greens (50–60% structural brand presence)
val PrimaryGreen = Color(0xFF5B9B4A)       // Fresh botanical chlorophyll green
val PrimaryGreenDark = Color(0xFF3F7337)   // Deep tactile rim & structural contrast
val PrimaryGreenLight = Color(0xFFE7F4DD)  // Soft sprout container wash
val EcoMint = Color(0xFF8FD8B0)            // Calming garden mint accent

// Mascot Emotional Accents (5–10% accent presence)
val MascotYellow = Color(0xFFFFD96A)      // Warm radiant bud yellow
val MascotOrange = Color(0xFFFF8A4C)      // Warm gradient glow orange
val Coral = Color(0xFFF46B5F)             // Warm energetic coral
val PinkAccent = Color(0xFFF27B86)        // Mascot cape / friendly spark accent
val AquaAccent = Color(0xFF68D5C5)        // Pure morning dew / audio accent

// Surfaces & Warm Canvases (25–35% clean breathing space)
val BackgroundWarm = Color(0xFFFFF9EC)    // Warm organic cream canvas
val Surface = Color(0xFFFFFFFF)           // Crisp card surface
val TextPrimary = Color(0xFF28352A)       // Deep readable botanical charcoal
val TextSecondary = Color(0xFF667267)     // Gentle readable secondary text
val BorderDivider = Color(0xFFDCE7D8)     // Soft organic divider

// Status & Material Feedback
val StatusPublishedGreen = Color(0xFF5B9B4A)
val StatusVerifiedGreen = StatusPublishedGreen
val StatusPendingOrange = Color(0xFFFF8A4C)
val StatusReviewAmber = StatusPendingOrange
val ErrorMaterialRed = Color(0xFFBA1A1A)   // Standard Material error color

// ==========================================================
// BACKWARD-COMPATIBLE ALIASES FOR EXISTING UI COMPONENTS
// ==========================================================
val PrimarySeedlingGreen = PrimaryGreen
val PrimaryForest = PrimaryGreen
val PrimaryForestDark = PrimaryGreenDark
val OnPrimaryWhite = Color(0xFFFFFFFF)
val PrimaryContainerMint = PrimaryGreenLight
val OnPrimaryContainerDark = Color(0xFF1B3814)

val AccentSunnyGold = MascotYellow
val AccentSunnyContainer = Color(0xFFFFF5D6)
val OnAccentGoldDark = Color(0xFF4D3800)

val AccentDewTeal = AquaAccent
val AccentDewContainer = Color(0xFFE2F8F5)
val OnDewTealDark = Color(0xFF0F4E47)

val SecondarySage = PrimaryGreenDark
val OnSecondaryWhite = Color(0xFFFFFFFF)
val SecondaryContainer = EcoMint

val BackgroundVanilla = BackgroundWarm
val BackgroundLight = BackgroundWarm
val SurfaceCardWhite = Surface
val SurfaceWhite = Surface
val OnSurfaceForestDark = TextPrimary
val OnSurfaceDark = TextPrimary
val OnSurfaceSageMuted = TextSecondary
val OnSurfaceVariant = TextSecondary
val OutlineOrganic = BorderDivider
val OutlineGrey = BorderDivider
val ErrorRestrainedRed = ErrorMaterialRed
val ErrorRed = ErrorMaterialRed
