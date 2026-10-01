package id.bubakangreen.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================================
// BUBAKAN GREEN — ECO-LEARNING COLOR SYSTEM
// Duolingo-inspired friendly, character-driven botanical palette
// ==========================================================

// Primary Botanical Greens (Section 26 Eco Identity)
val ForestGreen = Color(0xFF3F7337)        // Dark #3F7337: Rich structural evergreen & headings
val LeafGreen = Color(0xFF5B9B4A)          // Primary #5B9B4A: Signature Bubakan botanical green
val LeafGreenDark = Color(0xFF325E2B)      // High contrast rim and text accent
val LeafGreenLight = Color(0xFFE7F4DD)     // Soft #E7F4DD: Fresh morning sprout wash & badges
val EcoMint = Color(0xFF68D5C5)            // Accent #68D5C5: Fresh water & audio cue

// Secondary Energetic Accents (Character / Mascot matching)
val WarmYellow = Color(0xFFFFD96A)         // Accent #FFD96A: Warm botanical blossom & primary CTA
val WarmYellowLight = Color(0xFFFFF6D9)    // Soft sunny callout container
val SoftOrange = Color(0xFFFF8A4C)         // Accent #FF8A4C: Mascot energy & friendly highlight
val SoftOrangeLight = Color(0xFFFEE8DC)    // Soft peach card wash
val SoftCoral = Color(0xFFF27B86)          // Accent #F27B86: Playful highlight coral

// Surfaces & Warm Botanical Paper Canvas
val BotanicalPaper = Color(0xFFFFF9EC)     // Cream #FFF9EC: Warm botanical paper canvas
val SurfaceCard = Color(0xFFFFFFFF)        // Clean white card surface
val SurfaceCardPressed = Color(0xFFF6F0DF) // Tactile pressed state
val BorderCard = Color(0xFFE2EAD8)         // Gentle organic outline
val DividerSoft = Color(0xFFEDEADF)        // Subtle separation divider

// Deep High-Contrast Readable Typography
val TextPrimary = Color(0xFF1E331B)        // Deep forest botanical charcoal (accessible contrast)
val TextSecondary = Color(0xFF556852)      // Readable secondary description
val TextMuted = Color(0xFF7A8D78)          // Small captions and metadata
val TextOnColor = Color(0xFFFFFFFF)        // Crisp white text on primary buttons

// Feedback & Status Colors
val NaturalGreen = Color(0xFF3F7337)       // Verified / published status
val AlertOrange = Color(0xFFFF8A4C)        // Pending / warning indicator
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
