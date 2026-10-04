package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GeminiService
import com.example.data.AllSubjects
import com.example.data.FlashcardSet
import com.example.data.PaperSet
import com.example.data.QuizSet
import com.example.data.SubjectItem
import com.example.ui.screens.study.FlashcardViewer
import com.example.ui.screens.study.PaperViewer
import com.example.ui.screens.study.QuizViewer
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.IndigoGradientBrush
import com.example.ui.theme.TealAccent
import kotlinx.coroutines.launch

enum class StudyMode(val label: String, val emoji: String) {
    FLASHCARDS("Flashcards", "🗂️"),
    QUIZ("Quiz", "📝"),
    PAPER("Paper", "📄")
}

@Composable
fun StudyScreen(
    flashcardSets: List<FlashcardSet>,
    quizSets: List<QuizSet>,
    paperSets: List<PaperSet>,
    soundEnabled: Boolean,
    onSaveFlashcards: (FlashcardSet) -> Unit,
    onDeleteFlashcards: (String) -> Unit,
    onSaveQuiz: (QuizSet) -> Unit,
    onDeleteQuiz: (String) -> Unit,
    onQuizCompleted: (correctCount: Int, totalCount: Int) -> Unit,
    onSavePaper: (PaperSet) -> Unit,
    onDeletePaper: (String) -> Unit,
    onFlashcardGenerated: () -> Unit,
    onQuizGenerated: () -> Unit,
    onPaperGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(StudyMode.FLASHCARDS) }
    var selectedSubject by remember { mutableStateOf(AllSubjects.first()) }
    var notesInput by remember { mutableStateOf("") }
    var standardInput by remember { mutableStateOf("Grade 10") }
    var chaptersInput by remember { mutableStateOf("") }
    var totalMarks by remember { mutableIntStateOf(50) }
    var isGenerating by remember { mutableStateOf(false) }

    // Active viewer overlays
    var activeFlashcardSet by remember { mutableStateOf<FlashcardSet?>(null) }
    var activeQuizSet by remember { mutableStateOf<QuizSet?>(null) }
    var activePaperSet by remember { mutableStateOf<PaperSet?>(null) }

    val coroutineScope = rememberCoroutineScope()

    if (activeFlashcardSet != null) {
        FlashcardViewer(
            flashcardSet = activeFlashcardSet!!,
            onUpdateSet = { updated ->
                activeFlashcardSet = updated
                onSaveFlashcards(updated)
            },
            onClose = { activeFlashcardSet = null }
        )
        return
    }

    if (activeQuizSet != null) {
        QuizViewer(
            quizSet = activeQuizSet!!,
            soundEnabled = soundEnabled,
            onQuizComplete = { correct, total ->
                onQuizCompleted(correct, total)
            },
            onUpdateSet = { updated ->
                activeQuizSet = updated
                onSaveQuiz(updated)
            },
            onClose = { activeQuizSet = null }
        )
        return
    }

    if (activePaperSet != null) {
        PaperViewer(
            paperSet = activePaperSet!!,
            onUpdatePaper = { updated ->
                activePaperSet = updated
                onSavePaper(updated)
            },
            onClose = { activePaperSet = null }
        )
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 760.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Mode Selector Pill Chips
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudyMode.values().forEach { mode ->
                        val isSelected = mode == selectedMode
                        Box(
                            modifier = Modifier
                                .testTag("study_mode_${mode.name.lowercase()}")
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) DeepIndigo else MaterialTheme.colorScheme.background)
                                .border(
                                    1.dp,
                                    if (isSelected) DeepIndigo else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedMode = mode }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${mode.emoji} ${mode.label}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // Two-column layout on wide screens, stacked on compact screens
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Left Column: Generator Panel
                    Box(modifier = Modifier.weight(1.1f)) {
                        GeneratorPanel(
                            mode = selectedMode,
                            selectedSubject = selectedSubject,
                            onSubjectChange = { selectedSubject = it },
                            notesInput = notesInput,
                            onNotesChange = { notesInput = it },
                            standardInput = standardInput,
                            onStandardChange = { standardInput = it },
                            chaptersInput = chaptersInput,
                            onChaptersChange = { chaptersInput = it },
                            totalMarks = totalMarks,
                            onTotalMarksChange = { totalMarks = it },
                            isGenerating = isGenerating,
                            onGenerate = {
                                isGenerating = true
                                coroutineScope.launch {
                                    when (selectedMode) {
                                        StudyMode.FLASHCARDS -> {
                                            val cards = GeminiService.generateFlashcards(
                                                notes = notesInput,
                                                subject = selectedSubject.name
                                            )
                                            val newSet = FlashcardSet(
                                                title = "${selectedSubject.name} Flashcards",
                                                subject = selectedSubject.id,
                                                cards = cards
                                            )
                                            onSaveFlashcards(newSet)
                                            onFlashcardGenerated()
                                            activeFlashcardSet = newSet
                                        }
                                        StudyMode.QUIZ -> {
                                            val questions = GeminiService.generateQuiz(
                                                notes = notesInput,
                                                subject = selectedSubject.name
                                            )
                                            val newSet = QuizSet(
                                                title = "${selectedSubject.name} Quiz",
                                                subject = selectedSubject.id,
                                                questions = questions
                                            )
                                            onSaveQuiz(newSet)
                                            onQuizGenerated()
                                            activeQuizSet = newSet
                                        }
                                        StudyMode.PAPER -> {
                                            val content = GeminiService.generatePaper(
                                                subject = selectedSubject.name,
                                                standard = standardInput,
                                                chapters = chaptersInput,
                                                totalMarks = totalMarks
                                            )
                                            val newSet = PaperSet(
                                                title = "${selectedSubject.name} Examination Paper",
                                                subject = selectedSubject.id,
                                                standard = standardInput,
                                                chapters = chaptersInput,
                                                totalMarks = totalMarks,
                                                content = content
                                            )
                                            onSavePaper(newSet)
                                            onPaperGenerated()
                                            activePaperSet = newSet
                                        }
                                    }
                                    isGenerating = false
                                }
                            }
                        )
                    }

                    // Right Column: My Library Grid
                    Box(modifier = Modifier.weight(0.9f)) {
                        LibraryPanel(
                            flashcardSets = flashcardSets,
                            quizSets = quizSets,
                            paperSets = paperSets,
                            onOpenFlashcards = { activeFlashcardSet = it },
                            onDeleteFlashcards = onDeleteFlashcards,
                            onOpenQuiz = { activeQuizSet = it },
                            onDeleteQuiz = onDeleteQuiz,
                            onOpenPaper = { activePaperSet = it },
                            onDeletePaper = onDeletePaper
                        )
                    }
                }
            } else {
                // Stacked single column for compact viewports
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        GeneratorPanel(
                            mode = selectedMode,
                            selectedSubject = selectedSubject,
                            onSubjectChange = { selectedSubject = it },
                            notesInput = notesInput,
                            onNotesChange = { notesInput = it },
                            standardInput = standardInput,
                            onStandardChange = { standardInput = it },
                            chaptersInput = chaptersInput,
                            onChaptersChange = { chaptersInput = it },
                            totalMarks = totalMarks,
                            onTotalMarksChange = { totalMarks = it },
                            isGenerating = isGenerating,
                            onGenerate = {
                                isGenerating = true
                                coroutineScope.launch {
                                    when (selectedMode) {
                                        StudyMode.FLASHCARDS -> {
                                            val cards = GeminiService.generateFlashcards(
                                                notes = notesInput,
                                                subject = selectedSubject.name
                                            )
                                            val newSet = FlashcardSet(
                                                title = "${selectedSubject.name} Flashcards",
                                                subject = selectedSubject.id,
                                                cards = cards
                                            )
                                            onSaveFlashcards(newSet)
                                            onFlashcardGenerated()
                                            activeFlashcardSet = newSet
                                        }
                                        StudyMode.QUIZ -> {
                                            val questions = GeminiService.generateQuiz(
                                                notes = notesInput,
                                                subject = selectedSubject.name
                                            )
                                            val newSet = QuizSet(
                                                title = "${selectedSubject.name} Quiz",
                                                subject = selectedSubject.id,
                                                questions = questions
                                            )
                                            onSaveQuiz(newSet)
                                            onQuizGenerated()
                                            activeQuizSet = newSet
                                        }
                                        StudyMode.PAPER -> {
                                            val content = GeminiService.generatePaper(
                                                subject = selectedSubject.name,
                                                standard = standardInput,
                                                chapters = chaptersInput,
                                                totalMarks = totalMarks
                                            )
                                            val newSet = PaperSet(
                                                title = "${selectedSubject.name} Examination Paper",
                                                subject = selectedSubject.id,
                                                standard = standardInput,
                                                chapters = chaptersInput,
                                                totalMarks = totalMarks,
                                                content = content
                                            )
                                            onSavePaper(newSet)
                                            onPaperGenerated()
                                            activePaperSet = newSet
                                        }
                                    }
                                    isGenerating = false
                                }
                            }
                        )
                    }

                    item {
                        LibraryPanel(
                            flashcardSets = flashcardSets,
                            quizSets = quizSets,
                            paperSets = paperSets,
                            onOpenFlashcards = { activeFlashcardSet = it },
                            onDeleteFlashcards = onDeleteFlashcards,
                            onOpenQuiz = { activeQuizSet = it },
                            onDeleteQuiz = onDeleteQuiz,
                            onOpenPaper = { activePaperSet = it },
                            onDeletePaper = onDeletePaper
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratorPanel(
    mode: StudyMode,
    selectedSubject: SubjectItem,
    onSubjectChange: (SubjectItem) -> Unit,
    notesInput: String,
    onNotesChange: (String) -> Unit,
    standardInput: String,
    onStandardChange: (String) -> Unit,
    chaptersInput: String,
    onChaptersChange: (String) -> Unit,
    totalMarks: Int,
    onTotalMarksChange: (Int) -> Unit,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = when (mode) {
                    StudyMode.FLASHCARDS -> "AI Flashcards Generator (+10 XP)"
                    StudyMode.QUIZ -> "AI Quiz Generator (+10 XP)"
                    StudyMode.PAPER -> "AI Exam Paper Generator (+12 XP)"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Subject Pill Chips
            Text(
                text = "Select Subject:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AllSubjects.forEach { subj ->
                    val isSel = subj.id == selectedSubject.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) TealAccent else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSel) TealAccent else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .clickable { onSubjectChange(subj) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${subj.emoji} ${subj.name}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (mode == StudyMode.PAPER) {
                // Paper mode inputs
                OutlinedTextField(
                    value = standardInput,
                    onValueChange = onStandardChange,
                    label = { Text("Standard / Class / Grade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = chaptersInput,
                    onValueChange = onChaptersChange,
                    label = { Text("Chapters / Syllabus Topics") },
                    placeholder = { Text("e.g. Thermodynamics, Kinetic Theory, Ideal Gases") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total Marks:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(25, 50, 100).forEach { marks ->
                        val isSel = marks == totalMarks
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) AmberAccent else MaterialTheme.colorScheme.background)
                                .border(1.dp, if (isSel) AmberAccent else MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                                .clickable { onTotalMarksChange(marks) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$marks Marks",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color(0xFF22263A) else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            } else {
                // Flashcards or Quiz mode: Large textarea for pasted notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = onNotesChange,
                    placeholder = {
                        Text(
                            if (mode == StudyMode.FLASHCARDS)
                                "Paste your textbook notes, lecture summaries, or article snippets here to create interactive flashcards..."
                            else
                                "Paste your study notes here to generate an active recall multiple-choice quiz..."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("study_notes_textarea"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Generate Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isGenerating) androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline) else IndigoGradientBrush)
                    .clickable(enabled = !isGenerating) { onGenerate() }
                    .testTag("generate_study_set_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Gemini AI is crafting your study set...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = when (mode) {
                            StudyMode.FLASHCARDS -> "Generate Flashcards ✨"
                            StudyMode.QUIZ -> "Generate Quiz ✨"
                            StudyMode.PAPER -> "Generate Exam Paper ✨"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryPanel(
    flashcardSets: List<FlashcardSet>,
    quizSets: List<QuizSet>,
    paperSets: List<PaperSet>,
    onOpenFlashcards: (FlashcardSet) -> Unit,
    onDeleteFlashcards: (String) -> Unit,
    onOpenQuiz: (QuizSet) -> Unit,
    onDeleteQuiz: (String) -> Unit,
    onOpenPaper: (PaperSet) -> Unit,
    onDeletePaper: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${flashcardSets.size + quizSets.size + paperSets.size} sets",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (flashcardSets.isEmpty() && quizSets.isEmpty() && paperSets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📚", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No saved study sets yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Generate flashcards, quizzes, or papers to build your library!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Flashcards
                    flashcardSets.forEach { set ->
                        LibraryItemRow(
                            emoji = "🗂️",
                            title = set.title,
                            subtitle = "${set.cards.size} Cards • ${set.subject.replaceFirstChar { it.uppercase() }}",
                            onOpen = { onOpenFlashcards(set) },
                            onDelete = { onDeleteFlashcards(set.id) }
                        )
                    }

                    // Quizzes
                    quizSets.forEach { set ->
                        LibraryItemRow(
                            emoji = "📝",
                            title = set.title,
                            subtitle = "${set.questions.size} Questions • ${set.subject.replaceFirstChar { it.uppercase() }}",
                            onOpen = { onOpenQuiz(set) },
                            onDelete = { onDeleteQuiz(set.id) }
                        )
                    }

                    // Papers
                    paperSets.forEach { set ->
                        LibraryItemRow(
                            emoji = "📄",
                            title = set.title,
                            subtitle = "${set.standard} • ${set.totalMarks} Marks",
                            onOpen = { onOpenPaper(set) },
                            onDelete = { onDeletePaper(set.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryItemRow(
    emoji: String,
    title: String,
    subtitle: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable { onOpen() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
