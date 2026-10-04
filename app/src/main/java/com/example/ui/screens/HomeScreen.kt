package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AllSubjects
import com.example.data.FlashcardSet
import com.example.data.PaperSet
import com.example.data.QuizSet
import com.example.data.SubjectItem
import com.example.data.UserProfile
import com.example.ui.components.LevelProgressBar
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.HeroGradientBrush
import com.example.ui.theme.IndigoGradientBrush
import com.example.ui.theme.SubjectArtBg
import com.example.ui.theme.SubjectArtBorder
import com.example.ui.theme.SubjectCodingBg
import com.example.ui.theme.SubjectCodingBorder
import com.example.ui.theme.SubjectEconBg
import com.example.ui.theme.SubjectEconBorder
import com.example.ui.theme.SubjectEnglishBg
import com.example.ui.theme.SubjectEnglishBorder
import com.example.ui.theme.SubjectHistoryBg
import com.example.ui.theme.SubjectHistoryBorder
import com.example.ui.theme.SubjectLanguagesBg
import com.example.ui.theme.SubjectLanguagesBorder
import com.example.ui.theme.SubjectMathBg
import com.example.ui.theme.SubjectMathBorder
import com.example.ui.theme.SubjectScienceBg
import com.example.ui.theme.SubjectScienceBorder
import com.example.ui.theme.TealAccent

@Composable
fun HomeScreen(
    profile: UserProfile,
    flashcardSets: List<FlashcardSet>,
    quizSets: List<QuizSet>,
    paperSets: List<PaperSet>,
    onAskClick: (subjectId: String?) -> Unit,
    onStudyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSavedSets = flashcardSets.size + quizSets.size + paperSets.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Center container with max width for clean desktop & tablet look
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
            ) {
                // Hero Banner: Teal -> Grape -> Amber diagonal gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(HeroGradientBrush)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Hey ${profile.name} ${profile.avatarEmoji}",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ready to conquer your study goals today?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            // Streak Badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${profile.streakCount} Day Streak",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Level Progress Bar
                        LevelProgressBar(
                            levelInfo = profile.levelInfo,
                            xp = profile.xp,
                            textColor = Color.White
                        )
                    }
                }
            }
        }

        // "Ask Any Question" CTA Button (Indigo Gradient)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(IndigoGradientBrush)
                        .clickable { onAskClick(null) }
                        .testTag("cta_ask_button")
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ask Any Question",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Step-by-step tutoring in 14 languages with Gemini AI",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // "Pick a Subject" Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
            ) {
                Text(
                    text = "Pick a Subject",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a subject to launch an instant AI tutoring session",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 8 Subject Tiles Grid (2x4 on mobile, 4x2 on tablet/desktop)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val chunkedSubjects = AllSubjects.chunked(4)
                chunkedSubjects.forEach { rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSubjects.forEach { subject ->
                            val (bgColor, borderColor) = getSubjectColors(subject.id)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(bgColor)
                                    .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
                                    .clickable { onAskClick(subject.id) }
                                    .testTag("subject_tile_${subject.id}")
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = subject.emoji,
                                        fontSize = 28.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF22263A) // Deep indigo ink
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // "Your Library" Quick-Access Card (if saved sets exist)
        if (totalSavedSets > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 840.dp)
                        .clickable { onStudyClick() }
                        .testTag("home_library_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(TealAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Your Study Library",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${flashcardSets.size} Flashcard Sets • ${quizSets.size} Quizzes • ${paperSets.size} Exam Papers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Library",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun getSubjectColors(subjectId: String): Pair<Color, Color> {
    return when (subjectId) {
        "math" -> Pair(SubjectMathBg, SubjectMathBorder)
        "science" -> Pair(SubjectScienceBg, SubjectScienceBorder)
        "history" -> Pair(SubjectHistoryBg, SubjectHistoryBorder)
        "english" -> Pair(SubjectEnglishBg, SubjectEnglishBorder)
        "coding" -> Pair(SubjectCodingBg, SubjectCodingBorder)
        "languages" -> Pair(SubjectLanguagesBg, SubjectLanguagesBorder)
        "art" -> Pair(SubjectArtBg, SubjectArtBorder)
        "economics" -> Pair(SubjectEconBg, SubjectEconBorder)
        else -> Pair(SubjectMathBg, SubjectMathBorder)
    }
}
