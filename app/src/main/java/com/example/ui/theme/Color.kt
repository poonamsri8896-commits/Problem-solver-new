package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Mode
val WarmCream = Color(0xFFFDF9F3)
val DeepIndigo = Color(0xFF22263A)
val MutedSecondary = Color(0xFF6E7391)
val AmberAccent = Color(0xFFFFB238)
val AmberDeeper = Color(0xFFE9932A)
val TealAccent = Color(0xFF22C3A6)
val TealDeeper = Color(0xFF0FA085)
val CoralAccent = Color(0xFFFF7A6B)
val GrapeAccent = Color(0xFF8B7FE8)
val CardWhite = Color(0xFFFFFFFF)
val BorderLight = Color(0xFFEFE7D8)

// Dark Mode
val DarkBackground = Color(0xFF141726)
val DarkText = Color(0xFFF1EEE5)
val DarkMutedText = Color(0xFF9BA0C0)
val DarkCard = Color(0xFF1D2136)
val DarkBorder = Color(0xFF2B3050)
val DarkAmber = Color(0xFFFFC059)
val DarkTeal = Color(0xFF35D6B9)
val DarkCoral = Color(0xFFFF8F82)
val DarkGrape = Color(0xFF9F95EE)

// Subject Pastel Colors (Light & Dark friendly)
val SubjectMathBg = Color(0xFFFFF4E5)
val SubjectMathBorder = Color(0xFFFFD199)
val SubjectScienceBg = Color(0xFFE6FAF6)
val SubjectScienceBorder = Color(0xFFA1EEDB)
val SubjectHistoryBg = Color(0xFFFBF0E8)
val SubjectHistoryBorder = Color(0xFFF2C8B0)
val SubjectEnglishBg = Color(0xFFEFF3FF)
val SubjectEnglishBorder = Color(0xFFC7D5FF)
val SubjectCodingBg = Color(0xFFF0EBFB)
val SubjectCodingBorder = Color(0xFFD3C2F7)
val SubjectLanguagesBg = Color(0xFFE8F7F0)
val SubjectLanguagesBorder = Color(0xFFAFE0C9)
val SubjectArtBg = Color(0xFFFFF0F5)
val SubjectArtBorder = Color(0xFFFFC4DA)
val SubjectEconBg = Color(0xFFF9F7E8)
val SubjectEconBorder = Color(0xFFEAE2A7)

// Gradients
val IndigoGradientStart = Color(0xFF262B45)
val IndigoGradientEnd = Color(0xFF3D4270)
val IndigoGradientBrush = Brush.linearGradient(
    colors = listOf(IndigoGradientStart, IndigoGradientEnd)
)

val HeroGradientBrush = Brush.linearGradient(
    colors = listOf(TealAccent, GrapeAccent, AmberAccent)
)
