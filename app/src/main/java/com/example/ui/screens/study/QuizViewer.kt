package com.example.ui.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundPlayer
import com.example.data.QuizQuestion
import com.example.data.QuizSet
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.IndigoGradientBrush
import com.example.ui.theme.TealAccent

@Composable
fun QuizViewer(
    quizSet: QuizSet,
    soundEnabled: Boolean,
    onQuizComplete: (correctCount: Int, totalCount: Int) -> Unit,
    onUpdateSet: (QuizSet) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf(quizSet.title) }

    val questions = quizSet.questions
    val currentQuestion = questions.getOrNull(currentIndex)

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Quiz") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newTitle.trim()
                        if (trimmed.isNotEmpty()) {
                            onUpdateSet(quizSet.copy(title = trimmed))
                        }
                        showRenameDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Quiz")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = quizSet.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = {
                    newTitle = quizSet.title
                    showRenameDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename Quiz",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isQuizFinished && currentQuestion != null) {
                Text(
                    text = "Q ${currentIndex + 1} / ${questions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
            } else {
                Spacer(modifier = Modifier.width(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuizFinished) {
            // Trophy + Score Screen
            val percentage = if (questions.isNotEmpty()) {
                ((correctAnswersCount.toDouble() / questions.size.toDouble()) * 100).toInt()
            } else 0

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AmberAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = AmberAccent,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (percentage >= 80) "Outstanding Work!" else if (percentage >= 50) "Great Effort!" else "Keep Practicing!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You scored $correctAnswersCount out of ${questions.size} ($percentage%)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // XP Earned Banner
                val xpEarned = 15 + (correctAnswersCount * 2)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(TealAccent.copy(alpha = 0.18f))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "+$xpEarned XP Earned!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Button(
                        onClick = {
                            currentIndex = 0
                            selectedOptionIndex = null
                            answered = false
                            correctAnswersCount = 0
                            isQuizFinished = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Quiz", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(IndigoGradientBrush)
                            .clickable { onClose() }
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Finish", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (currentQuestion != null) {
            // Interactive Question Card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentQuestion.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = selectedOptionIndex == optIndex
                        val isCorrect = optIndex == currentQuestion.correctIndex

                        val (cardBg, cardBorder, textColor) = when {
                            !answered -> {
                                if (isSelected) {
                                    Triple(MaterialTheme.colorScheme.surfaceVariant, TealAccent, MaterialTheme.colorScheme.onSurface)
                                } else {
                                    Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            isCorrect -> {
                                // Green for correct
                                Triple(Color(0xFFE6FAF6), TealAccent, Color(0xFF0FA085))
                            }
                            isSelected -> {
                                // Coral for wrong
                                Triple(Color(0xFFFFECEB), CoralAccent, Color(0xFFD32F2F))
                            }
                            else -> {
                                Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.5.dp, cardBorder, RoundedCornerShape(16.dp))
                                .clickable(enabled = !answered) {
                                    selectedOptionIndex = optIndex
                                    answered = true
                                    if (isCorrect) {
                                        correctAnswersCount++
                                        SoundPlayer.playCorrectChime(soundEnabled)
                                    } else {
                                        SoundPlayer.playWrongBuzz(soundEnabled)
                                    }
                                }
                                .padding(16.dp)
                                .testTag("quiz_option_$optIndex")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(cardBorder.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = textColor,
                                        fontWeight = if (answered && isCorrect) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                if (answered && isCorrect) {
                                    Text("✓ Correct", color = Color(0xFF0FA085), fontWeight = FontWeight.Bold)
                                } else if (answered && isSelected && !isCorrect) {
                                    Text("✗ Wrong", color = CoralAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation & Delete Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete Question button (min 1)
                IconButton(
                    onClick = {
                        if (questions.size > 1) {
                            val updatedQuestions = questions.filterIndexed { idx, _ -> idx != currentIndex }
                            val newIdx = currentIndex.coerceAtMost(updatedQuestions.size - 1)
                            currentIndex = newIdx
                            selectedOptionIndex = null
                            answered = false
                            onUpdateSet(quizSet.copy(questions = updatedQuestions))
                        }
                    },
                    enabled = questions.size > 1,
                    modifier = Modifier.testTag("delete_question_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Question",
                        tint = if (questions.size > 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }

                // Next or Finish Button
                if (answered) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(IndigoGradientBrush)
                            .clickable {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                    selectedOptionIndex = null
                                    answered = false
                                } else {
                                    isQuizFinished = true
                                    onQuizComplete(correctAnswersCount, questions.size)
                                }
                            }
                            .padding(horizontal = 24.dp)
                            .testTag("next_question_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentIndex < questions.size - 1) "Next Question →" else "View Results 🏆",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
